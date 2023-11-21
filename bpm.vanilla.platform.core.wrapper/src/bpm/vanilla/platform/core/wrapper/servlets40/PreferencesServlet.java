package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.PreferenceManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class PreferencesServlet extends AbstractComponentServlet{

	private static final long serialVersionUID = 1L;

	public PreferencesServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing PreferencesServlet...");
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IPreferencesManager.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IPreferencesManager.ActionType type = (IPreferencesManager.ActionType)action.getActionType();
			
			log(type, ((PreferenceManager)component.getPreferenceManager()).getComponentName(), req);
			
			try{
				switch (type) {
				case ADD_OPEN_PREF:
					actionResult = addOpenPreference(args);
					break;
				case DEL_OPEN_PREF:
					delOpenPreference(args);
					break;
				case FIND_OPEN_PREF:
					actionResult = findOpenPreferences(args);
					break;
				case LIST_OPEN_PREF:
					actionResult = listOpenPreferences(args);
					break;
				case LIST_OPEN_PREF_4USER:
					actionResult = findOpenPreferences4User(args);
					break;
				case UPDATE_OPEN_PREF:
					updateOpenPreferences(args);
					break;
				
				case ADD_USER_RUN_CONFIG:
					actionResult = addUserRunConfiguration(args);
					break;
				case DEL_USER_RUN_CONFIG:
					deleteUserRunConfiguration(args);
					break;
				case LIST_USER_RUN_CONFIG:
					actionResult = getUserRunConfiguration(args);
					break;
				case LIST_USER_RUN_CONFIG_BY_ID:
					actionResult = getUserRunConfigurationById(args);
					break;
				case LIST_USER_RUN_CONFIG_BY_USER:
					actionResult = getUserRunConfigurationByUser(args);
					break;
				case LIST_USER_RUN_CONFIG_BY_USER_OBJECTID:
					actionResult = getUserRunConfigurationByUserObjectId(args);
					break;
				case UPDATE_USER_RUN_CONFIG:
					updateUserRunConfiguration(args);
					break;
				case GET_LOGO_URL:
					actionResult = component.getPreferenceManager().getCustomLogoUrl();
					break;
				case SET_LOGO_PICTURE:
					copyCustomLogo(args);
					break;
				case GET_FAST_CONNECTION:
					actionResult = component.getPreferenceManager().includeFastConnection();
					break;
				case SET_FAST_CONNECTION:
					setFastConnection(args);
					break;
				case ADD_WIDGET:
					actionResult = addWidget(args);
					break;
				case DEL_WIDGET:
					deleteWitget(args);
					break;
				case LIST_OF_WIDGET:
					actionResult = getListWidgets();
					break;
				case GET_WIDGET_BY_ID:
					actionResult = getWidgetById(args);
					break;
				case GET_WIDGET_BY_USER:
					actionResult = getWidgetByUser(args);
					break;
				case DELETE_DATASOURCE:
					component.getPreferenceManager().deleteDatasource((Datasource) args.getArguments().get(0));
					break;
				case GET_DATASOURCE_BY_ID:
					actionResult = component.getPreferenceManager().getDatasourceById((Integer) args.getArguments().get(0));
					break;
				case GET_DATASOURCES:
					actionResult = component.getPreferenceManager().getDatasources();
					break;
				case GET_DATASOURCES_FILTER_TYPE:
					actionResult = component.getPreferenceManager().getDatasources((List<DatasourceType>) args.getArguments().get(0));
					break;
				case ADD_DATASOURCE:
					actionResult = component.getPreferenceManager().addDatasource((Datasource) args.getArguments().get(0), (boolean) args.getArguments().get(1));
					break;
				case UPDATE_DATASOURCE:
					component.getPreferenceManager().updateDatasource((Datasource) args.getArguments().get(0));
					break;
				case DELETE_DATASET:
					component.getPreferenceManager().deleteDataset((Dataset) args.getArguments().get(0));
					break;
				case GET_DATASET_BY_ID:
					actionResult = component.getPreferenceManager().getDatasetById((Integer) args.getArguments().get(0));
					break;
				case GET_DATASETS:
					actionResult = component.getPreferenceManager().getDatasets();
					break;
				case ADD_DATASET:
					actionResult = component.getPreferenceManager().addDataset((Dataset) args.getArguments().get(0));
					break;
				case UPDATE_DATASET:
					component.getPreferenceManager().updateDataset((Dataset) args.getArguments().get(0));
					break;
				case GET_DATACOLUMNS_BY_DATASET:
					actionResult = component.getPreferenceManager().getDataColumnsbyDataset((Dataset) args.getArguments().get(0));
					break;
				case ADD_DATACOLUMN:
					component.getPreferenceManager().addDataColumn((DataColumn) args.getArguments().get(0));
					break;
				case UPDATE_DATACOLUMN:
					component.getPreferenceManager().updateDataColumn((DataColumn) args.getArguments().get(0));
					break;
				case DELETE_DATACOLUMN:
					component.getPreferenceManager().deleteDataColumn((DataColumn) args.getArguments().get(0));
					break;
				case GET_DATACOLUMN_BY_ID:
					actionResult = component.getPreferenceManager().getDataColumnbyId((int) args.getArguments().get(0));
					break;
				case GET_DATASOURCE_HBASE_LIST_TABLES:
					actionResult = component.getPreferenceManager().getDatasourceHbaseMetadataListTables((Datasource) args.getArguments().get(0));
					break;
				case GET_DATASOURCE_HBASE_METADATA:
					actionResult = component.getPreferenceManager().getDatasourceHbaseMetadata((String) args.getArguments().get(0), (Datasource) args.getArguments().get(1));
					break;
				case GET_DATASOURCE_ARCHITECT_METADATA:
					actionResult = component.getPreferenceManager().getDatasourceArchitectMetadata((String) args.getArguments().get(0), (Datasource) args.getArguments().get(1));
					break;
				case GET_CSV_VANILLA_METADATA:
					actionResult = component.getPreferenceManager().getDatasourceCsvVanillaMetadata((Datasource) args.getArguments().get(0));
					break;
				case GET_HDFS_FILE:
					actionResult = component.getPreferenceManager().getHdfsFile((String) args.getArguments().get(0));
					break;
				case TEST_JDBC:
					actionResult = component.getPreferenceManager().testJdbcDatasource((DatasourceJdbc) args.getArguments().get(0));
					break;
				case GET_DATABASE_STRUCTURE:
					actionResult = component.getPreferenceManager().getDatabaseStructure((Datasource) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case TEST_CONNECTION:
					actionResult = component.getPreferenceManager().testConnection((Datasource) args.getArguments().get(0));
					break;
				case GET_RESULT_QUERY:
					actionResult = component.getPreferenceManager().getResultQuery((Dataset) args.getArguments().get(0));
					break;
				case GET_RESULT_QUERY_LIMIT:
					actionResult = component.getPreferenceManager().getResultQuery((Dataset) args.getArguments().get(0), (Integer) args.getArguments().get(1));
					break;
				case EXECUTE_FORM_QUERY:
					component.getPreferenceManager().executeFormQuery((Form) args.getArguments().get(0));
					break;
				case EXECUTE_FORM_SEARCH_QUERY:
					actionResult = component.getPreferenceManager().executeFormSearchQuery((String)args.getArguments().get(0), (Form) args.getArguments().get(1));
					break;
				case EXECUTE_FORM_UPDATE_QUERY:
					component.getPreferenceManager().executeFormUpdateQuery((Form) args.getArguments().get(0), (Map<String, FormField>) args.getArguments().get(1));
					break;
				case GET_COUNT_QUERY:
					actionResult = component.getPreferenceManager().getCountQuery((Dataset) args.getArguments().get(0));
					break;
				case GET_DATASETS_BY_DATASOURCE:
					actionResult = component.getPreferenceManager().getDatasetByDatasource((Datasource) args.getArguments().get(0));
					break;
				}
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private Object getWidgetByUser(XmlArgumentsHolder args) throws Exception {
		return component.getPreferenceManager().getWidgetsByUser((Integer)args.getArguments().get(0));
	}

	private Object getWidgetById(XmlArgumentsHolder args) throws Exception {
		return component.getPreferenceManager().getWidgetById((Integer)args.getArguments().get(0));
	}

	private Object getListWidgets() throws Exception {
		return component.getPreferenceManager().getAllWidgets();
	}

	private void deleteWitget(XmlArgumentsHolder args) throws Exception {
		component.getPreferenceManager().delWidget((Widgets)args.getArguments().get(0));
	}

	private Object addWidget(XmlArgumentsHolder args) throws Exception {
		return component.getPreferenceManager().addWidget((Widgets)args.getArguments().get(0));
	}

	private void updateUserRunConfiguration(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("updateUserRunConfiguration", UserRunConfiguration.class), args);
		component.getPreferenceManager().updateUserRunConfiguration((UserRunConfiguration) args.getArguments().get(0));
	}

	private Object getUserRunConfigurationByUserObjectId(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("getUserRunConfigurationsByUserIdObjectId", int.class, IObjectIdentifier.class), args);
		return component.getPreferenceManager().getUserRunConfigurationsByUserIdObjectId((Integer) args.getArguments().get(0), (IObjectIdentifier) args.getArguments().get(1));
	}

	private Object getUserRunConfigurationByUser(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("getUserRunConfigurationsByUserId", int.class), args);
		return component.getPreferenceManager().getUserRunConfigurationsByUserId((Integer) args.getArguments().get(0));
	}

	private Object getUserRunConfigurationById(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("getUserRunConfigurationById", int.class), args);
		return component.getPreferenceManager().getUserRunConfigurationById((Integer) args.getArguments().get(0));
	}

	private Object getUserRunConfiguration(XmlArgumentsHolder args) throws Exception {
		return component.getPreferenceManager().getUserRunConfigurations();
	}

	private void deleteUserRunConfiguration(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("deleteUserRunConfiguration", UserRunConfiguration.class), args);
		component.getPreferenceManager().deleteUserRunConfiguration((UserRunConfiguration) args.getArguments().get(0));
	}

	private Object addUserRunConfiguration(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("addUserRunConfiguration", UserRunConfiguration.class), args);
		return component.getPreferenceManager().addUserRunConfiguration((UserRunConfiguration) args.getArguments().get(0));
	}

	private void updateOpenPreferences(XmlArgumentsHolder args ) throws Exception{
		argChecker.checkArguments(IPreferencesManager.class.getMethod("updateOpenPreference", OpenPreference.class), args);
		component.getPreferenceManager().updateOpenPreference((OpenPreference)args.getArguments().get(0));
		
	}

	private Object findOpenPreferences4User(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IPreferencesManager.class.getMethod("getOpenPreferencesForUserId", int.class), args);
		return component.getPreferenceManager().getOpenPreferencesForUserId((Integer)args.getArguments().get(0));
	}

	private Object listOpenPreferences(XmlArgumentsHolder args) throws Exception{
		return component.getPreferenceManager().getOpenPreferences();

	}

	private Object findOpenPreferences(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IPreferencesManager.class.getMethod("getOpenPreferenceById", int.class), args);
		return component.getPreferenceManager().getOpenPreferenceById((Integer)args.getArguments().get(0));

	}

	private void delOpenPreference(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IPreferencesManager.class.getMethod("delOpenPreference", OpenPreference.class), args);
		component.getPreferenceManager().delOpenPreference((OpenPreference)args.getArguments().get(0));

		
	}

	private int addOpenPreference(XmlArgumentsHolder args)throws Exception {
		argChecker.checkArguments(IPreferencesManager.class.getMethod("addOpenPreference", OpenPreference.class), args);
		return component.getPreferenceManager().addOpenPreference((OpenPreference)args.getArguments().get(0));

		
	}
	
	private void setFastConnection(XmlArgumentsHolder args)throws Exception {
		component.getPreferenceManager().setIncludeFastConnection((Boolean)args.getArguments().get(0));
	}
	private void copyCustomLogo(XmlArgumentsHolder args)throws Exception {
		if (args.getArguments().isEmpty() ||args.getArguments().get(0)==null){
			component.getPreferenceManager().setCustomLogo(null);
		}
		else{
			DataHandler h = null;
			try{
				byte[] raw = (byte[])args.getArguments().get(0);
				
				raw = Base64.decodeBase64(raw);
				ByteArrayInputStream bis = new ByteArrayInputStream(raw);
				String mimeType = URLConnection.guessContentTypeFromStream(bis);
				h = new DataHandler(new _ByteArrayDataSource(mimeType, bis, "customLogo.png"));
				
			}catch(Exception ex){
				String m = "Failed to read incoming picture raw bytes ";
				Logger.getLogger(getClass()).error(m + ex.getMessage(), ex);
				throw new Exception(m + ex.getMessage(), ex);
			}
			component.getPreferenceManager().setCustomLogo(h);
			
		}
	}
	
	public static class _ByteArrayDataSource implements DataSource{
		String mimeType;
		InputStream is;
		String name;
		
		_ByteArrayDataSource(String mimeType, InputStream is, String name){
			this.mimeType = mimeType;
			this.is = is;
			this.name = name;
		}
		@Override
		public String getContentType() {
			return mimeType;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return is;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Datas wannot be wrote on this DataSource");
		}
		
	}
}
