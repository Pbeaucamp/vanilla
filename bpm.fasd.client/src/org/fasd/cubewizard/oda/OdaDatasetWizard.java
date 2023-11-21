package org.fasd.cubewizard.oda;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.NewDataSourceWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;
import org.freeolap.FreemetricsPlugin;

import bpm.dataprovider.odainput.consumer.ConnectionManager;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.beans.data.OdaInput;

import com.enterprisedt.util.debug.Logger;

public class OdaDatasetWizard extends NewDataSourceWizard implements INewWizard {

	private DataObjectOda dataset;
	private OdaDatasourceSelectionPage selectionPage;
	private OdaDatasetContentPage contentPage;
	private boolean isEdit;
	private String previousName;

	public OdaDatasetWizard(DataObjectOda dataset) {
		this.dataset = dataset;
	}

	@Override
	public void addPages() {
		selectionPage = new OdaDatasourceSelectionPage("Selection page", dataset); //$NON-NLS-1$
		selectionPage.setTitle(LanguageText.OdaDatasetWizard_1);
		selectionPage.setDescription(LanguageText.OdaDatasetWizard_2);

		addPage(selectionPage);

		contentPage = new OdaDatasetContentPage("contentPage", dataset); //$NON-NLS-1$
		contentPage.setTitle(LanguageText.OdaDatasetWizard_4);
		addPage(contentPage);
	}

	@Override
	public boolean performFinish() {
		try {

			if (dataset != null && dataset.getDataSource() != null) {
				dataset.getDataSource().removeDataObject(dataset);
				dataset.getColumns().clear();
				isEdit = true;
				previousName = dataset.getName();
			}

			DataSetWizardPage p = (DataSetWizardPage) selectionPage.getDataSetPage();
			DataSetDesign dd = ((DataSetWizard) p.getWizard()).getResponseSession().getResponseDataSetDesign();
			java.util.Properties pp = new java.util.Properties();
			if (dd.getPrivateProperties() != null) {
				for (Object o : dd.getPrivateProperties().getProperties()) {

					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pp.setProperty(pName, pValue);
					}
				}
			}
			java.util.Properties pu = new java.util.Properties();
			if (dd.getPublicProperties() != null) {
				for (Object o : dd.getPublicProperties().getProperties()) {
					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pu.setProperty(pName, pValue);
					}

				}
			}
			dataset.setPublicProperties(pu);
			dataset.setPrivateProperties(pp);
			dataset.setName(selectionPage.getDataSetName());
			dataset.setOdaDatasetExtensionId(dd.getOdaExtensionDataSetId());

			DatasourceOda con = selectionPage.getDataSourceSelected();
			con.addDataObject(dataset);

			String query = dd.getQueryText();
			dataset.setDataSource(con);
			dataset.setQueryText(query);

			fillDataObject();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void fillDataObject() throws Exception {

		OdaInput input = new OdaInput();
		input.setDatasetPublicProperties(dataset.getPublicProperties());
		input.setDatasetPrivateProperties(dataset.getPrivateProperties());
		input.setDatasourcePublicProperties(((DatasourceOda) dataset.getDataSource()).getPublicProperties());
		input.setDatasourcePrivateProperties(((DatasourceOda) dataset.getDataSource()).getPrivateProperties());

		input.setOdaExtensionDataSourceId(((DatasourceOda) dataset.getDataSource()).getOdaDatasourceExtensionId());
		input.setOdaExtensionId(((DatasourceOda) dataset.getDataSource()).getOdaExtensionId());

		input.setQueryText(dataset.getQueryText());

		input.getDatasourcePublicProperties().put("odaAutoCommit", "false"); //$NON-NLS-1$ //$NON-NLS-2$

		input.setName(((DatasourceOda) dataset.getDataSource()).getOdaDatasourceExtensionId());
		if (ConnectionManager.getOpenedConnectionsForInput(input).size() > 0) {
			for (IConnection c : ConnectionManager.getOpenedConnectionsForInput(input)) {
				c.close();
			}
		}
		ConnectionManager.openConnection(input);
		IQuery query = QueryHelper.buildquery(input);

		query.setMaxRows(1);

		query.setProperty("rowFetchSize", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$

		IResultSet res = null;

		try {
			res = query.executeQuery();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).warn(LanguageText.OdaDatasetWizard_9);

			query.setProperty("rowFetchSize", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$
			res = query.executeQuery();
		}

		IResultSetMetaData rsMeta = res.getMetaData();

		for (int i = 1; i <= rsMeta.getColumnCount(); i++) {

			DataObjectItem item = new DataObjectItem();

			String label = rsMeta.getColumnLabel(i);
			String name = rsMeta.getColumnName(i);
//			if(name == null || name.isEmpty()) {
//				name = ;
//			}

			item.setName(label != null ? label : name);
			item.setParent(dataset);
			item.setOrigin(name);
			item.setSqlType(rsMeta.getColumnTypeName(i));
			item.setClasse(findClasse(rsMeta.getColumnTypeName(i)));

			dataset.addDataObjectItem(item);
		}

		if (isEdit) {
			rebindElements();
		}
	}

	private void rebindElements() {
		// rebind Relations
		List<OLAPRelation> toRemove = new ArrayList<OLAPRelation>();
		for (OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()) {
			boolean leftFound = true;
			boolean rightFound = true;
			if (r.getLeftObject() != null && r.getLeftObject().getName().equals(previousName)) {
				leftFound = false;
				for (DataObjectItem i : dataset.getColumns()) {
					if (i.getName().equals(r.getLeftObjectItem().getName())) {
						r.setLeftObjectItem(i);
						leftFound = true;
						break;
					}
				}
			}
			if (r.getRightObject() != null && r.getRightObject().getName().equals(previousName)) {
				rightFound = false;
				for (DataObjectItem i : dataset.getColumns()) {
					if (i.getName().equals(r.getRightObjectItem().getName())) {
						r.setRightObjectItem(i);
						rightFound = true;
						break;
					}
				}
			}
			if (!rightFound || !leftFound) {
				toRemove.add(r);
			} else {
				System.out.println();
			}
		}

		if (!toRemove.isEmpty()) {

			StringBuilder b = new StringBuilder();
			b.append(LanguageText.OdaDatasetWizard_12);
			for (OLAPRelation r : toRemove) {
				FreemetricsPlugin.getDefault().getFAModel().removeRelation(r);
				b.append(" - relation " + r.getName() + LanguageText.OdaDatasetWizard_14); //$NON-NLS-1$
			}
			MessageDialog.openInformation(getShell(), LanguageText.OdaDatasetWizard_15, b.toString());
		}

		// rebind dimensions
		for (OLAPDimension dim : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
			for (OLAPHierarchy hiera : dim.getHierarchies()) {
				for (OLAPLevel lvl : hiera.getLevels()) {
					if (lvl.getItem() != null && lvl.getItem().getParent().getName().equals(previousName)) {
						lvl.setItem(findItem(lvl.getItem()));
					}
					if (lvl.getOrderItem() != null && lvl.getOrderItem().getParent().getName().equals(previousName)) {
						lvl.setOrderItem(findItem(lvl.getOrderItem()));
					}
					if (lvl.getClosureChildCol() != null && lvl.getClosureChildCol().getParent().getName().equals(previousName)) {
						lvl.setClosureChildCol(findItem(lvl.getClosureChildCol()));
					}
					if (lvl.getClosureParentCol() != null && lvl.getClosureParentCol().getParent().getName().equals(previousName)) {
						lvl.setClosureParentCol(findItem(lvl.getClosureParentCol()));
					}
					if (lvl.getLabel() != null && lvl.getLabel().getParent().getName().equals(previousName)) {
						lvl.setLabel(findItem(lvl.getLabel()));
					}

					for (org.fasd.olap.Property p : lvl.getProperties()) {
						if (p.getColumn() != null && p.getColumn().getParent().getName().equals(previousName)) {
							p.setColumn(findItem(lvl.getLabel()));
						}

					}

				}
			}
		}

		// rebindMeasures
		for (OLAPMeasure mes : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()) {
			if (mes instanceof OlapDynamicMeasure) {
				OlapDynamicMeasure dynMes = (OlapDynamicMeasure) mes;
				for (IMeasureAggregation agg : dynMes.getAggregations()) {
					if (agg instanceof LastAggregation) {
						LastAggregation lastagg = (LastAggregation) agg;
						if (lastagg.getOrigin() != null && lastagg.getOrigin().getParent().getName().equals(previousName)) {
							lastagg.setOrigin(findItem(lastagg.getOrigin()));
						}
					} else if (agg instanceof ClassicAggregation) {
						ClassicAggregation lastagg = (ClassicAggregation) agg;
						if (lastagg.getOrigin() != null && lastagg.getOrigin().getParent().getName().equals(previousName)) {
							lastagg.setOrigin(findItem(lastagg.getOrigin()));
						}
					}
				}
			} else {
				if (mes.getOrigin() != null && mes.getOrigin().getParent().getName().equals(previousName)) {
					mes.setOrigin(findItem(mes.getOrigin()));
				}
			}
		}

	}

	private DataObjectItem findItem(DataObjectItem item) {
		if (item == null) {
			return null;
		}
		for (DataObjectItem newItem : dataset.getColumns()) {
			if (newItem.getName().equals(item.getName())) {
				return newItem;
			}
		}

		return null;
	}

	private String findClasse(String columnTypeName) {

		if (columnTypeName.equalsIgnoreCase("String") || columnTypeName.equalsIgnoreCase("VARCHAR") || columnTypeName.equalsIgnoreCase("VARCHAR2")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return "java.lang.String"; //$NON-NLS-1$
		} else if (columnTypeName.equalsIgnoreCase("Integer") || columnTypeName.equalsIgnoreCase("INT")) { //$NON-NLS-1$ //$NON-NLS-2$
			return "java.lang.Integer"; //$NON-NLS-1$
		} else if (columnTypeName.equalsIgnoreCase("BigDecimal")) { //$NON-NLS-1$
			return "java.math.BigDecimal"; //$NON-NLS-1$
		} else if (columnTypeName.equalsIgnoreCase("Timestamp") || columnTypeName.equalsIgnoreCase("Date")) { //$NON-NLS-1$ //$NON-NLS-2$
			return "java.util.Date"; //$NON-NLS-1$
		} else if (columnTypeName.equalsIgnoreCase("Double") || columnTypeName.equalsIgnoreCase("NUMBER")) { //$NON-NLS-1$ //$NON-NLS-2$
			return "java.lang.Double"; //$NON-NLS-1$
		}
		return "java.lang.Long"; //$NON-NLS-1$
	}

	@Override
	public boolean canFinish() {
		boolean b = super.canFinish();

		if (b) {
			if (selectionPage != null) {
				IWizardPage p = selectionPage.getNextPage();

				if (p.getControl() != null && !p.isPageComplete()) {
					return false;
				}

				while (p.getNextPage() != null && p.getNextPage() != p) {
					p = p.getNextPage();

					if (!p.isPageComplete()) {
						return false;
					}
				}

			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizard#getNextPage
	 * (org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return null;
	}
}
