package org.fasd.views.actions;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.views.dialogs.DialogXMLColumnBrowser;
import org.freeolap.FreemetricsPlugin;

import xmldesigner.internal.DimensionTree;
import xmldesigner.internal.TreeParent;
import xmldesigner.parse.XMLParser;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class ActionBrowseColumn extends Action {
	private DataObjectItem item;
	private int max;
	private List<String> values = new ArrayList<String>();
	private List<String> distincts = new ArrayList<String>();
	private Shell shell;
	private DataSourceConnection sock;

	public ActionBrowseColumn(DataObjectItem col, int nbLines) {
		this.item = col;
		this.max = nbLines;
		shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		sock = col.getParent().getDataSource().getDriver();
	}

	public void run() {
		if (item.getParent() instanceof DataObjectOda) {
			HashMap<String, Integer> distinctCount = new HashMap<String, Integer>();
			try {
				DataObjectOda odaTable = (DataObjectOda) item.getParent();

				OdaInput input = new OdaInput();
				input.setDatasetPublicProperties(odaTable.getPublicProperties());
				input.setDatasetPrivateProperties(odaTable.getPrivateProperties());
				input.setDatasourcePublicProperties(((DatasourceOda) odaTable.getDataSource()).getPublicProperties());
				input.setDatasourcePrivateProperties(((DatasourceOda) odaTable.getDataSource()).getPrivateProperties());

				input.setOdaExtensionDataSourceId(((DatasourceOda) odaTable.getDataSource()).getOdaDatasourceExtensionId());
				input.setOdaExtensionId(((DatasourceOda) odaTable.getDataSource()).getOdaExtensionId());

				input.setQueryText(odaTable.getQueryText());

				input.setName(((DatasourceOda) odaTable.getDataSource()).getOdaDatasourceExtensionId());

				IQuery query = QueryHelper.buildquery(input);

				query.setMaxRows(max);

				IResultSet rs = query.executeQuery();

				while (rs.next()) {
					try {
						String val = rs.getString(item.getName());
						values.add(val);
						if (!distinctCount.keySet().contains(val)) {
							distinctCount.put(val, 1);
						} else {
							distinctCount.put(val, distinctCount.get(val) + 1);
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(shell, LanguageText.ActionBrowseColumn_0, e.getMessage());
						rs.close();
						query.close();
						QueryHelper.closeConnectionFor(query);
						QueryHelper.removeQuery(query);
						break;
					}
				}
				rs.close();
				query.close();
				QueryHelper.closeConnectionFor(query);
				QueryHelper.removeQuery(query);
			} catch (OdaException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (String key : distinctCount.keySet()) {
				String val = key + ";" + distinctCount.get(key); //$NON-NLS-1$
				distincts.add(val);
			}

		}

		else if (item.getParent().getDataSource().getDriver().getType().equals("XML")) { //$NON-NLS-1$
			String url = "file:///" + sock.getTransUrl(); //$NON-NLS-1$

			XMLParser parser = new XMLParser(url);
			parser.parser();
			DataXML dtd = parser.getDataXML();
			Xpath xpath = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());

			DimensionTree model = new DimensionTree(dtd);
			TreeParent root = model.createModel();
			xpath.setListHiera(createHiera(root));
			xpath.addCol("count"); //$NON-NLS-1$
			xpath.addCol(item.getOrigin());

			// requete distinct
			String distinctQuery = "for $i in distinct-values(doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + item.getOrigin() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			distinctQuery += "let $p:=count(doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + dtd.getRoot().getElement(0).getName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			distinctQuery += "[" + item.getOrigin() + "=$i])\n"; //$NON-NLS-1$ //$NON-NLS-2$
			distinctQuery += "return\n"; //$NON-NLS-1$

			distinctQuery += "<" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
			distinctQuery += "<count>\n"; //$NON-NLS-1$
			distinctQuery += "{$p}\n"; //$NON-NLS-1$
			distinctQuery += "</count>\n"; //$NON-NLS-1$
			distinctQuery += "<" + item.getOrigin() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
			distinctQuery += "{$i}\n"; //$NON-NLS-1$
			distinctQuery += "</" + item.getOrigin() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$

			distinctQuery += "</" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$

			try {
				xpath.executeXquery(distinctQuery);
				xpath.modifieSortie();

				XMLParser pars = new XMLParser("Temp/sortie.xml"); //$NON-NLS-1$
				pars.parser();
				DataXML dtd2 = pars.getDataXML();
				List<String> list = xpath.listXquery(dtd2.getRoot());

				// second list for all values
				Xpath xpathAll = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());
				xpathAll.setListHiera(createHiera(root));
				xpathAll.addCol(item.getOrigin());
				distinctQuery = "for $i in doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + item.getOrigin() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				distinctQuery += "return\n"; //$NON-NLS-1$
				distinctQuery += "<" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
				distinctQuery += "{$i}\n"; //$NON-NLS-1$

				distinctQuery += "</" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println(distinctQuery);
				xpathAll.executeXquery(distinctQuery);
				xpathAll.modifieSortie();

				pars = new XMLParser("Temp/sortie.xml"); //$NON-NLS-1$
				pars.parser();
				dtd2 = pars.getDataXML();
				List<String> listAll = xpathAll.listXquery(dtd2.getRoot());

				DialogXMLColumnBrowser dial = new DialogXMLColumnBrowser(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.ActionBrowseColumn_Content_of + item.getParent().getName() + "." + item.getName(), //$NON-NLS-1$
						xpath, list, xpathAll, listAll);
				dial.open();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			VanillaPreparedStatement stmt;
			// all values
			try {
				if (sock.getDriver().equals("")) //$NON-NLS-1$
					throw new SQLException(LanguageText.ActionBrowseColumn_Driver_Name);
				if (sock.getConnection() == null)
					sock.connectAll();

				stmt = sock.getConnection().getConnection().createStatement();
				stmt.setMaxRows(max);
				ResultSet rs = stmt.executeQuery("SELECT " + item.getOrigin() + " FROM " + item.getParent().getPhysicalName()); //$NON-NLS-1$ //$NON-NLS-2$

				while (rs.next()) {
					values.add(rs.getString(1));
				}
				rs.close();

				// count
				rs = stmt.executeQuery("SELECT " + item.getOrigin() + ", COUNT(" + item.getOrigin() + ") FROM " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						item.getParent().getPhysicalName() + " GROUP BY " + item.getOrigin()); //$NON-NLS-1$
				while (rs.next()) {
					distincts.add(rs.getString(1) + ";" + rs.getString(2)); //$NON-NLS-1$

				}
				rs.close();

				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(sock.getConnection().getConnection());
			} catch (SQLException e) {
				MessageDialog.openError(shell, LanguageText.ActionBrowseColumn_Error, LanguageText.ActionBrowseColumn_Check_DS_Conn + e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				MessageDialog.openError(shell, LanguageText.ActionBrowseColumn_Error, LanguageText.ActionBrowseColumn_unable_Load);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				MessageDialog.openError(shell, LanguageText.ActionBrowseColumn_Error, LanguageText.ActionBrowseColumn_File_Not_Found);
				e.printStackTrace();
			} catch (Exception e) {
				MessageDialog.openError(shell, LanguageText.ActionBrowseColumn_Error, ""); //$NON-NLS-1$
				e.printStackTrace();
			}

		}
	}

	public List<String> getDistincts() {
		return distincts;
	}

	public List<String> getValues() {
		return values;
	}

	/**
	 * Creation a list hierarchic of tags of file xml
	 * 
	 * @param t
	 * @return
	 */
	private ArrayList<String> createHiera(TreeParent t) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(t.getName());
		for (int i = 0; i < t.getChildren().length; i++) {
			add(list, createHiera((TreeParent) t.getChildren(i)));
		}
		return list;
	}

	private List<String> add(List<String> list1, List<String> list2) {
		for (int i = 0; i < list2.size(); i++) {
			list1.add(list2.get(i));
		}
		return list1;
	}
}
