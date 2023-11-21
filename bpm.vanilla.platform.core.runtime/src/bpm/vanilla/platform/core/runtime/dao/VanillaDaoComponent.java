package bpm.vanilla.platform.core.runtime.dao;

import org.osgi.framework.Bundle;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.runtime.dao.external.ExternalDao;
import bpm.vanilla.platform.core.runtime.dao.ged.CategoryDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DefinitionDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocCatDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentVersionDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.SecurityDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.StoredFieldDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.BiwLogsDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.FmdtQueriesLoggerDao;
import bpm.vanilla.platform.core.runtime.dao.logs.LogDao;
import bpm.vanilla.platform.core.runtime.dao.logs.UolapQueriesLoggerDao;
import bpm.vanilla.platform.core.runtime.dao.logs.VanillaLogsDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.VanillaLogsPropsDAO;
import bpm.vanilla.platform.core.runtime.dao.massreport.MassReportMonitorDao;
import bpm.vanilla.platform.core.runtime.dao.platform.RepositoryDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.RuntimeComponentDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.SessionDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaLocaleDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaSetupDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaVersionDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VariableDAO;
import bpm.vanilla.platform.core.runtime.dao.preferences.OpenPreferenceDAO;
import bpm.vanilla.platform.core.runtime.dao.preferences.UserRunConfigurationDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.FmdtUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.PublicParameterDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.PublicUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.resource.ResourceDao;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobDAO;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobInstanceDAO;
import bpm.vanilla.platform.core.runtime.dao.security.AccessRequestDao;
import bpm.vanilla.platform.core.runtime.dao.security.GroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.GroupProjectionDAO;
import bpm.vanilla.platform.core.runtime.dao.security.RoleDAO;
import bpm.vanilla.platform.core.runtime.dao.security.RoleGroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.SecurityLogDAO;
import bpm.vanilla.platform.core.runtime.dao.security.SettingsDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserGroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserRepDAO;
import bpm.vanilla.platform.core.runtime.dao.service.VanillaWebServiceDAO;
import bpm.vanilla.platform.core.runtime.dao.unitedolap.UnitedOlapPreloadDAO;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsGroupDao;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsLogsDAO;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class VanillaDaoComponent implements IVanillaDaoComponent {

	private IVanillaComponentListenerService vanillaListener;

	/**
	 * @return the biwLogDao
	 */
	public BiwLogsDAO getBiwLogsDao() {
		return biwLogDao;
	}

	/**
	 * @return the logDao
	 */
	public LogDao getLogDao() {
		return logDao;
	}

	/**
	 * @return the vanillaLogDao
	 */
	public VanillaLogsDAO getVanillaLogsDao() {
		return vanillaLogDao;
	}

	/**
	 * @return the uolapQueryLogDao
	 */
	public UolapQueriesLoggerDao getUolapQueryLogDao() {
		return uolapQueryLogDao;
	}

	/**
	 * @return the uolapQueryLogDao
	 */
	public FmdtQueriesLoggerDao getFmdtQueryLogDao() {
		return fmdtQueryLogDao;
	}

	/**
	 * @return the vanillaLogPropsDao
	 */
	public VanillaLogsPropsDAO getVanillaLogsPropsDao() {
		return vanillaLogPropsDao;
	}

	/**
	 * @return the serverDao
	 */
	public RuntimeComponentDAO getServerDao() {
		return serverDao;
	}

	/**
	 * @return the repositoryDao
	 */
	public RepositoryDAO getRepositoryDao() {
		return repositoryDao;
	}

	/**
	 * @return the vanillaSetupDao
	 */
	public VanillaSetupDAO getVanillaSetupDao() {
		return vanillaSetupDao;
	}

	/**
	 * @return the vanillaVersionDao
	 */
	public VanillaVersionDAO getVanillaVersionDao() {
		return vanillaVersionDao;
	}

	/**
	 * @return the variableDao
	 */
	public VariableDAO getVariableDao() {
		return variableDao;
	}

	/**
	 * @return the sessionDao
	 */
	public SessionDAO getSessionDao() {
		return sessionDao;
	}

	/**
	 * @return the openPreferencesDao
	 */
	public OpenPreferenceDAO getOpenPreferencesDao() {
		return openPreferencesDao;
	}

	/**
	 * @return the fmdtUrlDao
	 */
	public FmdtUrlDAO getFmdtUrlDao() {
		return fmdtUrlDao;
	}

	/**
	 * @return the publicUrlDao
	 */
	public PublicUrlDAO getPublicUrlDao() {
		return publicUrlDao;
	}

	/**
	 * @return the publicParameterDao
	 */
	public PublicParameterDAO getPublicParameterDao() {
		return publicParameterDao;
	}

	/**
	 * @return the userDao
	 */
	public UserDAO getUserDao() {
		return userDao;
	}

	/**
	 * @return the groupDao
	 */
	public GroupDAO getGroupDao() {
		return groupDao;
	}

	/**
	 * @return the roleDao
	 */
	public RoleDAO getRoleDao() {
		return roleDao;
	}

	/**
	 * @return the userGroupDao
	 */
	public UserGroupDAO getUserGroupDao() {
		return userGroupDao;
	}

	/**
	 * @return the roleGroupDao
	 */
	public RoleGroupDAO getRoleGroupDao() {
		return roleGroupDao;
	}

	/**
	 * @return the userRepDao
	 */
	public UserRepDAO getUserRepDao() {
		return userRepDao;
	}

	/**
	 * @return the loggingService
	 */
	public IVanillaLoggerService getLoggingService() {
		return loggingService;
	}

	/*
	 * Log Daos
	 */
	private static final String BEAN_BIW_LOG_DAO = "biwLogsDAO";
	private static final String BEAN_LOG_DAO = "logDAO";
	private static final String BEAN_VANILLA_LOG_DAO = "vanillaLogsDAO";
	private static final String BEAN_VANILLA_LOG_PROPS_DAO = "vanillaLogsPropsDAO";
	private static final String BEAN_UOLAP_QUERY_LOG_DAO = "uolapQueryLogDAO";
	private static final String BEAN_FMDT_QUERY_LOG_DAO = "fmdtQueryLogDAO";

	private BiwLogsDAO biwLogDao;
	private LogDao logDao;
	private VanillaLogsDAO vanillaLogDao;
	private VanillaLogsPropsDAO vanillaLogPropsDao;
	private UolapQueriesLoggerDao uolapQueryLogDao;
	private FmdtQueriesLoggerDao fmdtQueryLogDao;
	private bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsLogsDAO WidgetsLogsDAO;
	private bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsGroupDao WidgetsGroupDAO;
	private bpm.vanilla.platform.core.runtime.dao.widgets.WidgetPositionDAO WidgetPositionDAO;
	/*
	 * Platform Daos
	 */
	private static final String BEAN_SERVER_DAO = "serverDAO";
	private static final String BEAN_REPOSITORY_DAO = "repositoryDAO";
	private static final String BEAN_VANILLA_SETUP_DAO = "vanillaSetupDAO";
	private static final String BEAN_VANILLA_VERSION_DAO = "vanillaVersionDAO";
	private static final String BEAN_SESSION_DAO = "sessionDAO";
	private static final String BEAN_VARIABLE_DAO = "variableDAO";
	private static final String BEAN_VANILLA_LOCALE = "vanillaLocaleDAO";

	private RuntimeComponentDAO serverDao;
	private RepositoryDAO repositoryDao;
	private VanillaSetupDAO vanillaSetupDao;
	private VanillaVersionDAO vanillaVersionDao;
	private VariableDAO variableDao;
	private SessionDAO sessionDao;
	private VanillaLocaleDAO vanillaLocaleDao;

	/*
	 * Preferences Dao
	 */
	private static final String BEAN_OPEN_PREFERENCES_DAO = "openPreferenceDAO";
	private static final String BEAN_LOOK_PREFERENCES_DAO = "lookPreferenceDAO";

	// private LookPreferenceDAO lookPreferencesDao;
	private OpenPreferenceDAO openPreferencesDao;

	/*
	 * PublicAccess Daos
	 */

	private static final String BEAN_FMDT_URL_DAO = "fmdtUrlDAO";
	private static final String BEAN_ADRESSABLE_DAO = "objectAdressableDAO";
	private static final String BEAN_SECURITY_ADRESSABLE_DAO = "securedObjectAdressableDAO";
	private static final String BEAN_PUBLIC_URL_DAO = "publicUrlDAO";
	private static final String BEAN_PUBLIC_URL_PARAMETER_DAO = "publicParameterDAO";

	private FmdtUrlDAO fmdtUrlDao;
	private PublicUrlDAO publicUrlDao;
	private PublicParameterDAO publicParameterDao;

	/*
	 * Security Daos
	 */

	private static final String BEAN_USER_DAO = "userDAO";
	private static final String BEAN_GROUP_DAO = "groupDAO";
	private static final String BEAN_ROLE_DAO = "roleDAO";
	private static final String BEAN_USER_GROUP_DAO = "userGroupDAO";
	private static final String BEAN_ROLE_GROUP_DAO = "roleGroupDAO";
	private static final String BEAN_USER_REPOSITORY_DAO = "userRepDAO";
	private static final String BEAN_ACCESS_REQUEST = "accessRequestDAO";
	private static final String BEAN_SECURITY_LOG_DAO = "securityLogDAO";
	private static final String BEAN_SETTINGS_DAO = "settingsDAO";
	private UserDAO userDao;
	private GroupDAO groupDao;
	private RoleDAO roleDao;
	private UserGroupDAO userGroupDao;
	private RoleGroupDAO roleGroupDao;
	private UserRepDAO userRepDao;
	private AccessRequestDao accessRequestDao;
	private SecurityLogDAO securityLogDAO;
	private SettingsDAO settingsDAO;

	// Historic dao (also used by GED)
	private static final String BEAN_HISTORIC_DOCUMENT_DAO = "histoDocumentDao";
	private static final String BEAN_HISTORIC_SECURITY_DAO = "histoSecurityDao";
	private static final String BEAN_GED_DAO_DOCUMENTVERSION = "histoDocumentVersionDao";

	private DocumentDAO documentDao;
	private DocumentVersionDAO documentVersionDAO;
	private SecurityDAO securityDao;

	public DocumentVersionDAO getDocumentVersionDAO() {
		return documentVersionDAO;
	}

	/*
	 * unitedOlap
	 */
	private static final String BEAN_UNITED_OLAP_PRELOAD_DAO = "unitedOlapPreloadDAO";
	private static final String BEAN_USER_RUN_CONFIG_DAO = "userRunConfigurationDao";
	private static final String BEAN_GROUP_PROJECTION_DAO = "groupProjectionDAO";
	private UnitedOlapPreloadDAO unitedOlapPreloadDao;

	/*
	 * massreprot
	 */
	private static final String BEAN_MASS_REPORT_MANAGER = "massReportDAO";
	private MassReportMonitorDao massReportDao;

	private static int sessionTimeOut = 60000;

	private IVanillaLoggerService loggingService = null;
	private UserRunConfigurationDAO userRunConfigurationDao;
	private GroupProjectionDAO groupProjectionDAO;

	// GED dao
	private static final String BEAN_GED_DAO_CATEGORY = "gedCategoryDao";
	private static final String BEAN_GED_DAO_DEFINITION = "gedDefinitionDao";
	private static final String BEAN_GED_DAO_DOCCAT = "gedDocCatDao";
	private static final String BEAN_GED_DAO_STOREDFIELD = "gedStoredFieldDao";

	private CategoryDAO gedCategoryDao;
	private DefinitionDAO gedDefinitionDao;
	private DocCatDAO gedDocCatDao;
	private StoredFieldDAO gedStoredFieldDao;

	public static final String BEAN_SCHEDULER_JOB = "jobDAO";
	public static final String BEAN_SCHEDULER_INSTANCE = "jobInstanceDAO";
	private static final String BEAN_WEB_SERVICE = "vanillaWebServiceDAO";

	private JobDAO jobDAO;
	private JobInstanceDAO jobInstanceDAO;
	private VanillaWebServiceDAO vanillaWebServiceDAO;

	private static final String BEAN_COMMENT = "commentService";
	private static final String BEAN_COMMENT_DEFINITION = "commentDefinitionService";
	
	// Resource DAO
	public static final String BEAN_RESOURCE_DAO = "resourceDao";
	public static final String BEAN_EXTERNAL_DAO = "externalDao";
	
	private ResourceDao resourceDao;
	private ExternalDao externalDao;
	
	// Global DAO
	public static final String BEAN_GLOBAL_DAO = "globalDao";
	
	private GlobalDAO globalDAO;

	public void bind(IVanillaLoggerService service) {
		this.loggingService = service;
	}

	public void unbind(IVanillaLoggerService service) {
		this.loggingService = null;
	}

	public void bind(IVanillaComponentListenerService service) {
		this.vanillaListener = service;
	}

	public void unbind(IVanillaComponentListenerService service) {
		this.vanillaListener = null;
	}

	public void activate() {
		try {
			init();
		} catch (Throwable e) {
			getLogger().error("Unable to init VanillaDaoComponent - " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void deactivate() {
		this.jobDAO = null;
		this.jobInstanceDAO = null;
		this.biwLogDao = null;
		this.logDao = null;
		this.vanillaLogDao = null;
		this.vanillaLogPropsDao = null;
		this.uolapQueryLogDao = null;
		this.fmdtQueryLogDao = null;
		this.WidgetsLogsDAO = null;
		this.WidgetsGroupDAO = null;
		this.WidgetPositionDAO = null;
		this.serverDao = null;
		this.repositoryDao = null;
		this.vanillaSetupDao = null;
		this.vanillaVersionDao = null;
		this.variableDao = null;
		this.sessionDao = null;
		this.vanillaLocaleDao = null;
		this.openPreferencesDao = null;
		this.userRunConfigurationDao = null;
		this.groupProjectionDAO = null;
		this.fmdtUrlDao = null;
		this.publicUrlDao = null;
		this.publicParameterDao = null;
		this.userDao = null;
		this.groupDao = null;
		this.roleDao = null;
		this.userGroupDao = null;
		this.roleGroupDao = null;
		this.userRepDao = null;
		this.accessRequestDao = null;
		this.unitedOlapPreloadDao = null;
		this.documentDao = null;
		this.documentVersionDAO = null;
		this.securityDao = null;
		this.gedCategoryDao = null;
		this.gedDefinitionDao = null;
		this.gedDocCatDao = null;
		this.gedStoredFieldDao = null;
		this.massReportDao = null;
		this.vanillaWebServiceDAO = null;
		this.securityLogDAO = null;
		this.settingsDAO = null;
		this.resourceDao = null;
		this.externalDao = null;
		this.globalDAO = null;
	}

	public IVanillaLogger getLogger() {
		return loggingService.getLogger(VanillaDaoComponent.class.getName());
	}

	public static int getVanillaSessionTimeOut() {
		return sessionTimeOut;
	}

	private void init() throws Throwable {
		ApplicationContext factory = new ClassPathXmlApplicationContext("/bpm/vanilla/platform/core/runtime/dao/bpm.vanilla.core.context.xml") {

			protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
				super.initBeanDefinitionReader(reader);
				reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
				Bundle bundle = org.eclipse.core.runtime.Platform.getBundle("bpm.vanilla.platform.core.runtime");
				// This the important line and available since Equinox 3.7
				ClassLoader loader = VanillaDaoComponent.this.getClass().getClassLoader();
				// ClassLoader loader =
				// bundle.adapt(BundleWiring.class).getClassLoader();
				reader.setBeanClassLoader(loader);
			}

		};

		try {

			jobDAO = (JobDAO) factory.getBean(BEAN_SCHEDULER_JOB);
			jobInstanceDAO = (JobInstanceDAO) factory.getBean(BEAN_SCHEDULER_INSTANCE);

		} catch (Exception e) {
			getLogger().error("Error when loading SCHEDULER Dao's spring beans", e);
			e.printStackTrace();
		}

		try {
			biwLogDao = (BiwLogsDAO) factory.getBean(BEAN_BIW_LOG_DAO);
			logDao = (LogDao) factory.getBean(BEAN_LOG_DAO);
			vanillaLogDao = (VanillaLogsDAO) factory.getBean(BEAN_VANILLA_LOG_DAO);
			vanillaLogPropsDao = (VanillaLogsPropsDAO) factory.getBean(BEAN_VANILLA_LOG_PROPS_DAO);
			uolapQueryLogDao = (UolapQueriesLoggerDao) factory.getBean(BEAN_UOLAP_QUERY_LOG_DAO);
			fmdtQueryLogDao = (FmdtQueriesLoggerDao) factory.getBean(BEAN_FMDT_QUERY_LOG_DAO);

			WidgetsLogsDAO = (bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsLogsDAO) factory.getBean("WidgetsDAO");
			WidgetsGroupDAO = (bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsGroupDao) factory.getBean("WidgetsGroupDAO");
			WidgetPositionDAO = (bpm.vanilla.platform.core.runtime.dao.widgets.WidgetPositionDAO) factory.getBean("WidgetPositionDAO");
			getLogger().info("Spring Logs Dao's Beans loaded");
		} catch (Exception ex) {
			getLogger().error("Error when loading Logs Dao's spring beans", ex);
			throw ex;
		}

		try {
			serverDao = new RuntimeComponentDAO(vanillaListener);
			repositoryDao = (RepositoryDAO) factory.getBean(BEAN_REPOSITORY_DAO);
			vanillaSetupDao = (VanillaSetupDAO) factory.getBean(BEAN_VANILLA_SETUP_DAO);
			vanillaVersionDao = (VanillaVersionDAO) factory.getBean(BEAN_VANILLA_VERSION_DAO);
			variableDao = (VariableDAO) factory.getBean(BEAN_VARIABLE_DAO);
			sessionDao = (SessionDAO) factory.getBean(BEAN_SESSION_DAO);
			vanillaLocaleDao = (VanillaLocaleDAO) factory.getBean(BEAN_VANILLA_LOCALE);
			getLogger().info("Spring Platform Dao's Beans loaded");
		} catch (Exception ex) {
			getLogger().error("Error when loading Platform Dao's spring beans", ex);

			throw ex;
		}

		try {
			// lookPreferencesDao =
			// (LookPreferenceDAO)factory.getBean(BEAN_LOOK_PREFERENCES_DAO);
			openPreferencesDao = (OpenPreferenceDAO) factory.getBean(BEAN_OPEN_PREFERENCES_DAO);
			userRunConfigurationDao = (UserRunConfigurationDAO) factory.getBean(BEAN_USER_RUN_CONFIG_DAO);
			groupProjectionDAO = (GroupProjectionDAO) factory.getBean(BEAN_GROUP_PROJECTION_DAO);
			getLogger().info("Spring Preferences Dao's Beans loaded");
		} catch (Exception ex) {
			getLogger().error("Error when loading Preferences Dao's spring beans", ex);

			throw ex;
		}
		try {
			fmdtUrlDao = (FmdtUrlDAO) factory.getBean(BEAN_FMDT_URL_DAO);
			publicUrlDao = (PublicUrlDAO) factory.getBean(BEAN_PUBLIC_URL_DAO);
			publicParameterDao = (PublicParameterDAO) factory.getBean(BEAN_PUBLIC_URL_PARAMETER_DAO);
			getLogger().info("Spring Accessibility Dao's Beans loaded");
		} catch (Exception ex) {
			getLogger().error("Error when loading Accessibility Dao's spring beans", ex);

			throw ex;
		}
		try {
			userDao = (UserDAO) factory.getBean(BEAN_USER_DAO);
			groupDao = (GroupDAO) factory.getBean(BEAN_GROUP_DAO);
			roleDao = (RoleDAO) factory.getBean(BEAN_ROLE_DAO);
			userGroupDao = (UserGroupDAO) factory.getBean(BEAN_USER_GROUP_DAO);
			roleGroupDao = (RoleGroupDAO) factory.getBean(BEAN_ROLE_GROUP_DAO);
			userRepDao = (UserRepDAO) factory.getBean(BEAN_USER_REPOSITORY_DAO);
			accessRequestDao = (AccessRequestDao) factory.getBean(BEAN_ACCESS_REQUEST);
			securityLogDAO = (SecurityLogDAO) factory.getBean(BEAN_SECURITY_LOG_DAO);
			securityLogDAO.setUserDao(userDao);
			settingsDAO = (SettingsDAO) factory.getBean(BEAN_SETTINGS_DAO);
			getLogger().info("Spring Security Dao's Beans loaded");
		} catch (Exception ex) {
			getLogger().error("Error when loading Security Dao's spring beans", ex);

			throw ex;
		}

		try {
			unitedOlapPreloadDao = (UnitedOlapPreloadDAO) factory.getBean(BEAN_UNITED_OLAP_PRELOAD_DAO);
		} catch (Exception ex) {
			getLogger().error("Error when loading United Olap Dao's spring beans", ex);

			throw ex;
		}

		try {
			documentDao = (DocumentDAO) factory.getBean(BEAN_HISTORIC_DOCUMENT_DAO);
			documentVersionDAO = (DocumentVersionDAO) factory.getBean(BEAN_GED_DAO_DOCUMENTVERSION);
			securityDao = (SecurityDAO) factory.getBean(BEAN_HISTORIC_SECURITY_DAO);

			// Pass the versionDAO to the documentDAO
			documentDao.setVersionDao(documentVersionDAO);
			documentDao.setSecurityDao(securityDao);

		} catch (Exception ex) {
			getLogger().error("Error when loading Historic Dao's spring beans", ex);

			throw ex;
		}

		try {
			gedCategoryDao = (CategoryDAO) factory.getBean(BEAN_GED_DAO_CATEGORY);
			gedDefinitionDao = (DefinitionDAO) factory.getBean(BEAN_GED_DAO_DEFINITION);
			gedDocCatDao = (DocCatDAO) factory.getBean(BEAN_GED_DAO_DOCCAT);
			gedStoredFieldDao = (StoredFieldDAO) factory.getBean(BEAN_GED_DAO_STOREDFIELD);
		} catch (Exception e) {
			getLogger().error("Error when loading GED Dao's spring beans", e);
			e.printStackTrace();
		}

		try {
			massReportDao = (MassReportMonitorDao) factory.getBean(BEAN_MASS_REPORT_MANAGER);
		} catch (Exception ex) {
			getLogger().error("Error when loading MassReportMonitorManager's spring beans", ex);

			throw ex;
		}

		try {
			vanillaWebServiceDAO = (VanillaWebServiceDAO) factory.getBean(BEAN_WEB_SERVICE);
		} catch (Exception ex) {
			getLogger().error("Error when loading vanillaWebServiceDAO spring beans", ex);

			throw ex;
		}

		try {
			resourceDao = (ResourceDao) factory.getBean(BEAN_RESOURCE_DAO);
			externalDao = (ExternalDao) factory.getBean(BEAN_EXTERNAL_DAO);
		} catch (Exception ex) {
			getLogger().error("Error when loading Resource's dao spring beans", ex);

			throw ex;
		}

		try {
			globalDAO = (GlobalDAO) factory.getBean(BEAN_GLOBAL_DAO);
		} catch (Exception ex) {
			getLogger().error("Error when loading Global dao spring beans", ex);

			throw ex;
		}
	}

	@Override
	public UnitedOlapPreloadDAO getUnitedOlapPreloadDao() {
		return unitedOlapPreloadDao;
	}

	@Override
	public UserRunConfigurationDAO getUserRunConfigurationDao() {
		return userRunConfigurationDao;
	}

	@Override
	public GroupProjectionDAO getGroupProjectionDAO() {
		return groupProjectionDAO;
	}

	@Override
	public AccessRequestDao getAccessRequestDao() {
		return accessRequestDao;
	}

	@Override
	public DocumentDAO getDocumentDao() {
		return documentDao;
	}

	@Override
	public SecurityDAO getSecurityDao() {
		return securityDao;
	}

	@Override
	public CategoryDAO getGedCategoryDao() {
		return gedCategoryDao;
	}

	@Override
	public DefinitionDAO getGedDefinitionDao() {
		return gedDefinitionDao;
	}

	@Override
	public DocCatDAO getGedDocCatDao() {
		return gedDocCatDao;
	}

	@Override
	public StoredFieldDAO getGedStoredFieldDao() {
		return gedStoredFieldDao;
	}

	@Override
	public MassReportMonitorDao getMassReportMonitorDao() {
		return massReportDao;
	}

	public WidgetsLogsDAO getWidgetsDao() {
		return WidgetsLogsDAO;
	}

	public WidgetsGroupDao getWidgetsGroupDAO() {
		return WidgetsGroupDAO;
	}

	public bpm.vanilla.platform.core.runtime.dao.widgets.WidgetPositionDAO getWidgetPositionDAO() {
		return WidgetPositionDAO;
	}

	public JobDAO getJobDAO() {
		return jobDAO;
	}

	public JobInstanceDAO getJobInstanceDAO() {
		return jobInstanceDAO;
	}

	public VanillaWebServiceDAO getWebServiceDAO() {
		return vanillaWebServiceDAO;
	}

	public VanillaLocaleDAO getVanillaLocaleDao() {
		return vanillaLocaleDao;
	}

	@Override
	public SecurityLogDAO getSecurityLogDao() {
		return securityLogDAO;
	}

	@Override
	public SettingsDAO getSettingsDao() {
		return settingsDAO;
	}

	@Override
	public ResourceDao getResourceDAO() {
		return resourceDao;
	}

	@Override
	public ExternalDao getExternalDao() {
		return externalDao;
	}
	
	@Override
	public GlobalDAO getGlobalDAO() {
		return globalDAO;
	}
}
