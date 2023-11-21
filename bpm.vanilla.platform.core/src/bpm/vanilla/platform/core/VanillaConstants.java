package bpm.vanilla.platform.core;

/**
 * 
 * ere : dunno about others, i DEPEND on the trailing '/' in servlets, do not remove 
 *
 */
public interface VanillaConstants {
	/**
	 * This constant is used in the bpm.android.vanilla.core.IAndroidConstant plugin so be carefull before changing it
	 */
	public static final String HTTP_HEADER_VANILLA_SESSION_ID = "bpm.vanilla.platform.core.wrapper.sessionUUID";
	public static final String HTTP_HEADER_COMPONENT_URL = "bpm.vanilla.platform.core.wrapper.componentUrl";
	
	public static final String HTTP_HEADER_SERVLET_DISPATCH_ACTION = "bpm.vanilla.platform.core.wrapper.dispatcher.action";
//	public static final String HTTP_HEADER_SERVLET_COMPONENT_NATURE = "bpm.vanilla.platform.core.wrapper.dispatcher.componentNature";
	
	public static final String VANILLA_PLATFORM_DISPATCHER_SERVLET = "/vanilla40/vanillaDispatcher";
	
	public static final String FREEMETRICS_MANAGER_URL = "/freeMetricsManager";
	public static final String FREEMETRICSEXCEL_MANAGER_URL = "/KpitExcelServlet";
	
	public static final String VANILLA_SYSTEM_MANAGER_SERVLET = "/vanilla40/vanillaSystem";
	public static final String VANILLA_ACCESS_MANAGER_SERVLET = "/vanilla40/vanillaAccessRequest";
	public static final String VANILLA_REPOSITORY_MANAGER_SERVLET = "/vanilla40/vanillaRepositories";
	public static final String VANILLA_PREFERENCES_MANAGER_SERVLET = "/vanilla40/vanillaPreferences";
	public static final String VANILLA_LOGGING_MANAGER_SERVLET = "/vanilla40/vanillaLogging";
	public static final String VANILLA_EXTERNAL_ACCESSIBILLITY_MANAGER_SERVLET = "/vanilla40/externalAccess";
	public static final String VANILLA_SECURITY_MANAGER_SERVLET = "/vanilla40/security";
	public static final String VANILLA_CONTRIBUTION_MANAGER_SERVLET = "/vanilla40/contribution";
	public static final String VANILLA_EXCEL_MANAGER_SERVLET = "/vanilla40/vanillaExcel";
	
	public static final String VANILLA_UNITED_OLAP_PRELOAD_SERVLET = "/vanilla40/unitedOlapPreload";

	public static final String VANILLA_LISTENERS_SERVLET = "/vanilla40/listeners";
	public static final String VANILLA_PARAMETERS_PROVIDER_SERVLET = "/vanilla40/parameterProvider";
	
	public static final String VANILLA_EXTERNAL_CALL = "/vanilla40/accessibility";
	//this one is for setting the parameters for the VANILLA_EXTERNAL_CALL
	public static final String VANILLA_EXTERNAL_CALL_PARAM = "/vanilla40/accessibility_param";
	
	public static final String VANILLA_HISTORIC_REPORT_SERVLET = "/vanilla40/reportHistoric";
	public static final String VANILLA_GED_INDEX_SERVLET = "/vanilla40/gedIndex";
	public static final String VANILLA_BIG_FILE_GED_SERVLET = "/vanilla40/bigFileGed";

	public static final String VANILLA_WEBSERVICE_SERVLET = "/vanilla/WebServiceServlet";
	
	public static final String REPOSITORY_SERVLET = "/serviceServlet";
	public static final String VANILLA_COMMENT = "/vanilla40/commentServlet";

	public static final String VANILLA_JDBC_FMDT_SERVLET = "/vanilla/fmdtJdbcServlet";

	public static final String VANILLA_CONNECTION_MANAGER = "/vanilla/connectionManager";
	
	public static final String FREEMETADATA_EXCEL_SERVLET = "/vanilla/FmdtExcelServlet";
	public static final String VANILLA_FEEDBACK_SERVLET = "/communicationServlet";
	
	public static final String IMAGE_MANAGER_SERVLET = "/vanilla/imagemanagerservlet";
	public static final String ARCHIVE_MANAGER_SERVLET = "/vanilla/archivemanagerservlet";
	public static final String RESOURCE_MANAGER_SERVLET = "/vanilla/resourcemanagerservlet";
	public static final String AUTO_LOGIN_SERVLET = "/vanilla/getAutologin";
	public static final String GLOBAL_SERVLET = "/vanilla/global";
	public static final String EXTERNAL_SERVLET = "/vanilla/external";
	
	public static final String KEYCLOAK_RESOURCE = "/vanilla/keycloak";
	

	public static final String HTTP_HEADER_LOCALE = "bpm.vanilla.locale";
	public static final String HTTP_PART_XSTREAM_INFOS = "bpm.vanilla.part.infos";
	public static final String HTTP_PART_FILES = "bpm.vanilla.part.files";
	
	/**
	 * argument for the type of Component taht is trageted
	 */
//	public static final String VANILLA_COMPONENT_TYPE = "bpm.vanilla.platform.core.wrapper.dispatcher.typeName";
	
}
