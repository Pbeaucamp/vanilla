package bpm.vanilla.platform.core.remote.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
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
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteVanillaPreferencesManager implements IPreferencesManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteVanillaPreferencesManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public int addOpenPreference(OpenPreference openPreference) throws Exception {
		XmlAction op = new XmlAction(createArguments(openPreference), IPreferencesManager.ActionType.ADD_OPEN_PREF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void delOpenPreference(OpenPreference openPreference) throws Exception {
		XmlAction op = new XmlAction(createArguments(openPreference), IPreferencesManager.ActionType.DEL_OPEN_PREF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

	}

	@Override
	public OpenPreference getOpenPreferenceById(int openPreferenceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(openPreferenceId), IPreferencesManager.ActionType.FIND_OPEN_PREF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (OpenPreference) xstream.fromXML(xml);
	}

	@Override
	public List<OpenPreference> getOpenPreferences() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.LIST_OPEN_PREF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List) xstream.fromXML(xml);
	}

	@Override
	public List<OpenPreference> getOpenPreferencesForUserId(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IPreferencesManager.ActionType.LIST_OPEN_PREF_4USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List) xstream.fromXML(xml);
	}

	@Override
	public void updateOpenPreference(OpenPreference openPreference) throws Exception {
		XmlAction op = new XmlAction(createArguments(openPreference), IPreferencesManager.ActionType.UPDATE_OPEN_PREF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

	}

	@Override
	public int addUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRunConfiguration), IPreferencesManager.ActionType.ADD_USER_RUN_CONFIG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void deleteUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRunConfiguration), IPreferencesManager.ActionType.DEL_USER_RUN_CONFIG);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public UserRunConfiguration getUserRunConfigurationById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IPreferencesManager.ActionType.LIST_USER_RUN_CONFIG_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (UserRunConfiguration) xstream.fromXML(xml);
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurations() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.LIST_USER_RUN_CONFIG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<UserRunConfiguration>) xstream.fromXML(xml);
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurationsByUserId(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IPreferencesManager.ActionType.LIST_USER_RUN_CONFIG_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<UserRunConfiguration>) xstream.fromXML(xml);
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurationsByUserIdObjectId(int userId, IObjectIdentifier objectId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, objectId), IPreferencesManager.ActionType.LIST_USER_RUN_CONFIG_BY_USER_OBJECTID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<UserRunConfiguration>) xstream.fromXML(xml);
	}

	@Override
	public void updateUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRunConfiguration), IPreferencesManager.ActionType.UPDATE_USER_RUN_CONFIG);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public String getCustomLogoUrl() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.GET_LOGO_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (xml == null || xml.isEmpty()) {
			return null;
		}
		return (String) xstream.fromXML(xml);

	}

	@Override
	public void setCustomLogo(DataHandler datas) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		byte[] raws = null;

		if (datas != null) {
			try {
				IOWriter.write(datas.getInputStream(), stream, true, true);
				raws = stream.toByteArray();
				raws = Base64.encodeBase64(raws);
			} catch (Exception ex) {
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Unable to convert picture DataHandler into base64 raw byte array", ex);
			}

		}

		XmlAction op = new XmlAction(createArguments(raws), IPreferencesManager.ActionType.SET_LOGO_PICTURE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

	}

	@Override
	public boolean includeFastConnection() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.GET_FAST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public void setIncludeFastConnection(boolean include) throws Exception {
		XmlAction op = new XmlAction(createArguments(include), IPreferencesManager.ActionType.SET_FAST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

	}

	@Override
	public int addWidget(Widgets w) throws Exception {
		// XmlAction op = new XmlAction(createArguments(userRunConfiguration),
		// IPreferencesManager.ActionType.ADD_USER_RUN_CONFIG);
		// String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		// return (Integer)xstream.fromXML(xml);
		XmlAction op = new XmlAction(createArguments(w), IPreferencesManager.ActionType.ADD_WIDGET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void delWidget(Widgets w) throws Exception {
		XmlAction op = new XmlAction(createArguments(w), IPreferencesManager.ActionType.DEL_WIDGET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<Widgets> getAllWidgets() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.LIST_OF_WIDGET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		Object o = xstream.fromXML(xml);
		return (List<Widgets>) o;
	}

	@Override
	public Widgets getWidgetById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IPreferencesManager.ActionType.GET_WIDGET_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Widgets) xstream.fromXML(xml);
	}

	@Override
	public Datasource getDatasourceById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IPreferencesManager.ActionType.GET_DATASOURCE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Datasource) xstream.fromXML(xml);
	}

	@Override
	public List<Datasource> getDatasources() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.GET_DATASOURCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Datasource>) xstream.fromXML(xml);
	}

	@Override
	public Datasource addDatasource(Datasource datasource, boolean forceCreation) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource, forceCreation), IPreferencesManager.ActionType.ADD_DATASOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Datasource) xstream.fromXML(xml);
	}

	@Override
	public void updateDatasource(Datasource datasource) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource), IPreferencesManager.ActionType.UPDATE_DATASOURCE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteDatasource(Datasource datasource) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource), IPreferencesManager.ActionType.DELETE_DATASOURCE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public Dataset getDatasetById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IPreferencesManager.ActionType.GET_DATASET_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Dataset) xstream.fromXML(xml);
	}

	@Override
	public List<Dataset> getDatasets() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IPreferencesManager.ActionType.GET_DATASETS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Dataset>) xstream.fromXML(xml);
	}

	@Override
	public Dataset addDataset(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.ADD_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Dataset) xstream.fromXML(xml);
	}

	@Override
	public void updateDataset(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.UPDATE_DATASET);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteDataset(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.DELETE_DATASET);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<DataColumn> getDataColumnsbyDataset(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.GET_DATACOLUMNS_BY_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<DataColumn>) xstream.fromXML(xml);
	}

	@Override
	public void addDataColumn(DataColumn column) throws Exception {
		XmlAction op = new XmlAction(createArguments(column), IPreferencesManager.ActionType.ADD_DATACOLUMN);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void updateDataColumn(DataColumn column) throws Exception {
		XmlAction op = new XmlAction(createArguments(column), IPreferencesManager.ActionType.UPDATE_DATACOLUMN);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteDataColumn(DataColumn column) throws Exception {
		XmlAction op = new XmlAction(createArguments(column), IPreferencesManager.ActionType.DELETE_DATACOLUMN);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	// @Override
	// public List<String> getDatasourceCsvMetadata(Datasource datasourceCsv)
	// throws Exception {
	// XmlAction op = new XmlAction(createArguments(datasourceCsv),
	// IPreferencesManager.ActionType.GET_CSV_METADATA);
	// String xml = httpCommunicator.executeAction(op, xstream.toXML(op),
	// false);
	// return (List<String>) xstream.fromXML(xml);
	// }

	@Override
	public DataColumn getDataColumnbyId(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IPreferencesManager.ActionType.GET_DATACOLUMN_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DataColumn) xstream.fromXML(xml);
	}

	@Override
	public List<HbaseTable> getDatasourceHbaseMetadataListTables(Datasource datasourceHBase) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasourceHBase), IPreferencesManager.ActionType.GET_DATASOURCE_HBASE_LIST_TABLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<HbaseTable>) xstream.fromXML(xml);
	}

	@Override
	public List<DataColumn> getDatasourceHbaseMetadata(String tableName, Datasource datasourceHBase) throws Exception {
		XmlAction op = new XmlAction(createArguments(tableName, datasourceHBase), IPreferencesManager.ActionType.GET_DATASOURCE_HBASE_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<DataColumn>) xstream.fromXML(xml);
	}

	@Override
	public List<DataColumn> getDatasourceArchitectMetadata(String tableName, Datasource datasourceArchitect) throws Exception {
		XmlAction op = new XmlAction(createArguments(tableName, datasourceArchitect), IPreferencesManager.ActionType.GET_DATASOURCE_ARCHITECT_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<DataColumn>) xstream.fromXML(xml);
	}
	
	@Override
	public List<String> getDatasourceCsvVanillaMetadata(Datasource datasourceCsvVanilla) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasourceCsvVanilla), IPreferencesManager.ActionType.GET_CSV_VANILLA_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public String getHdfsFile(String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(path), IPreferencesManager.ActionType.GET_HDFS_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) xstream.fromXML(xml);
	}

	@Override
	public String testJdbcDatasource(DatasourceJdbc datasource) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource), IPreferencesManager.ActionType.TEST_JDBC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) xstream.fromXML(xml);
	}

	@Override
	public List<DatabaseTable> getDatabaseStructure(Datasource datasource, boolean managePostgres) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource, managePostgres), IPreferencesManager.ActionType.GET_DATABASE_STRUCTURE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<DatabaseTable>) xstream.fromXML(xml);
	}

	@Override
	public boolean testConnection(Datasource datasource) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasource), IPreferencesManager.ActionType.TEST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<Datasource> getDatasources(List<DatasourceType> filterTypes) throws Exception {
		XmlAction op = new XmlAction(createArguments(filterTypes), IPreferencesManager.ActionType.GET_DATASOURCES_FILTER_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Datasource>) xstream.fromXML(xml);
	}

	@Override
	public DatasetResultQuery getResultQuery(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.GET_RESULT_QUERY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DatasetResultQuery) xstream.fromXML(xml);
	}

	@Override
	public List<Widgets> getWidgetsByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IPreferencesManager.ActionType.GET_WIDGET_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		Object o = xstream.fromXML(xml);
		return (List<Widgets>) o;
	}

	@Override
	public void executeFormQuery(Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IPreferencesManager.ActionType.EXECUTE_FORM_QUERY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<Map<String, FormField>> executeFormSearchQuery(String query, Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(query, form), IPreferencesManager.ActionType.EXECUTE_FORM_SEARCH_QUERY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Map<String, FormField>>) xstream.fromXML(xml);
	}

	@Override
	public void executeFormUpdateQuery(Form form, Map<String, FormField> editedLine) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, editedLine), IPreferencesManager.ActionType.EXECUTE_FORM_UPDATE_QUERY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public int getCountQuery(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), IPreferencesManager.ActionType.GET_COUNT_QUERY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public DatasetResultQuery getResultQuery(Dataset dataset, int limit) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset, limit), IPreferencesManager.ActionType.GET_RESULT_QUERY_LIMIT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DatasetResultQuery) xstream.fromXML(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Dataset> getDatasetByDatasource(Datasource ds) throws Exception {
		XmlAction op = new XmlAction(createArguments(ds), IPreferencesManager.ActionType.GET_DATASETS_BY_DATASOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Dataset>) xstream.fromXML(xml);
	}
}
