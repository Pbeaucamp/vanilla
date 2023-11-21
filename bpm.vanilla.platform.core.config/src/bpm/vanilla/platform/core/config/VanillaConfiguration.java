package bpm.vanilla.platform.core.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Class that holds all the info about the current configuration of the instance
 * of vanilla
 * 
 * @author manu
 * 
 */
public class VanillaConfiguration {
	
	public static final String P_VANILLA_URL_LOCALHOST_REWRITE = "bpm.vanilla.url.rewrite.localhost";
	
	public static final String P_VANILLA_URL = "bpm.vanilla.server.url";
	public static final String P_VANILLA_URL_EXTERNAL = "bpm.vanilla.server.url.external";
	
	public static final String P_VANILLA_ROOT_LOGIN = "bpm.vanilla.server.root.login";
	public static final String P_VANILLA_ROOT_PASSWORD = "bpm.vanilla.server.root.password";
	public static final String P_VANILLA_LOG4J_FILE = "bpm.log4j.configurationFile";

	public static final String P_REPOSITORY_DB_DRIVERCLASSNAME = "bpm.vanilla.repository.database.driverClassName";
	public static final String P_REPOSITORY_DB_JDBCURL = "bpm.vanilla.repository.database.jdbcUrl";
	public static final String P_REPOSITORY_DB_USERNAME = "bpm.vanilla.repository.database.userName";
	public static final String P_REPOSITORY_DB_PASSWORD = "bpm.vanilla.repository.database.password";
	public static final String P_REPOSITORY_DB_DIALECT = "bpm.vanilla.repository.database.dialect";

	public static final String P_VANILLA_DB_DRIVERCLASSNAME = "bpm.vanilla.database.driverClassName";
	public static final String P_VANILLA_DB_JDBCURL = "bpm.vanilla.database.jdbcUrl";
	public static final String P_VANILLA_DB_USERNAME = "bpm.vanilla.database.userName";
	public static final String P_VANILLA_DB_PASSWORD = "bpm.vanilla.database.password";
	public static final String P_VANILLA_DB_DIALECT = "bpm.vanilla.database.dialect";

	public static final String P_MDM_DB_DRIVERCLASSNAME = "bpm.mdm.database.driverClassName";
	public static final String P_MDM_DB_JDBCURL = "bpm.mdm.database.jdbcUrl";
	public static final String P_MDM_DB_USERNAME = "bpm.mdm.database.userName";
	public static final String P_MDM_DB_PASSWORD = "bpm.mdm.database.password";
	public static final String P_MDM_DB_DIALECT = "bpm.mdm.database.dialect";

	public static final String P_FREEMETRICS_DB_DRIVERCLASSNAME = "bpm.vanilla.freemetrics.database.driverClassName";
	public static final String P_FREEMETRICS_DB_JDBCURL = "bpm.vanilla.freemetrics.database.jdbcUrl";
	public static final String P_FREEMETRICS_DB_USERNAME = "bpm.vanilla.freemetrics.database.userName";
	public static final String P_FREEMETRICS_DB_PASSWORD = "bpm.vanilla.freemetrics.database.password";
	public static final String P_FREEMETRICS_DB_DIALECT = "bpm.vanilla.freemetrics.database.dialect";

	public static final String P_AGILA_DB_DRIVERCLASSNAME = "bpm.vanilla.vanilladata.database.driverClassName";
	public static final String P_AGILA_DB_JDBCURL = "bpm.vanilla.vanilladata.database.jdbcUrl";
	public static final String P_AGILA_DB_USERNAME = "bpm.vanilla.vanilladata.database.userName";
	public static final String P_AGILA_DB_PASSWORD = "bpm.vanilla.vanilladata.database.password";
	public static final String P_AGILA_DB_DIALECT = "bpm.vanilla.vanilladata.database.dialect";

	public static final String P_NORPARENA_DB_DRIVERCLASSNAME = "bpm.vanilla.norparena.database.driverClassName";
	public static final String P_NORPARENA_DB_JDBCURL = "bpm.vanilla.norparena.database.jdbcUrl";
	public static final String P_NORPARENA_DB_USERNAME = "bpm.vanilla.norparena.database.userName";
	public static final String P_NORPARENA_DB_PASSWORD = "bpm.vanilla.norparena.database.password";
	public static final String P_NORPARENA_DB_DIALECT = "bpm.vanilla.norparena.database.dialect";
	public static final String P_NORPARENA_DB_KML_FILE = "bpm.vanilla.norparena.kmlFilesLocation";
	public static final String P_NORPARENA_DB_FUSIONMAP_FILE = "bpm.vanilla.norparena.fusionMapFilesLocation";
	public static final String P_NORPARENA_DB_SHAPE_FILE = "bpm.vanilla.norparena.shapeFileLocation";

	public static final String P_V_FORM_DB_DRIVERCLASSNAME = "bpm.vanilla.forms.database.driverClassName";
	public static final String P_V_FORM_DB_JDBCURL = "bpm.vanilla.forms.database.jdbcUrl";
	public static final String P_V_FORM_DB_USERNAME = "bpm.vanilla.forms.database.userName";
	public static final String P_V_FORM_DB_PASSWORD = "bpm.vanilla.forms.database.password";
	public static final String P_V_FORM_DB_DIALECT = "bpm.vanilla.forms.database.dialect";

	public static final String P_V_PROFILING_DB_DRIVERCLASSNAME = "bpm.vanilla.profiling.database.driverClassName";
	public static final String P_V_PROFILING_DB_JDBCURL = "bpm.vanilla.profiling.database.jdbcUrl";
	public static final String P_V_PROFILING_DB_USERNAME = "bpm.vanilla.profiling.database.userName";
	public static final String P_V_PROFILING_DB_PASSWORD = "bpm.vanilla.profiling.database.password";
	public static final String P_V_PROFILING_DB_DIALECT = "bpm.vanilla.profiling.database.dialect";

	public static final String P_BIPROCESSMAN_DB_DRIVERCLASSNAME = "bpm.vanilla.scheduler.database.driverClassName";
	public static final String P_BIPROCESSMAN_DB_JDBCURL = "bpm.vanilla.scheduler.database.jdbcUrl";
	public static final String P_BIPROCESSMAN_DB_USERNAME = "bpm.vanilla.scheduler.database.userName";
	public static final String P_BIPROCESSMAN_DB_PASSWORD = "bpm.vanilla.scheduler.database.password";
	public static final String P_BIPROCESSMAN_DB_DIALECT = "bpm.vanilla.scheduler.database.dialect";

	public static final String P_QUARTZ_SHEDULER_INSTANCENAME = "org.quartz.scheduler.instanceName";
	public static final String P_QUARTZ_SHEDULER_INSTANCEID = "org.quartz.scheduler.instanceId";
	public static final String P_QUARTZ_THREADPOOL_CLASS = "org.quartz.threadPool.class";
	public static final String P_QUARTZ_THREADPOOL_COUNT = "org.quartz.threadPool.threadCount";
	public static final String P_QUARTZ_THREADPOOL_PRIORITY = "org.quartz.threadPool.threadPriority";
	public static final String P_QUARTZ_JOBSTORE_MISFIRE = "org.quartz.jobStore.misfireThreshold";
	public static final String P_QUARTZ_JOBSTORE_CLASS = "org.quartz.jobStore.class";
	public static final String P_QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS = "org.quartz.jobStore.driverDelegateClass";
	public static final String P_QUARTZ_JOBSTORE_USEPROPERTIES = "org.quartz.jobStore.useProperties";
	public static final String P_QUARTZ_JOBSTORE_DATASOURCE = "org.quartz.jobStore.dataSource";
	public static final String P_QUARTZ_JOBSTORE_TABLEPREFIX = "org.quartz.jobStore.tablePrefix";
	public static final String P_QUARTZ_JOBSTORE_ISCLUSTERED = "org.quartz.jobStore.isClustered";
	public static final String P_QUARTZ_DS_DRIVER = "org.quartz.dataSource.myDS.driver";
	public static final String P_QUARTZ_DS_URL = "org.quartz.dataSource.myDS.URL";
	public static final String P_QUARTZ_DS_USER = "org.quartz.dataSource.myDS.user";
	public static final String P_QUARTZ_DS_PASSWORD = "org.quartz.dataSource.myDS.password";
	public static final String P_QUARTZ_DS_MAXCONNECTIONS = "org.quartz.dataSource.myDS.maxConnections";
	public static final String P_QUARTZ_DS_TRIGGHISTORY = "org.quartz.plugin.triggHistory.class";

	public static final String P_GATEWAY_REPPOOLSIZE = "bpm.vanilla.server.gateway.server.repositoryPoolSize";
	public static final String P_GATEWAY_REPORT_POOLSIZE = "bpm.vanilla.server.gateway.server.reportPoolSize";
	public static final String P_GATEWAY_MAXROWS = "bpm.vanilla.server.gateway.server.maxrows";
	public static final String P_GATEWAY_HOME_FOLDER = "bpm.vanilla.server.gateway.server.homeFolder";
	public static final String P_GATEWAY_TEMP_FOLDER = "bpm.vanilla.server.gateway.server.tempFolder";

	public static final String P_VANILLA_LOCAL_SERVER_PROTOCOLE = "bpm.vanilla.server.local.protocole";
	public static final String P_VANILLA_LOCAL_SERVER_IP = "bpm.vanilla.server.local.ip";
	public static final String P_VANILLA_LOCAL_SERVER_PORT = "bpm.vanilla.server.local.port";
	public static final String P_VANILLA_LOCAL_SERVER_CONTEXT = "bpm.vanilla.server.local.context";
	public static final String P_BIRT_LOG_LEVEL = "bpm.vanilla.reporting.birt.logLevel";
	public static final String P_COMPONENT_CONNECTION_TIMEOUT = "bpm.vanilla.component.connection.timeOut";
	public static final String P_AUTHENTICATION_TIMEOUT = "bpm.vanilla.authentication.timeOut";
	public static final String P_AUTHENTICATION_MAX_TRY = "bpm.vanilla.authentication.maxTry";
	public static final String P_AUTHENTICATION_MAX_TRY_BLOCKTIME = "bpm.vanilla.authentication.maxTry.blockTime";
	public static final String P_AUTHENTICATION_SORT_GROUP = "bpm.vanilla.authentication.sortGroup";

	public static final String P_VANILLA_USER_IMAGE_FOLDER = "bpm.vanilla.user.image.folder";
	public static final String P_VANILLA_DEFAULT_THEME = "bpm.vanilla.default.theme";
	public static final String P_VANILLA_FILES = "bpm.vanillafiles.path";
	public static final String P_VANILLA_IMAGE_FOLDER = "bpm.vanilla.image.folder";
	public static final String P_VANILLA_ALERT_ENABLED = "bpm.vanilla.alertEnabled";

	public static final String P_USE_BIRT_VIEWER = "bpm.vanilla.server.useBirtViewer";
	public static final String P_BIRT_VIEWER_URL = "bpm.vanilla.server.birt.url";
	public static final String P_BIRT_VIEWER_PATH = "bpm.vanilla.server.birt.path";
	public static final String P_BIRT_RESOURCE_PATH = "bpm.vanilla.server.birt.resource";

	public static final String P_MAIL_HOST = "bpm.vanilla.mailer.smtp.host";
	public static final String P_MAIL_PORT = "bpm.vanilla.mailer.smtp.port";
	public static final String P_MAIL_FROM = "bpm.vanilla.mailer.smtp.from";
	public static final String P_MAIL_AUTH = "bpm.vanilla.mailer.smtp.auth";
	public static final String P_MAIL_USER = "bpm.vanilla.mailer.smtp.user";
	public static final String P_MAIL_PASSWORD = "bpm.vanilla.mailer.smtp.password";
	public static final String P_MAIL_START_TLS_ENABLE = "bpm.vanilla.mailer.smtp.starttls.enable";

	public static final String P_WEBAPPS_FAWEB = "bpm.vanilla.webapps.url.faweb";
	public static final String P_WEBAPPS_FMLOADERWEB = "bpm.vanilla.webapps.url.fmloaderweb";
	public static final String P_WEBAPPS_FMUSERWEB = "bpm.vanilla.webapps.url.fmuserweb";
	public static final String P_WEBAPPS_FMDESIGNERWEB = "bpm.vanilla.webapps.url.fmdesignerweb";
	public static final String P_WEBAPPS_FDWEB = "bpm.vanilla.webapps.url.fdweb";
	public static final String P_WEBAPPS_FMDTWEB = "bpm.vanilla.webapps.url.metadataweb";
	public static final String P_WEBAPPS_FWR = "bpm.vanilla.webapps.url.fwr";
	public static final String P_WEBAPPS_ARCHITECTWEB = "bpm.vanilla.webapps.url.architectweb";
	public static final String P_WEBAPPS_DATAPREPARATION = "bpm.vanilla.webapps.url.datapreparation";
	public static final String P_WEBAPPS_AIR = "bpm.vanilla.webapps.url.air";
	public static final String P_WEBAPPS_HUB = "bpm.vanilla.webapps.url.hub";
	public static final String P_WEBAPPS_KpiMap = "bpm.vanilla.webapps.url.fmkpimapweb";
	public static final String P_WEBAPPS_PORTAL = "bpm.vanilla.webapps.url.portal";
	public static final String P_WEBAPPS_DECONNECT = "bpm.vanilla.webapps.url.deconnect";
	public static final String P_WEBAPPS_LOGIN_CSS = "bpm.vanilla.login.css.path";

	public static final String P_GED_INDEX_DIRECTORY = "bpm.ged.api.IndexDirectory";
	public static final String P_GED_SHOW_ALL_DOCUMENT = "bpm.ged.api.ShowAllDocument";
	public static final String P_VANILLA_LOG_LEVEL = "bpm.vanilla.log.level";

	public static final String P_SOLR_USESOLR = "bpm.solr.usesolr";
	public static final String P_SOLR_EMBEDDED = "bpm.solr.embedded";
	public static final String P_SOLR_CONFIG_PATH = "bpm.solr.configpath";
	public static final String P_SOLR_URL = "bpm.solr.url";

	public static final String P_VANILLA_SERVER_MAX_RUN_TASK = "bpm.vanilla.server.commons.server.maximumRunningTasks";
	public static final String P_VANILLA_SERVER_HISTO_FOLDER = "bpm.vanilla.server.historizationfolder";
	public static final String P_VANILLA_SERVER_REP_POOL_SIZE = "bpm.vanilla.server.reporting.server.repositoryPoolSize";
	public static final String P_VANILLA_SERVER_REPORT_POOL_SIZE = "bpm.vanilla.server.reporting.server.reportPoolSize";
	public static final String P_VANILLA_SERVER_GEN_FOLDER = "bpm.vanilla.server.reporting.server.generationFolder";
	public static final String P_VANILLA_SERVER_IMAGES = "bpm.vanilla.server.reporting.server.imagesUri";
	public static final String P_BIRT_REPORT_ENGINE_LOG = "bpm.vanilla.server.reporting.server.birtReportEngineLogs";
	public static final String P_VANILLA_SERVER_BACKGROUND_FOLDER = "bpm.vanilla.server.backgroundFolder";

	public static final String P_VANILLA_SESSION_TIME_OUT = "bpm.vanilla.session.timeOut";
	public static final String P_VANILLA_HELP_URL = "bpm.vanilla.help.url";

	public static final String P_VANILLA_LIMIT_VERSION_MODEL = "bpm.vanilla.repository.limit.version.model";

	public static final String P_LDAP_AUTHENTICATION_ACTIVE = "bpm.ldap.authentication.use";
	public static final String P_LDAP_AUTHENTICATION_URL = "bpm.ldap.authentication.url";
	public static final String P_LDAP_AUTHENTICATION_TYPE = "bpm.ldap.authentication.type";
	public static final String P_LDAP_AUTHENTICATION_USER_CONTAINER = "bpm.ldap.authentication.user.container";

	public static final String P_WORKFLOW_HOME = "bpm.vanilla.server.workflow.server.home";
	public static final String P_WORKFLOW_HISTORIC_TASK_FOLDER = "bpm.vanilla.server.workflow.server.historicTaskFolder";
	public static final String P_WORKFLOW_GENERATED_REPORTS_HOME = "bpm.vanilla.server.workflow.server.generatedReportHome";
	public static final String P_WORKFLOW_FILES = "bpm.vanilla.server.workflow.server.files";
	public static final String P_WORKFLOW_TEMPORARY_FILES = "bpm.vanilla.server.workflow.server.temporaryFiles";

	/**
	 * if this property is present and its value is true, the united Olap cache
	 * disk won't be used if the property does not exist or has any other value,
	 * the cacheDisk will be used
	 */
	public static final String P_DISABLE_UOLAP_CACHE_DISK = "bpm.united.olap.runtime.data.cache.disk.disabled";
	public static final String P_WEBSERVICE_URL = "bpm.web.service.url";
	public static final String P_REPOSITORY_FILE = "bpm.repository.context.file.";

	public static final String P_PUBLIC_GROUP_ID = "bpm.public.group.id";
	public static final String P_PUBLIC_REPOSITORY_ID = "bpm.public.repository.id";

	public static final String P_SCHEDULER_TIME_START = "bpm.scheduler.start.time";
	public static final String P_SCHEDULER_TIME_CHECK = "bpm.scheduler.check.time";
	public static final String P_SCHEDULER_RELAUNCH_TIME = "bpm.scheduler.relaunch.time";
	public static final String P_SCHEDULER_USE_UTC_TIME = "bpm.scheduler.use.utc.time";
	public static final String P_CUSTOM_PROPERTIES_PATH = "bpm.custom.properties.path";
	public static final String P_REPOSITORY_POOL_SIZE = "bpm.repository.connection.pool.size";
	
	public static final String P_CONNECTION_MANAGER_JDBC_POOLS_SIZE = "bpm.connection.manager.jdbc.pools.size";
	public static final String P_CONNECTION_MANAGER_JDBC_MAX_CONNECTIONS = "bpm.connection.manager.jdbc.max.connections";
	
	public static final String P_CONNECTION_MANAGER_ODA_POOLS_SIZE = "bpm.connection.manager.oda.pools.size";
	public static final String P_CONNECTION_MANAGER_ODA_MAX_CONNECTIONS = "bpm.connection.manager.oda.max.connections";

	public static final String P_HDFS_USE = "bpm.hdfs.use";
	public static final String P_HDFS_URL = "bpm.hdfs.default.fs";
	public static final String P_HDFS_USER = "bpm.hdfs.user";
	public static final String P_HDFS_USER_PATH = "bpm.hdfs.user.path";
	
	public static final String P_JDBC_XML_FILE = "bpm.vanilla.platform.jdbcDriverXmlFile";
	public static final String P_JDBC_DRIVER_FOLDER = "bpm.vanilla.platform.jdbcDriverFolder";
	
	public static final String P_VANILLA_DISCO_PACKAGE_FOLDER = "bpm.vanilla.disco.package.folder";
	
	public static final String P_DASHBOARD_EXPORT_WKHTMLTOPDF_PATH = "bpm.dashboard.export.wkhtmltopdf.path";
	public static final String P_DASHBOARD_EXPORT_WKHTMLTOIMAGE_PATH = "bpm.dashboard.export.wkhtmltoimage.path";
	public static final String P_DASHBOARD_EXPORT_DELAY = "bpm.dashboard.export.delay";
	public static final String P_DASHBOARD_EXPORT_ON_LINUX = "bpm.dashboard.export.onLinux";
	
	public static final String P_LOADER_EXCEL_DIRECTORY = "bpm.vanilla.loaderExcel.directory.id";
	
	public static final String P_CAS_ATTRIBUTE = "bpm.cas.login.attribut";
	
	public static final String P_FEEDBACK_ALLOWED = "bpm.vanilla.feedback.allowed";
	public static final String P_FEEDBACK_URL = "bpm.vanilla.feedback.url";
	public static final String P_UPDATE_MANAGER_URL = "bpm.update.manager.url";

	public static final String P_VANILLA_R_LIBRARIES_PATH = "bpm.vanilla.R.libraries.path";
	public static final String P_VISUALR_DIRECTORY = "bpm.vanilla.visualr.directory";

	public static final String P_LICENCE_PATH = "bpm.licence.path";
	public static final String P_FEEDBACK_CAN_SEND_MESSAGE = "bpm.vanilla.feedback.canSendMessage";
	
	//Vanilla Smart Data Properties
	public static final String P_SMART_RUNTIME_URL = "bpm.smart.runtime.url";
	public static final String P_SMART_R_SERVER_URL = "bpm.smart.r.server.url.";
	
	public static final String P_SMART_DB_DRIVERCLASSNAME = "bpm.vanilla.smart.database.driverClassName";
	public static final String P_SMART_DB_JDBCURL = "bpm.vanilla.smart.database.jdbcUrl";
	public static final String P_SMART_DB_USERNAME = "bpm.vanilla.smart.database.userName";
	public static final String P_SMART_DB_PASSWORD = "bpm.vanilla.smart.database.password";
	public static final String P_SMART_DB_DIALECT = "bpm.vanilla.smart.database.dialect";
	

	//Vanilla Hub Properties
	public static final String P_HUB_RUNTIME_URL = "bpm.hub.runtime.url";
	
	public static final String JDBC_XML_FILE = "bpm.jdbc.xml.path";
	public static final String HUB_FILE_PATH = "bpm.file.path";

	public static final String P_HUB_DB_DRIVERCLASSNAME = "bpm.vanillahub.database.driverClassName";
	public static final String P_HUB_DB_JDBCURL = "bpm.vanillahub.database.jdbcUrl";
	public static final String P_HUB_DB_USERNAME = "bpm.vanillahub.database.userName";
	public static final String P_HUB_DB_PASSWORD = "bpm.vanillahub.database.password";
	public static final String P_HUB_DB_DIALECT = "bpm.vanillahub.database.dialect";

	public static final String P_ARCHIVE_THREAD_DELAY = "bpm.vanilla.archive.thread.delay";
	
	public static final String P_ADMINISTRATION_IP_ALLOWED = "bpm.vanilla.administration.ip.allowed";
	public static final String P_ADMINISTRATION_PASSWORD_NEEDADMIN = "bpm.vanilla.administration.password.needadmin";
	public static final String P_FORBIDDEN_WORDS_FILE_PATH = "bpm.vanilla.administration.forbidden.words";
	
	public static final String P_VANILLA_CLIENT = "bpm.vanilla.client";

	public static final String P_CKAN_URL = "bpm.ckan.url";
	public static final String P_CKAN_API_KEY = "bpm.ckan.apiKey";
	public static final String P_CKAN_ORG = "bpm.ckan.org";
	public static final String P_D4C_URL = "bpm.d4c.url";
	public static final String P_D4C_LOGIN = "bpm.d4c.login";
	public static final String P_D4C_PASSWORD = "bpm.d4c.password";
	public static final String P_D4C_EXPORT_MAP = "bpm.d4c.export.map";
	
	public static final String P_WOPI_WS_URL = "bpm.wopi.ws.url";
	
	//Aklabox Properties
	public static final String P_AKLABOX_RUNTIME_URL = "bpm.aklabox.runtime.url";
	public static final String P_AKLABOX_WEBAPP_URL = "bpm.aklabox.webapp.url";

	//AutoLogin properties
	public static final String P_WEBAPPS_SSO_AUTOLOGIN = "bpm.vanilla.autoLogin.enabled";
	public static final String P_WEBAPPS_SSO_AUTHORIZED_IPS = "bpm.vanilla.autoLogin.ip.allowed";
	
	public static final String P_WEBAPPS_KEYCLOAK_ENABLED = "bpm.vanilla.keycloak.enabled";
	public static final String P_WEBAPPS_KEYCLOAK_KEY = "bpm.vanilla.keycloak.key";
	public static final String P_WEBAPPS_KEYCLOAK_CONFIG_URL = "bpm.vanilla.keycloak.configUrl";
	
	
	// LimeSurvey properties
	public static final String P_LIMESURVEY_URL = "bpm.limesurvey.url";
	public static final String P_LIMESURVEY_LOGIN = "bpm.limesurvey.login";
	public static final String P_LIMESURVEY_PASSWORD = "bpm.limesurvey.password";
	
	// KPI properties
	public static final String P_KPI_DATA_DB_SCHEMA = "bpm.vanilla.kpi.database.schema";
	public static final String P_KPI_DATA_DB_DRIVERCLASSNAME = "bpm.vanilla.kpi.database.driverClassName";
	public static final String P_KPI_DATA_DB_JDBCURL = "bpm.vanilla.kpi.database.jdbcUrl";
	public static final String P_KPI_DATA_DB_USERNAME = "bpm.vanilla.kpi.database.userName";
	public static final String P_KPI_DATA_DB_PASSWORD = "bpm.vanilla.kpi.database.password";
	
	// VMAP properties
	public static final String P_VMAP_DATA_DB_SCHEMA = "bpm.vanilla.vmap.database.schema";
	public static final String P_VMAP_DATA_DB_DRIVERCLASSNAME = "bpm.vanilla.vmap.database.driverClassName";
	public static final String P_VMAP_DATA_DB_JDBCURL = "bpm.vanilla.vmap.database.jdbcUrl";
	public static final String P_VMAP_DATA_DB_USERNAME = "bpm.vanilla.vmap.database.userName";
	public static final String P_VMAP_DATA_DB_PASSWORD = "bpm.vanilla.vmap.database.password";
	
	// Ckan Datastore properties
	public static final String P_CKAN_DATASTORE_DB_SCHEMA = "bpm.vanilla.ckan.datastore.database.schema";
	public static final String P_CKAN_DATASTORE_DB_DRIVERCLASSNAME = "bpm.vanilla.ckan.datastore.database.driverClassName";
	public static final String P_CKAN_DATASTORE_DB_JDBCURL = "bpm.vanilla.ckan.datastore.database.jdbcUrl";
	public static final String P_CKAN_DATASTORE_DB_USERNAME = "bpm.vanilla.ckan.datastore.database.userName";
	public static final String P_CKAN_DATASTORE_DB_PASSWORD = "bpm.vanilla.ckan.datastore.database.password";
	
	
	// SFTP properties
	public static final String P_SFTP_URL = "bpm.sftp.url";
	public static final String P_SFTP_PORT = "bpm.sftp.port";
	public static final String P_SFTP_LOGIN = "bpm.sftp.login";
	public static final String P_SFTP_PASSWORD = "bpm.sftp.password";
	
	
	
	/**
	 * The url of vanilla as defined in the config file
	 */
	private String vanillaUrl;
	
	/**
	 * The url principaly used for FD and Public URL (for docker)
	 */
	private String vanillaExternalUrl;

	private boolean useUrlRewrite;
	private Properties propsUrlRewrite;

	/**
	 * Props from bpm.vanilla.configurationFile
	 */
	private Properties configProperties;

	private Logger logger;
	private String fileName;

	public VanillaConfiguration(String configFile, Logger logger) throws ConfigurationException {
		this.fileName = configFile;
		this.logger = logger;
		logger.info("Loading configuration with " + ConfigurationConstants.configurationFile + "=" + configFile);
		try {
			FileInputStream fis = new FileInputStream(configFile);
			configProperties = new Properties();
			configProperties.load(fis);
			logger.info("Configuration ficonfigPropertiesle loaded.");

			vanillaUrl = getProperty(P_VANILLA_URL);
			vanillaExternalUrl = getProperty(P_VANILLA_URL_EXTERNAL);
		} catch (Exception ex) {
			String msg = "Failed to load configuration file @" + configFile + ", reason : " + ex.getMessage();
			logger.fatal(msg, ex);
			throw new ConfigurationException(msg, ex);
		}

		logger.info("Parsing configuration file");

		doUrlRewrite();

	}

	public String getProperty(String property) {
		//Checking in environnement variables
		String envValue = getEnvValue(property);
		if (envValue != null && !envValue.isEmpty()) {
			return envValue;
		}
		else if (configProperties != null) {
			return configProperties.getProperty(property);
		}
		else {
			return null;
		}
	}

	private String getEnvValue(String property) {
		String envValue = System.getenv(property);
		if (envValue != null && !envValue.isEmpty()) {
			return envValue;
		}
		
		envValue = System.getenv(transformProperty(property));
		return envValue;
	}

	private String transformProperty(String property) {
		if (property != null) {
			property = property.toUpperCase();
			property = property.replace(".", "_");
		}
		return property;
	}

	private void doUrlRewrite() {
		logger.info("Loading url rewriting info");

		String urlRewrite = getProperty(ConfigurationConstants.urlRewriteActive);
		if (urlRewrite != null && !urlRewrite.isEmpty()) {
			boolean bval = Boolean.parseBoolean(urlRewrite);
			logger.info("Using value=" + bval + " for key " + " " + ConfigurationConstants.urlRewriteActive);
			useUrlRewrite = bval;
		}
		else {
			String msg = "Configuration file @" + System.getProperty("bpm.vanilla.configurationFile") + ", has no value for key " + ConfigurationConstants.urlRewriteActive + ". Will use 'false' and ignore rules";
			logger.warn(msg);
			useUrlRewrite = false;
		}

		if (useUrlRewrite) {
			propsUrlRewrite = new Properties();
			for (int i = 0;; i++) {
				String sval = configProperties.getProperty(ConfigurationConstants.urlRewriteRule + i);
				if (sval == null) {
					break;// we re finished, only getting 'propName + 0',
							// 'propName + 1', etc
				}
				else {
					try {
						String[] tokens = sval.split(",");
						String key = tokens[0];
						String value = tokens[1];

						logger.info("Added rewrite rule : will be replacing " + key + " by " + value);
						propsUrlRewrite.put(key, value);
					} catch (Exception e) {
						logger.warn("Failed parse rewrite rule '" + ConfigurationConstants.urlRewriteRule + i + "'" + " with value " + sval + ", will ignore it");
					}
				}
			}
		}

	}

	/**
	 * 
	 * @param tmp
	 * @return
	 */
	private String replaceInString(String clientUrl) {
		String serverUrl = new String(clientUrl);
		boolean changed = false;
		for (String key : propsUrlRewrite.stringPropertyNames()) {
			if (serverUrl.contains(key)) {
				changed = true;
				serverUrl = serverUrl.replace(key, propsUrlRewrite.getProperty(key));
				logger.info("Server will use " + serverUrl + " instead of " + clientUrl);

			}
		}
		if (changed) {
			return serverUrl;
		}
		// not found, returning default
		return clientUrl;
	}

	/**
	 * Server side URL
	 * 
	 * @return
	 */
	public String getVanillaServerUrl() {
		return vanillaUrl;
	}
	
	public String getVanillaExternalUrl() {
		return vanillaExternalUrl != null && !vanillaExternalUrl.isEmpty() ? vanillaExternalUrl : vanillaUrl;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String translateClientUrlToServer(String url) {
		if (!useUrlRewrite) {
			return url;
		}

		String serverUrl = replaceInString(url);

		return serverUrl;
	}

	/**
	 * Principaly use for docker with FD or Public URL
	 * 
	 * @param url
	 * @return
	 */
	public String translateUrlToExternalUrl(String url) {
		if (vanillaExternalUrl == null || vanillaExternalUrl.isEmpty()) {
			return url;
		}

		logger.info("Server will use " + vanillaExternalUrl + " instead of " + vanillaUrl);
		return url.replace(vanillaUrl, vanillaExternalUrl);
	}

	/**
	 * should not be used except by RCP applications it is to update the
	 * configuration VanillaUrl withn the properties file
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, String propertyValue) {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(fileName);
			config.setProperty(propertyName, propertyValue);
			config.save();

			FileInputStream fis = new FileInputStream(fileName);
			configProperties = new Properties();
			configProperties.load(fis);
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("error saving vanilla properties file - " + ex.getMessage(), ex);
		}
	}

	public boolean isAlertEnabled() {
		if (getProperty(P_VANILLA_ALERT_ENABLED) == null) {
			return false;
		}
		try {
			return Boolean.valueOf(getProperty(P_VANILLA_ALERT_ENABLED));
		} catch (Exception ex) {
			return false;
		}
	}

	public Customer getCustomer() {
		String client = getProperty(P_VANILLA_CLIENT);
		return Customer.fromValue(client);
	}
}
