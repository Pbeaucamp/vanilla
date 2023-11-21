package org.fasd.views.actions;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataInline;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataRow;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.composites.DialogTableBrowser;
import org.fasd.views.dialogs.DialogXMLBrowser;
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

import com.enterprisedt.util.debug.Logger;

public class ActionBrowseTable extends Action {
	private DataObject table;
	private DataSourceConnection sock;
	private Shell parent;
	private int max;

	public ActionBrowseTable(Shell parent, TreeTable table2, int nblines) {
		super(LanguageText.ActionBrowseTable_Browse_Table);
		this.sock = table2.getTable().getDataSource().getDriver();
		table = table2.getTable();

		this.parent = parent;
		this.max = nblines;
	}

	public ActionBrowseTable(Shell parent, DataObject table, int nblines) {
		super(LanguageText.ActionBrowseTable_Browse_Table);
		this.sock = table.getDataSource().getDriver();
		this.table = table;

		this.parent = parent;
		this.max = nblines;
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

	public void run() {
		String requete = table.getSelectStatement();

		if (table instanceof DataObjectOda) {

			try {
				DataObjectOda odaTable = (DataObjectOda) table;

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

				if (input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) { //$NON-NLS-1$
					if (((String) input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) { //$NON-NLS-1$ //$NON-NLS-2$
						query.setProperty("rowFetchSize", Integer.MIN_VALUE + ""); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						query.setProperty("rowFetchSize", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				query.setMaxRows(max);

				IResultSet rs = null;
				try {
					rs = query.executeQuery();
				} catch (Exception ex) {
					Logger.getLogger(getClass()).warn(ex.getMessage() + LanguageText.ActionBrowseTable_7);
					query.setProperty("rowFetchSize", "0"); //$NON-NLS-1$ //$NON-NLS-2$
					rs = query.executeQuery();
				}

				ArrayList<String[]> tableContent = new ArrayList<String[]>();
				while (rs.next()) {
					try {
						int nCols = table.getColumns().size();
						String[] row = new String[nCols];
						for (int i = 0; i < nCols; i++) {
							row[i] = rs.getString(i + 1);

						}
						tableContent.add(row);
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(parent, LanguageText.ActionBrowseTable_10, LanguageText.ActionBrowseTable_11 + e.getMessage());
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
				DialogTableBrowser dial = new DialogTableBrowser(parent, table.getColumns(), tableContent, table.getName());
				dial.open();

			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(parent, LanguageText.ActionBrowseTable_12, LanguageText.ActionBrowseTable_13 + e.getMessage());

			}

		}

		else if (sock.getType().equals("XML")) { //$NON-NLS-1$
			String url = "file:///" + sock.getTransUrl(); //$NON-NLS-1$

			XMLParser parser = new XMLParser(url);
			parser.parser();
			DataXML dtd = parser.getDataXML();
			Xpath xpath = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());
			System.out.println(requete);
			DimensionTree model = new DimensionTree(dtd);
			TreeParent root = model.createModel();
			xpath.setListHiera(createHiera(root));

			try {
				xpath.executeXquery(requete);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			for (DataObjectItem it : table.getColumns()) {
				xpath.addCol(it.getOrigin());
			}
			xpath.modifieSortie();

			try {
				XMLParser pars = new XMLParser("Temp/sortie.xml"); //$NON-NLS-1$
				pars.parser();
				DataXML dtd2 = pars.getDataXML();
				List<String> list = xpath.listXquery(dtd2.getRoot());
				DialogXMLBrowser dial = new DialogXMLBrowser(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), xpath, list);
				dial.open();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (!table.isInline()) {
			VanillaPreparedStatement stmt;

			try {
				if (sock.getDriver().equals("")) //$NON-NLS-1$
					throw new SQLException(LanguageText.ActionBrowseTable_Driver_No_set);
				sock.connectAll();

				stmt = sock.getConnection().getConnection().createStatement();
				stmt.setMaxRows(max);
				ResultSet rs = stmt.executeQuery(requete);

				ArrayList<String[]> tableContent = new ArrayList<String[]>();
				while (rs.next()) {
					int nCols = table.getColumns().size();
					String[] row = new String[nCols];
					for (int i = 0; i < nCols; i++) {
						row[i] = rs.getString(i + 1);

					}
					tableContent.add(row);
				}
				rs.close();
				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(sock.getConnection().getConnection());

				if (tableContent.size() > 0) {
					DialogTableBrowser dial = new DialogTableBrowser(parent, table.getColumns(), tableContent, table.getName());
					dial.open();
				} else
					MessageDialog.openInformation(parent, LanguageText.ActionBrowseTable_Information, LanguageText.ActionBrowseTable_Table + table.getName() + LanguageText.ActionBrowseTable_Is_empty);

			} catch (SQLException e) {
				MessageDialog.openError(parent, LanguageText.ActionBrowseTable_Error, LanguageText.ActionBrowseTable_Chek_DS + e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				MessageDialog.openError(parent, LanguageText.ActionBrowseTable_Error, LanguageText.ActionBrowseTable_unable_Load_Driver);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				MessageDialog.openError(parent, LanguageText.ActionBrowseTable_Error, LanguageText.ActionBrowseTable_File_Not_Found);
				e.printStackTrace();
			} catch (Exception e) {
				MessageDialog.openError(parent, LanguageText.ActionBrowseTable_Error, ""); //$NON-NLS-1$
				e.printStackTrace();
			}

		}
		// inlinetable
		else {
			ArrayList<String[]> tableContent = new ArrayList<String[]>();

			DataInline datas = table.getDatas();
			for (DataRow row : datas.getRows()) {
				if (tableContent.size() == max)
					break;

				String[] l = new String[table.getColumns().size()];
				int j = 0;
				for (DataObjectItem i : table.getColumns()) {
					l[j++] = row.getValue(i.getName());
				}
				tableContent.add(l);

			}

			if (tableContent.size() > 0) {
				DialogTableBrowser dial = new DialogTableBrowser(parent, table.getColumns(), tableContent, table.getName());
				dial.open();
			} else
				MessageDialog.openInformation(parent, LanguageText.ActionBrowseTable_Information, LanguageText.ActionBrowseTable_Table + table.getName() + LanguageText.ActionBrowseTable_Is_empty);

		}

	}
}
