package bpm.fwr.api.birt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.FilterCondition;
import org.eclipse.birt.report.model.api.elements.structures.OdaDataSetParameter;

import bpm.fwr.api.FwrReportManager;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.DynamicPrompt;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.Formula;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DatasHelper {

	public static String buildQuery(DataSet ds, IRepositoryApi sock, ReportDesignHandle designHandle, OdaDataSetHandle datasetHandle, String selectedLanguage, boolean isPreview) throws Exception {
		IBusinessModel model = null;
		IBusinessPackage pckg = null;
		try {
			model = getSelectedModel(sock, ds.getDatasource());
			pckg = model.getBusinessPackage(ds.getDatasource().getBusinessPackage(), ds.getDatasource().getGroup());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the selected package", e);
		}

		// find all the IDataStreamElements
		List<Column> columns = ds.getColumns();

		List<Formula> formulas = new ArrayList<Formula>();
		List<AggregateFormula> aggs = new ArrayList<AggregateFormula>();
		List<IDataStreamElement> iDataStreamElements = new ArrayList<IDataStreamElement>();
		for (Column col : columns) {
			if (!col.isCalculated()) {
				String tName = col.getBusinessTableParent();
				IBusinessTable t = pckg.getBusinessTable(ds.getDatasource().getGroup(), tName);
				if (t == null) {
					Collection<IBusinessTable> tables = pckg.getBusinessTables(ds.getDatasource().getGroup());
					Iterator<IBusinessTable> it = tables.iterator();
					while (it.hasNext() && t == null) {
						IBusinessTable table = it.next();
						t = lookUnder(table, tName, ds.getDatasource().getGroup());
					}
				}

				try {
					IDataStreamElement d = (IDataStreamElement) t.getColumn(ds.getDatasource().getGroup(), col.getBasicName());
					iDataStreamElements.add(d);
				} catch (GrantException e) {
					e.printStackTrace();
				}
			}
			else {
				// TODO Handle calculated columns
				if (col.isAgregate()) {

					Formula form = new Formula();
					form.setName(col.getName());
					form.setFormula(col.getFormula());
					form.setInvolvedDatastreams(col.getInvolvedDatastreams());
					// col.getFormula().substring(col.getFormula().indexOf("("))
					AggregateFormula f = new AggregateFormula("", form, col.getName());

					aggs.add(f);

				}
				else {
					Formula f = new Formula();
					f.setName(col.getName());
					f.setFormula(col.getFormula());
					f.setInvolvedDatastreams(col.getInvolvedDatastreams());
					formulas.add(f);
				}
			}
		}

		// add filters
		List<IFilter> filters = new ArrayList<IFilter>();
		// get metadata filters
		if (ds.getFilters() != null && !ds.getFilters().isEmpty()) {
			List<String> fi = ds.getFilters();
			for (String o : fi) {
				if (o.equalsIgnoreCase("")) {
					break;
				}
				filters.add((IFilter) pckg.getResourceByName(ds.getDatasource().getGroup(), o));
			}
		}
		// get fwrFilters
		int pos = 1;
		if (ds.getFwrFilters() != null && !ds.getFwrFilters().isEmpty()) {
			try {
				Collection<FWRFilter> fwrFilters = ds.getFwrFilters();
				for (Object a : fwrFilters) {
					FWRFilter f = (FWRFilter) a;
					if (f.getName() == null) {
						break;
					}
					FilterCondition cond = StructureFactory.createFilterCond();
					PropertyHandle propertyHandle = datasetHandle.getPropertyHandle(OdaDataSetHandle.FILTER_PROP);

					Column col = null;
					for (Column c : columns) {
						if (c.getBasicName().equals(f.getColumnName())) {
							col = c;
							break;
						}
					}
					cond.setExpr("dataSetRow[\"" + col.getDatasetRowExpr(selectedLanguage) + "\"]");
					cond.setValue1("\"" + f.getValues().get(0) + "\"");
					cond.setOperator(FwrReportManager.birtoperator(f.getOperator()));
					propertyHandle.addItem(cond);
				}
			} catch (Exception e) {
			}
		}

		if (filters.size() == 0) {
			filters = null;
		}

		// add prompts
		List<Prompt> prompts = new ArrayList<Prompt>();
		List<Prompt> dynamicPrompts = new ArrayList<Prompt>();

		if (ds.getPrompts() != null && !ds.getPrompts().isEmpty()) {
			try {
				PropertyHandle propertyHandle = datasetHandle.getPropertyHandle(OdaDataSetHandle.PARAMETERS_PROP);
				ElementFactory factory = designHandle.getElementFactory();
				for (Object o : ds.getPrompts()) {
					FwrPrompt selectedPrompt = null;
					IDataStreamElement selectedColumn = null;
					if (o instanceof DynamicPrompt) {
						DynamicPrompt dynPrompt = (DynamicPrompt) o;
						
						selectedColumn = getSelectedColumns(pckg, dynPrompt.getColumn().getBusinessTableParent(), dynPrompt.getColumn().getName(), ds.getDatasource().getGroup());

						Prompt dyn = new Prompt();
						dyn.setName(dynPrompt.getName());
						dyn.setOrigin(selectedColumn);
						dyn.setGotoDataStreamElement(selectedColumn);
						dyn.setOperator(dynPrompt.getOperator());
						dynamicPrompts.add(dyn);
						
						selectedPrompt = dynPrompt;
					}
					else if (o instanceof FwrPrompt) {
						FwrPrompt fp = (FwrPrompt) o;

						prompts.add((Prompt) pckg.getResourceByName(ds.getDatasource().getGroup(), fp.getName()));

						selectedPrompt = fp;
					}

					OdaDataSetParameter odaDataSetParameter = StructureFactory.createOdaDataSetParameter();
					odaDataSetParameter.setName(selectedPrompt.getName());
					odaDataSetParameter.setDataType(DesignChoiceConstants.PARAM_TYPE_STRING);
					odaDataSetParameter.setPosition(pos);
					odaDataSetParameter.setIsInput(true);
					odaDataSetParameter.setIsOutput(false);
					odaDataSetParameter.setParamName(selectedPrompt.getName());
					propertyHandle.addItem(odaDataSetParameter);

					pos++;

					if (selectedPrompt.getDataset() != null) {
						// We create the dataset for the parameter in case
						// we
						// save the report as BIRT
						DataSet dataset = selectedPrompt.getDataset();
						DataSource datasource = dataset.getDatasource();

						OdaDataSourceHandle dsHandle = designHandle.getElementFactory().newOdaDataSource(datasource.getName(), "bpm.metadata.birt.oda.runtime");
						dsHandle.setProperty("URL", datasource.getUrl());
						dsHandle.setProperty("USER", datasource.getUser());
						dsHandle.setProperty("PASSWORD", datasource.getPassword());
						dsHandle.setProperty("GROUP_NAME", datasource.getGroup());
						dsHandle.setProperty("IS_ENCRYPTED", datasource.isEncrypted() + "");
						dsHandle.setPrivateDriverProperty("CONNECTION_NAME", datasource.getConnectionName());
						dsHandle.setProperty("DIRECTORY_ITEM_ID", String.valueOf(datasource.getItemId()));
						dsHandle.setProperty("REPOSITORY_ID", String.valueOf(datasource.getRepositoryId()));
						dsHandle.setProperty("BUSINESS_MODEL", datasource.getBusinessModel());
						dsHandle.setProperty("BUSINESS_PACKAGE", datasource.getBusinessPackage());

						designHandle.getDataSources().add(dsHandle);

						OdaDataSetHandle dsetHandle = designHandle.getElementFactory().newOdaDataSet(dataset.getName(), "bpm.metadata.birt.oda.runtime.dataSet");
						dsetHandle.setDataSource(datasource.getName());

						// create the query
						String promptQuery = buildQuery(dataset, sock, designHandle, dsetHandle, selectedLanguage, isPreview);
						try {
							dsetHandle.setQueryText(promptQuery);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						// add the dataset to the report
						try {
							designHandle.getDataSets().add(dsetHandle);
						} catch (Exception e) {
							e.printStackTrace();
						}

						ScalarParameterHandle scalarParameterHandle = factory.newScalarParameter(selectedPrompt.getName());
						if (selectedPrompt.getType() == VanillaParameter.TEXT_BOX) {
							scalarParameterHandle.setControlType(DesignChoiceConstants.PARAM_CONTROL_TEXT_BOX);
						}
						else {
							scalarParameterHandle.setControlType(DesignChoiceConstants.PARAM_CONTROL_LIST_BOX);
						}
						scalarParameterHandle.setCategory("Unformatted");
						scalarParameterHandle.setValueType(DesignChoiceConstants.PARAM_VALUE_TYPE_DYNAMIC);
						scalarParameterHandle.setDataSetName(dataset.getName());
						if(o instanceof DynamicPrompt) {
							scalarParameterHandle.setValueExpr("dataSetRow[\"" + selectedColumn.getOriginName() + "\"]");
						}
						else {
							scalarParameterHandle.setValueExpr("dataSetRow[\"" + ((Prompt) pckg.getResourceByName(ds.getDatasource().getGroup(), selectedPrompt.getName())).getOriginDataStreamElementName() + "\"]");
						}
						scalarParameterHandle.setDistinct(true);

						List<String> values = new ArrayList<String>();
						values.add(selectedPrompt.getSelectedValuesToString(selectedPrompt.getParamType().equals(VanillaParameter.PARAM_TYPE_MULTI)));

						scalarParameterHandle.setDefaultValueList(values);
						designHandle.getParameters().add(scalarParameterHandle);
					}
					else {
						List<String> values = new ArrayList<String>();
						values.add(selectedPrompt.getSelectedValuesToString(selectedPrompt.getParamType().equals(VanillaParameter.PARAM_TYPE_MULTI)));
						
						ScalarParameterHandle scalarParameterHandle = factory.newScalarParameter(selectedPrompt.getName());
						scalarParameterHandle.setCategory("Unformatted");
						scalarParameterHandle.setDefaultValueList(values);
						designHandle.getParameters().add(scalarParameterHandle);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (prompts.size() == 0) {
			prompts = null;
		}

		List<RelationStrategy> strats = new ArrayList<RelationStrategy>();
		if (ds.getRelationStrategies() != null && !ds.getRelationStrategies().isEmpty()) {
			for (FwrRelationStrategy relation : ds.getRelationStrategies()) {
				strats.add(model.getRelationStrategy(relation.getName()));
			}
		}

		if (strats.size() == 0) {
			strats = null;
		}

		// get the query
		IQuery iq = SqlQueryBuilder.getQuery(ds.getDatasource().getGroup(), iDataStreamElements, null, aggs, null, filters, prompts);
		((QuerySql) iq).setRelationStrategies(strats);
		((QuerySql) iq).setFormulas(formulas);
		if (isPreview) {
			((QuerySql) iq).setLimit(100);
		}

		for (Prompt dyn : dynamicPrompts) {
			((QuerySql) iq).addDesignTimeResource(dyn);
		}

		if (ds.getDatasource().getConnectionName() == null || "Default".equalsIgnoreCase(ds.getDatasource().getConnectionName()) || ds.getDatasource().getConnectionName().trim().equalsIgnoreCase("")) {
			ds.getDatasource().setConnectionName(pckg.getConnectionsNames(ds.getDatasource().getGroup()).get(0));
		}

		String result = ((QuerySql) iq).getXml();
		if (pckg instanceof UnitedOlapBusinessPackage) {
			result = result.substring(0, result.indexOf("<distinct>")) + "\n<hideNull>true</hideNull>\n" + result.substring(result.indexOf("<distinct>"), result.length() - 1);
		}

		return result;
	}

	private static IBusinessTable lookUnder(IBusinessTable table, String tableName, String groupName) {
		IBusinessTable res = null;

		List<IBusinessTable> childs = table.getChilds(groupName);

		for (Object o : childs) {
			IBusinessTable t = (IBusinessTable) o;
			if (t.getName().equalsIgnoreCase(tableName)) {
				res = t;
			}
			if (res == null) {
				return lookUnder(t, tableName, groupName);
			}
		}

		return res;
	}

	public static IBusinessModel getSelectedModel(IRepositoryApi sock, DataSource datasource) throws Exception {
		IRepository irep = new Repository(sock, IRepositoryApi.FMDT_TYPE);

		RepositoryItem item = irep.getItem(datasource.getItemId());
		String result = sock.getRepositoryService().loadModel(item);
		InputStream input = IOUtils.toInputStream(result, "UTF-8");

		Collection<IBusinessModel> bmodels = MetaDataReader.read(datasource.getGroup(), input, sock, false);
		IBusinessModel model = null;

		Iterator<IBusinessModel> itbm = bmodels.iterator();
		while (itbm.hasNext()) {
			IBusinessModel bmodel = (IBusinessModel) itbm.next();
			if (bmodel.getName().equals(datasource.getBusinessModel())) {
				model = bmodel;
			}
		}

		return model;
	}

	private static IDataStreamElement getSelectedColumns(IBusinessPackage bpackage, String selectedTableName, String selectedColumnName, String group) throws Exception {
		Collection<IBusinessTable> tables = bpackage.getBusinessTables(group);
		for (IBusinessTable btable : tables) {
			if (btable.getName().equals(selectedTableName)) {
				Collection<IDataStreamElement> columns = btable.getColumns(group);
				for (IDataStreamElement element : columns) {
					if (element.getName().equals(selectedColumnName)) {
						return element;
					}
				}
			}
		}
		return null;
	}

}
