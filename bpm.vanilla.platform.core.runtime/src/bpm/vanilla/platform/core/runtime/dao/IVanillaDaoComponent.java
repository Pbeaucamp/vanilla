package bpm.vanilla.platform.core.runtime.dao;

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
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetPositionDAO;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsGroupDao;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsLogsDAO;

public interface IVanillaDaoComponent {
	public BiwLogsDAO getBiwLogsDao();
	public LogDao getLogDao();
	public VanillaLogsDAO getVanillaLogsDao();
	public VanillaLogsPropsDAO getVanillaLogsPropsDao();
	
	public RuntimeComponentDAO getServerDao();
	public RepositoryDAO getRepositoryDao();
	public VanillaSetupDAO getVanillaSetupDao();
	public VanillaVersionDAO getVanillaVersionDao();
	public VariableDAO getVariableDao();
	public SessionDAO getSessionDao();
	
	public OpenPreferenceDAO getOpenPreferencesDao();
	
	public FmdtUrlDAO getFmdtUrlDao();
	public PublicUrlDAO getPublicUrlDao();
	public PublicParameterDAO getPublicParameterDao();
		
	public UserDAO getUserDao();
	public GroupDAO getGroupDao();
	public RoleDAO getRoleDao();
	public UserGroupDAO getUserGroupDao();
	public RoleGroupDAO getRoleGroupDao();
	public UserRepDAO getUserRepDao();
	public AccessRequestDao getAccessRequestDao();
	public GroupProjectionDAO getGroupProjectionDAO();
	public UnitedOlapPreloadDAO getUnitedOlapPreloadDao();
	public SecurityLogDAO getSecurityLogDao();
	public SettingsDAO getSettingsDao();
	
	public UserRunConfigurationDAO getUserRunConfigurationDao();
	
	public DocumentDAO getDocumentDao();
	public SecurityDAO getSecurityDao();
	public DocCatDAO getGedDocCatDao();
	public StoredFieldDAO getGedStoredFieldDao();
	public CategoryDAO getGedCategoryDao();
	public DefinitionDAO getGedDefinitionDao();
	
	public MassReportMonitorDao getMassReportMonitorDao();
	
	public JobDAO getJobDAO();
	public JobInstanceDAO getJobInstanceDAO();
	
	public VanillaWebServiceDAO getWebServiceDAO();
	public ResourceDao getResourceDAO();
	public DocumentVersionDAO getDocumentVersionDAO();
	public WidgetsLogsDAO getWidgetsDao();
	public WidgetsGroupDao getWidgetsGroupDAO();
	public WidgetPositionDAO getWidgetPositionDAO();
	public FmdtQueriesLoggerDao getFmdtQueryLogDao();
	public UolapQueriesLoggerDao getUolapQueryLogDao();
	public VanillaLocaleDAO getVanillaLocaleDao();

	public ExternalDao getExternalDao();
	public GlobalDAO getGlobalDAO();

}
