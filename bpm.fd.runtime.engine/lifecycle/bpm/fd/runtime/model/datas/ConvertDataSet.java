package bpm.fd.runtime.model.datas;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceKpi;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * Class that contains method that were previously in the class DashbooardHelper 
 * In order to be possible to call these methodes in another project such as ...
 * 
 * @author BPM-FIX2
 *
 */

public class ConvertDataSet {
	
	/*private static IVanillaAPI vanillaApi;
	private static List<Datasource> datasources;*/
	
	private static List<Datasource> datasources;

	public static Dataset convertDataset(DataSet dataSet, Dictionary dictionary,IVanillaAPI vanillaApi) throws Exception {
		datasources = vanillaApi.getVanillaPreferencesManager().getDatasources();
		Datasource resultDatasource = new Datasource();

		String datasourceName = dataSet.getDataSourceName();
		resultDatasource.setName(datasourceName);

		DataSource datasource = dictionary.getDatasource(datasourceName);

		String datasourceId = datasource.getOdaExtensionDataSourceId();

		Dataset resultDataset = new Dataset();
		resultDataset.setDatasource(resultDatasource);
		resultDataset.setName(dataSet.getName());

		if (datasourceId.equals("org.eclipse.birt.report.data.oda.jdbc")) {
			convertJdbcDatasourceDataset(resultDataset, dataSet, datasource);
		}
		else if (datasourceId.equals("bpm.metadata.birt.oda.runtime")) {
			convertMetadataDatasourceDataset(resultDataset, dataSet, datasource,vanillaApi);
		}
		else if (datasourceId.equals("bpm.fm.oda.driver")) {
			convertKpiDatasourceDataset(resultDataset, dataSet, datasource);
			//FIXME Change this once we support kpi datasource/dataset correctly
			return resultDataset;
		}
		resultDataset = lookInVanillaForDatasourceDatasets(resultDatasource, resultDataset, vanillaApi, datasources);

		return resultDataset;
	}
	
	
	private static Dataset lookInVanillaForDatasourceDatasets(Datasource resultDatasource, Dataset resultDataset,IVanillaAPI vanillaApi,List<Datasource> datasources) throws Exception {

		for (Datasource datasource : datasources ) {
			// the datasource exists
			if (datasource.getType() == resultDatasource.getType() && datasource.getObject().equals(resultDatasource.getObject())) {
				for (Dataset dataset : datasource.getDatasets()) {
					// the dataset already exists
					try {
						if (dataset.getRequest().equals(resultDataset.getRequest())) {
							return dataset;
						}
					} catch (Exception e) {
					}
				}
				// the dataset doesn't exist
				resultDataset.setDatasource(datasource);
				Dataset result = getVanillaRootApi(vanillaApi).getVanillaPreferencesManager().addDataset(resultDataset);
				datasource.getDatasets().add(result);
				return result;
			}
		}

		// the datasource and dataset don't exist
		resultDatasource = getVanillaRootApi(vanillaApi).getVanillaPreferencesManager().addDatasource(resultDatasource, false);
		resultDataset.setDatasource(resultDatasource);

		resultDatasource.getDatasets().add(resultDataset);

		datasources.add(resultDatasource);

		return getVanillaRootApi(vanillaApi).getVanillaPreferencesManager().addDataset(resultDataset);
	}
	
	private static void convertJdbcDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource) {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.JDBC);
		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setFullUrl(true);
		resultDatasource.setObject(jdbc);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("odaDriverClass")) {
				jdbc.setDriver(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaURL")) {
				jdbc.setUrl(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaPassword")) {
				jdbc.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaUser")) {
				jdbc.setUser(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
	}
	
	
	private static void convertMetadataDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource, IVanillaAPI vanillaApi) throws Exception {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.FMDT);
		DatasourceFmdt fmdt = new DatasourceFmdt();
		resultDatasource.setObject(fmdt);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("USER")) {
				fmdt.setUser(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("PASSWORD")) {
				fmdt.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("REPOSITORY_ID")) {
				fmdt.setRepositoryId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("DIRECTORY_ITEM_ID")) {
				fmdt.setItemId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("BUSINESS_MODEL")) {
				fmdt.setBusinessModel(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("BUSINESS_PACKAGE")) {
				fmdt.setBusinessPackage(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("GROUP_NAME")) {
				fmdt.setGroupId(getVanillaRootApi(vanillaApi).getVanillaSecurityManager().getGroupByName(datasource.getProperties().getProperty(propName)).getId());
			}
			else if (propName.equals("VANILLA_URL")) {
				fmdt.setUrl(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
	}
	
	
	private static void convertKpiDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource) {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.KPI);
		DatasourceKpi fmdt = new DatasourceKpi();
		resultDatasource.setObject(fmdt);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("fmLogin")) {
				fmdt.setUser(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("freemetricsPassword")) {
				fmdt.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("fmGroupId")) {
				fmdt.setGroupId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("fmThemeId")) {
				fmdt.setThemeId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("fmUrl")) {
				fmdt.setUrl(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
		
	}
	
	private static List<DataColumn> convertColumns(List<ColumnDescriptor> columnsDescriptors) {
		List<DataColumn> columns = new ArrayList<DataColumn>();

		for (ColumnDescriptor desc : columnsDescriptors) {
			DataColumn col = new DataColumn();
			col.setColumnLabel(desc.getColumnLabel());
			col.setColumnName(desc.getColumnName());
			col.setColumnTypeName(desc.getColumnTypeName());
			col.setColumnType(desc.getColumnType());
			columns.add(col);
		}

		return columns;
	}

	private static IVanillaAPI getVanillaRootApi( IVanillaAPI vanillaApi ) {
		if (vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return vanillaApi;
	}


	/*
	public IVanillaAPI getVanillaApi() {
		return vanillaApi;
	}




	public void setVanillaApi(IVanillaAPI vanillaApi) {
		vanillaApi = vanillaApi;
	}


	public static List<Datasource> getDatasources() {
		return datasources;
	}


	public void setDatasources(List<Datasource> datasources) {
		this.datasources = datasources;
	}*/

}
