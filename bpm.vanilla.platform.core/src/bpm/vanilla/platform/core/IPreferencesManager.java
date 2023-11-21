package bpm.vanilla.platform.core;

import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.data.HbaseTable;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IPreferencesManager {

	public static enum ActionType implements IXmlActionType {
		ADD_OPEN_PREF(Level.DEBUG), DEL_OPEN_PREF(Level.DEBUG), UPDATE_OPEN_PREF(Level.DEBUG), FIND_OPEN_PREF(Level.DEBUG), LIST_OPEN_PREF(Level.DEBUG), LIST_OPEN_PREF_4USER(Level.DEBUG), ADD_USER_RUN_CONFIG(Level.INFO), DEL_USER_RUN_CONFIG(Level.INFO), LIST_USER_RUN_CONFIG(Level.DEBUG), 
		LIST_USER_RUN_CONFIG_BY_ID(Level.DEBUG), LIST_USER_RUN_CONFIG_BY_USER(Level.DEBUG), LIST_USER_RUN_CONFIG_BY_USER_OBJECTID(Level.DEBUG), UPDATE_USER_RUN_CONFIG(Level.INFO), GET_LOGO_URL(Level.DEBUG), SET_LOGO_PICTURE(Level.INFO), 
		SET_FAST_CONNECTION(Level.DEBUG), GET_FAST_CONNECTION(Level.DEBUG), ADD_WIDGET(Level.DEBUG), DEL_WIDGET(Level.DEBUG), LIST_OF_WIDGET(Level.DEBUG), GET_WIDGET_BY_ID(Level.DEBUG), GET_WIDGET_BY_USER(Level.DEBUG),
		GET_DATASOURCES(Level.DEBUG), GET_DATASOURCE_BY_ID(Level.DEBUG), ADD_DATASOURCE(Level.INFO), UPDATE_DATASOURCE(Level.INFO), DELETE_DATASOURCE(Level.INFO), GET_DATASETS(Level.DEBUG), GET_DATASET_BY_ID(Level.DEBUG), 
		ADD_DATASET(Level.INFO), UPDATE_DATASET(Level.INFO), DELETE_DATASET(Level.INFO), GET_DATACOLUMNS_BY_DATASET(Level.DEBUG), ADD_DATACOLUMN(Level.DEBUG), UPDATE_DATACOLUMN(Level.DEBUG), DELETE_DATACOLUMN(Level.DEBUG), GET_DATACOLUMN_BY_ID(Level.DEBUG), 
		GET_DATASOURCE_HBASE_LIST_TABLES(Level.DEBUG), GET_DATASOURCE_HBASE_METADATA(Level.DEBUG), GET_DATASOURCE_ARCHITECT_METADATA(Level.DEBUG), GET_CSV_VANILLA_METADATA(Level.DEBUG), GET_HDFS_FILE(Level.DEBUG), TEST_JDBC(Level.DEBUG), GET_DATABASE_STRUCTURE(Level.DEBUG), TEST_CONNECTION(Level.DEBUG), GET_DATASOURCES_FILTER_TYPE(Level.DEBUG), GET_RESULT_QUERY(Level.DEBUG), 
		EXECUTE_FORM_QUERY(Level.DEBUG), EXECUTE_FORM_SEARCH_QUERY(Level.DEBUG), EXECUTE_FORM_UPDATE_QUERY(Level.DEBUG), GET_COUNT_QUERY(Level.DEBUG), GET_RESULT_QUERY_LIMIT(Level.DEBUG), GET_DATASETS_BY_DATASOURCE(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public int addOpenPreference(OpenPreference openPreference) throws Exception;

	public void delOpenPreference(OpenPreference openPreference) throws Exception;

	public void updateOpenPreference(OpenPreference openPreference) throws Exception;

	public OpenPreference getOpenPreferenceById(int openPreferenceId) throws Exception;

	public List<OpenPreference> getOpenPreferences() throws Exception;

	public List<OpenPreference> getOpenPreferencesForUserId(int userId) throws Exception;

	public int addUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception;

	public void deleteUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception;

	public void updateUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception;

	public UserRunConfiguration getUserRunConfigurationById(int id) throws Exception;

	public List<UserRunConfiguration> getUserRunConfigurations() throws Exception;

	public List<UserRunConfiguration> getUserRunConfigurationsByUserId(int userId) throws Exception;

	public List<UserRunConfiguration> getUserRunConfigurationsByUserIdObjectId(int userId, IObjectIdentifier objectId) throws Exception;

	public String getCustomLogoUrl() throws Exception;

	public void setCustomLogo(DataHandler datas) throws Exception;

	public boolean includeFastConnection() throws Exception;

	public void setIncludeFastConnection(boolean include) throws Exception;

	public int addWidget(Widgets w) throws Exception;

	public void delWidget(Widgets w) throws Exception;

	public List<Widgets> getAllWidgets() throws Exception;

	public Widgets getWidgetById(int id) throws Exception;

	public Datasource getDatasourceById(int id) throws Exception;

	public List<Datasource> getDatasources() throws Exception;

	public Datasource addDatasource(Datasource datasource, boolean forceCreation) throws Exception;

	public void updateDatasource(Datasource datasource) throws Exception;

	public void deleteDatasource(Datasource datasource) throws Exception;

	public Dataset getDatasetById(int id) throws Exception;

	public List<Dataset> getDatasets() throws Exception;

	public Dataset addDataset(Dataset datasource) throws Exception;

	public void updateDataset(Dataset dataset) throws Exception;

	public void deleteDataset(Dataset dataset) throws Exception;

	public List<DataColumn> getDataColumnsbyDataset(Dataset dataset) throws Exception;

	public void addDataColumn(DataColumn column) throws Exception;

	public void updateDataColumn(DataColumn column) throws Exception;

	public void deleteDataColumn(DataColumn column) throws Exception;

	public DataColumn getDataColumnbyId(int id) throws Exception;

	public List<HbaseTable> getDatasourceHbaseMetadataListTables(Datasource datasourceHBase) throws Exception;

	public List<DataColumn> getDatasourceHbaseMetadata(String tableName, Datasource datasourceHBase) throws Exception;

	public List<String> getDatasourceCsvVanillaMetadata(Datasource datasourceCsvVanilla) throws Exception;
	
	public List<DataColumn> getDatasourceArchitectMetadata(String tableName, Datasource datasourceArchitect) throws Exception;

	public String getHdfsFile(String path) throws Exception;
	
	public String testJdbcDatasource(DatasourceJdbc datasource) throws Exception;

	public List<DatabaseTable> getDatabaseStructure(Datasource datasource, boolean managePostgres) throws Exception;
	
	public boolean testConnection(Datasource datasource) throws Exception;
	
	public List<Datasource> getDatasources(List<DatasourceType> filterTypes) throws Exception;
	
	public DatasetResultQuery getResultQuery(Dataset dataset) throws Exception;
	
	public List<Widgets> getWidgetsByUser(int userId) throws Exception;

	public void executeFormQuery(Form form) throws Exception;

	public List<Map<String, FormField>> executeFormSearchQuery(String string, Form form) throws Exception;

	public void executeFormUpdateQuery(Form form, Map<String, FormField> editedLine) throws Exception;
	
	public int getCountQuery(Dataset dataset) throws Exception;

	public DatasetResultQuery getResultQuery(Dataset dataset, int limit) throws Exception;
	
	public List<Dataset> getDatasetByDatasource(Datasource ds) throws Exception;
}
