package bpm.vanilla.platform.core.wrapper;

import java.io.File;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.vanilla.platform.core.IArchiveManager;
import bpm.vanilla.platform.core.ICommentService;
import bpm.vanilla.platform.core.IExcelManager;
import bpm.vanilla.platform.core.IExternalAccessibilityManager;
import bpm.vanilla.platform.core.IExternalManager;
import bpm.vanilla.platform.core.IGlobalManager;
import bpm.vanilla.platform.core.IImageManager;
import bpm.vanilla.platform.core.IMassReportMonitor;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.ISchedulerManager;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.IVanillaLoggingManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.commands.IVanillaCommandManager;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.HTMLFormComponent;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.wrapper.command.FolderHistorizationCommand;
import bpm.vanilla.platform.core.wrapper.dispatcher.ServletDispatcher;
import bpm.vanilla.platform.core.wrapper.pictures.PictureHttpContext;
import bpm.vanilla.platform.core.wrapper.servlets40.ArchiveManagerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.AutoLoginServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.BigFileGedServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.CommentServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExcelServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExternalAccessibilityServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExternalCallObjectParamServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExternalCallObjectServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExternalManagerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ExternalVanillaAccessServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.GatewayMonitorServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.GedIndexServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.GlobalManagerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ImageManagerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.MassReportMonitorServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.PreferencesServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ReportHistoricServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.RepositoryServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.ResourceManagerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.SchedulerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaAccessRequestServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaHTMLFormListerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaListenerServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaLoggingServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaParameterServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaSecurityServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaSystemServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaUnitedOlapPreloadServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaWebServiceServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.WorkflowMonitorServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.FmdtExcelServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.FmdtJdbcServlet;
import bpm.vanilla.platform.core.wrapper.servlets40.command.ServletMassHistorization;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;
import bpm.vanilla.web.service.IVanillaWebService;
import bpm.vanilla.web.service.VanillaWebService;

public class VanillaCoreWrapper implements IVanillaComponentProvider, IVanillaCommandManager {
	private static final String PLUGIN_ID = "bpm.vanilla.platform.core.wrapper";
	private HttpService httpService;
	private IVanillaLogger logger;

	private IExternalAccessibilityManager externalAccessibilityManager;
	private IPreferencesManager preferenceManager;
	private IRepositoryManager repositoryManager;
	private IVanillaLoggingManager loggingManager;
	private IVanillaSecurityManager securityManager;
	private IVanillaSystemManager systemManager;
	private IUnitedOlapPreloadManager unitedOlapPreloadManager;
	private IVanillaComponentListenerService listenerComponent;
	private VanillaParameterComponent parameterComponent;
	private IVanillaAccessRequestManager accessRequestManager;
	private ReportHistoricComponent historicComponent;
	private IGedComponent gedIndexComponent;
	private ISchedulerManager schedulerManager;
	private ICommentService commentComponent;
	private IExcelManager excelManager;
	private IMassReportMonitor massReportMonitor;
	private IImageManager imageManager;
	private IArchiveManager archiveManager;
	private IResourceManager resourceManager;
	private IGlobalManager globalManager;
	private IExternalManager externalManager;

	private Status status = Status.UNDEFINED;
	
	private FolderHistorizationCommand massHistorizationCmd;
	private volatile Boolean shouldMassHistorize = Boolean.FALSE;
	private IVanillaWebServiceComponent vanillaWebServiceComponent;
	private ComponentContext context;

	public VanillaCoreWrapper() {

		VanillaMBeanManager.createMBean(this);
	}

	public void bind(IVanillaComponentListenerService service) {
		listenerComponent = service;
	}

	public void bind(IUnitedOlapPreloadManager service) {
		unitedOlapPreloadManager = service;
	}

	public void bind(IExternalAccessibilityManager service) {
		externalAccessibilityManager = service;
	}

	public void bind(IVanillaSystemManager service) {
		systemManager = service;
	}

	public void bind(IVanillaSecurityManager service) {
		securityManager = service;
	}

	public void bind(IVanillaLoggingManager service) {
		loggingManager = service;
	}

	public void bind(IRepositoryManager service) {
		repositoryManager = service;
	}

	public void bind(IPreferencesManager service) {
		preferenceManager = service;
	}

	public void bind(IVanillaWebServiceComponent service) {
		vanillaWebServiceComponent = service;
	}

	public void unbind(IVanillaWebServiceComponent service) {
		vanillaWebServiceComponent = null;
	}

	public void bind(VanillaParameterComponent service) {
		parameterComponent = service;
	}

	public void bind(IVanillaAccessRequestManager service) {
		accessRequestManager = service;
	}

	public void bind(ReportHistoricComponent service) {
		historicComponent = service;
	}

	public void unbind(ReportHistoricComponent service) {
		historicComponent = null;
	}

	public void bind(IGedComponent service) {
		gedIndexComponent = service;
	}

	public void unbind(IGedComponent service) {
		gedIndexComponent = null;
	}

	public void bind(ICommentService service) {
		this.commentComponent = service;
	}

	public void unbind(ICommentService service) {
		this.commentComponent = null;
	}
	
	public void bind(IExcelManager service) {
		excelManager = service;
	}

	public void unbind(IExcelManager service) {
		this.excelManager = null;
	}
	
	public void bind(IArchiveManager service) {
		archiveManager = service;
	}

	public void unbind(IArchiveManager service) {
		this.archiveManager = null;
	}
	
	public void bind(IResourceManager service) {
		resourceManager = service;
	}

	public void unbind(IResourceManager service) {
		this.resourceManager = null;
	}
	
	public void bind(IGlobalManager service) {
		globalManager = service;
	}

	public void unbind(IGlobalManager service) {
		this.globalManager = null;
	}
	
	public void bind(IExternalManager service) {
		externalManager = service;
	}

	public void unbind(IExternalManager service) {
		this.externalManager = null;
	}


	/**
	 * @return the listenerComponent
	 */
	public IVanillaComponentListenerService getVanillaListenerComponent() {
		return listenerComponent;
	}

	/**
	 * @return the externalAccessibilityManager
	 */
	public IExternalAccessibilityManager getExternalAccessibilityManager() {
		return externalAccessibilityManager;
	}

	/**
	 * @return the preferenceManager
	 */
	public IPreferencesManager getPreferenceManager() {
		return preferenceManager;
	}

	/**
	 * @return the repositoryManager
	 */
	public IRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	/**
	 * @return the loggingManager
	 */
	public IVanillaLoggingManager getLoggingManager() {
		return loggingManager;
	}

	/**
	 * @return the securityManager
	 */
	public IVanillaSecurityManager getSecurityManager() {
		return securityManager;
	}

	/**
	 * @return the parameterComponent
	 */
	public VanillaParameterComponent getVanillaParameterComponent() {
		return parameterComponent;
	}

	/**
	 * @return the systemManager
	 */
	public IVanillaSystemManager getSystemManager() {
		return systemManager;
	}

	@Override
	public IUnitedOlapPreloadManager getUnitedOlapPreloadManager() {
		return unitedOlapPreloadManager;
	}

	@Override
	public IVanillaAccessRequestManager getVanillaAccessRequestComponent() {
		return accessRequestManager;
	}

	public void bind(IVanillaLoggerService service) {
		this.logger = service.getLogger(PLUGIN_ID);
		getLogger().info("Vanilla Logging Service binded");
	}

	public void unbind(IVanillaLoggerService service) {
		getLogger().info("unbinding Vanilla Logging Service");
		this.logger = null;
	}

	public void bind(HttpService service) {
		getLogger().info("Binding Http service ...");
		this.httpService = service;

		getLogger().info("Binded Http service");
	}

	public void unbind(HttpService service) {
		this.httpService = null;
		getLogger().info("Unbinded Http service");
	}

	public IVanillaLogger getLogger() {
		return logger;
	}

	public void activate(ComponentContext ctx) {
		this.context = ctx;
		try {
			status = Status.STARTING;
			VanillaHttpContext httpContext = new VanillaHttpContext(getSecurityManager(), getSystemManager());
			registerServlets40(httpContext);
			startWebService();

			status = Status.STARTED;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void startWebService() throws Throwable {
		String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBSERVICE_URL);
		if (url != null && !url.isEmpty()) {
			IVanillaWebService impl = new VanillaWebService();

			Endpoint endpoint = Endpoint.publish(url, impl);

			boolean status = endpoint.isPublished();
			logger.info("Web service available " + status);
		}
		else {
			logger.info("Web service non available (url empty in vanilla.properties) " + status);
		}
		
//		Dictionary<String, String> props = new Hashtable<String,String>();
//		// osgi rs property to signal to distribution system 
//		// that this is a remote service
//		props.put("service.exported.interfaces","*");
//		// specify the distribution provider with osgi rs property
//		props.put("service.exported.configs", "ecf.jaxrs.jersey.server");
//		// as per spec, <provider>.<prop> represents a property intended for use by this provider
//		props.put("ecf.jaxrs.jersey.server.alias", "/jersey");
//		props.put("ecf.jaxrs.jersey.server.urlContext", "http://localhost:19190");
//		
//		try {
//			ServiceRegistration reg = context.getBundleContext().registerService(VanillaWopiService.class, new VanillaWopiService(), props);
//		} catch(Throwable e) {
//			e.printStackTrace();
//		}
	}

	public void desactivate(ComponentContext ctx) {
		status = Status.STOPPING;
		releaseServlets40();
		status = Status.STOPPED;
	}

	public Status getStatus() {
		return status;
	}

	private void releaseServlets40() {
		httpService.unregister(HTMLFormComponent.LIST_FORMS_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_EXTERNAL_ACCESSIBILLITY_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_LOGGING_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_PREFERENCES_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_REPOSITORY_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_SECURITY_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_SYSTEM_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_UNITED_OLAP_PRELOAD_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_LISTENERS_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_EXTERNAL_CALL);
		httpService.unregister(VanillaConstants.VANILLA_EXTERNAL_CALL_PARAM);
		httpService.unregister(VanillaConstants.VANILLA_CONTRIBUTION_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_ACCESS_MANAGER_SERVLET);
		httpService.unregister(WorkflowService.WORKFLOW_JOB_MONITOR_SERVLET);
		httpService.unregister(GatewayComponent.GATEWAY_JOB_MONITOR_SERVLET);
		httpService.unregister(IVanillaCommandManager.SERVLET_MASS_HISTORIZATION);
		httpService.unregister(VanillaConstants.VANILLA_HISTORIC_REPORT_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_GED_INDEX_SERVLET);
		httpService.unregister(ISchedulerManager.SERVLET_SCHEDULER);
		httpService.unregister(VanillaConstants.VANILLA_WEBSERVICE_SERVLET);
		httpService.unregister(VanillaConstants.VANILLA_COMMENT);
		httpService.unregister(VanillaConstants.VANILLA_JDBC_FMDT_SERVLET);
		httpService.unregister(VanillaConstants.FREEMETADATA_EXCEL_SERVLET);
		httpService.unregister(VanillaConstants.IMAGE_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.ARCHIVE_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.RESOURCE_MANAGER_SERVLET);
		httpService.unregister(VanillaConstants.GLOBAL_SERVLET);
		httpService.unregister(VanillaConstants.EXTERNAL_SERVLET);
		logger.info("Servlets40 released");
	}

	private void registerServlets40(VanillaHttpContext httpCtx) {

		try {
			httpService.registerServlet("/vanillaExternalAccess", new ExternalVanillaAccessServlet(this), null, null);
		} catch (Exception ex) {
			logger.error("vanillaExternalAccess registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("vanillaExternalAccess registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(IMassReportMonitor.SERVLET_MASS_REPORT_MONITOR, new MassReportMonitorServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ServletDispatcher registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ServletDispatcher registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(HTMLFormComponent.LIST_FORMS_SERVLET, new VanillaHTMLFormListerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error(HTMLFormComponent.LIST_FORMS_SERVLET + " registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException(HTMLFormComponent.LIST_FORMS_SERVLET + " registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerResources("/images", "/images", new PictureHttpContext());
		} catch (Exception ex) {
		}

		try {
			httpService.registerServlet(GatewayComponent.GATEWAY_JOB_MONITOR_SERVLET, new GatewayMonitorServlet(), null, null);
		} catch (Exception ex) {
			logger.error("ServletDispatcher registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ServletDispatcher registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(WorkflowService.WORKFLOW_JOB_MONITOR_SERVLET, new WorkflowMonitorServlet(), null, null);
		} catch (Exception ex) {
			logger.error("ServletDispatcher registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ServletDispatcher registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET, new ServletDispatcher(httpCtx, this, logger), null, null);
		} catch (Exception ex) {
			logger.error("ServletDispatcher registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ServletDispatcher registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_EXTERNAL_ACCESSIBILLITY_MANAGER_SERVLET, new ExternalAccessibilityServlet(this, logger), null, null);
		} catch (Exception ex) {
			logger.error("ExternalAccessibilityServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ExternalAccessibilityServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_LOGGING_MANAGER_SERVLET, new VanillaLoggingServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaLoggingServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaLoggingServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_PREFERENCES_MANAGER_SERVLET, new PreferencesServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("PreferencesServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("PreferencesServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_REPOSITORY_MANAGER_SERVLET, new RepositoryServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("RepositoryServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("RepositoryServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_SECURITY_MANAGER_SERVLET, new VanillaSecurityServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaSecurityServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaSecurityServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_SYSTEM_MANAGER_SERVLET, new VanillaSystemServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaSystemServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaSystemServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_UNITED_OLAP_PRELOAD_SERVLET, new VanillaUnitedOlapPreloadServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaUnitedOlapPreloadServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaSystemServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_LISTENERS_SERVLET, new VanillaListenerServlet(this, logger), null, null);
			logger.info("VanillaListenerServlet registration succeed");
		} catch (Exception ex) {
			logger.error("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_PARAMETERS_PROVIDER_SERVLET, new VanillaParameterServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_EXTERNAL_CALL, new ExternalCallObjectServlet(this), null, null);
		} catch (Exception ex) {
			logger.error("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_EXTERNAL_CALL_PARAM, new ExternalCallObjectParamServlet(this), null, null);
		} catch (Exception ex) {
			logger.error("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(VanillaConstants.VANILLA_ACCESS_MANAGER_SERVLET, new VanillaAccessRequestServlet(this, logger), null, null);
		} catch (Exception ex) {
			logger.error("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaListenerServlet registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(IVanillaCommandManager.SERVLET_MASS_HISTORIZATION, new ServletMassHistorization(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ContributionManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ContributionManagerServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_HISTORIC_REPORT_SERVLET, new ReportHistoricServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ReportHistoricServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ReportHistoricServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_GED_INDEX_SERVLET, new GedIndexServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("GedIndexServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("GedIndexServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_BIG_FILE_GED_SERVLET, new BigFileGedServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("BigFileGedServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("BigFileGedServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(ISchedulerManager.SERVLET_SCHEDULER, new SchedulerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ScheduleurServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ScheduleurServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_WEBSERVICE_SERVLET, new VanillaWebServiceServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("VanillaWebServiceServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("VanillaWebServiceServlet registration failed - " + ex.getMessage(), ex);
		}

		try {
			httpService.registerServlet(VanillaConstants.VANILLA_COMMENT, new CommentServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("CommentServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("CommentServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.VANILLA_JDBC_FMDT_SERVLET, new FmdtJdbcServlet(this), null, null);//httpCtx);
		} catch (Exception ex) {
			logger.error("FmdtJdbcServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("FmdtJdbcServlet registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(VanillaConstants.FREEMETADATA_EXCEL_SERVLET, new FmdtExcelServlet(), null, null);
		} catch (Exception ex) {
			logger.error("FmdtExcelServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("FmdtExcelServlet registration failed - " + ex.getMessage(), ex);
		}
		try {
			httpService.registerServlet(VanillaConstants.VANILLA_EXCEL_MANAGER_SERVLET, new ExcelServlet(this, logger), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ExcelServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ExcelServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.IMAGE_MANAGER_SERVLET, new ImageManagerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ImageManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ImageManagerServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.ARCHIVE_MANAGER_SERVLET, new ArchiveManagerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ArchiveManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ArchiveManagerServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.RESOURCE_MANAGER_SERVLET, new ResourceManagerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ResourceManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ResourceManagerServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.AUTO_LOGIN_SERVLET, new AutoLoginServlet(this, logger), null, null);
		} catch (Exception ex) {
			logger.error("AutoLoginServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("AutoLoginServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerResources(VanillaConstants.KEYCLOAK_RESOURCE, "/resources", null);
		} catch (Exception ex) {
			logger.error("Keycloak configuration registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("Keycloak configuration registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.GLOBAL_SERVLET, new GlobalManagerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("GlobalManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("GlobalManagerServlet registration failed - " + ex.getMessage(), ex);
		}
		
		try {
			httpService.registerServlet(VanillaConstants.EXTERNAL_SERVLET, new ExternalManagerServlet(this), null, httpCtx);
		} catch (Exception ex) {
			logger.error("ExternalManagerServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ExternalManagerServlet registration failed - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void historizeFolderContent(String vanillaRootRelativeFolderPath) throws Exception {
		if (massHistorizationCmd != null && massHistorizationCmd.isAlive()) {
			shouldMassHistorize = true;
			Logger.getLogger(getClass()).info("Mass Historization asked but already running");
			return;
		}
		synchronized (shouldMassHistorize) {
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();

			massHistorizationCmd = new FolderHistorizationCommand(new File(conf.getProperty(VanillaConfiguration.P_VANILLA_FILES) + WorkflowService.WORKFLOW_MASS_HISTORIZATION_FOLDER), this, new RemoteHistoricReportComponent(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)));
			shouldMassHistorize = Boolean.FALSE;
			massHistorizationCmd.start();
		}

	}

	@Override
	public ReportHistoricComponent getReportHistoricComponent() {
		return historicComponent;
	}

	@Override
	public IGedComponent getGedIndexComponent() {
		return gedIndexComponent;
	}

	@Override
	public IMassReportMonitor getMassReportMonitor() {
		return massReportMonitor;
	}

	public void bind(IMassReportMonitor service) {
		massReportMonitor = service;
	}

	public void unbind(IMassReportMonitor service) {
		massReportMonitor = null;
	}
	
	public void bind(IImageManager service) {
		imageManager = service;
	}

	public void unbind(IImageManager service) {
		imageManager = null;
	}

	public ISchedulerManager getSchedulerManager() {
		return schedulerManager;
	}

	public void bind(ISchedulerManager scheduler) {
		schedulerManager = scheduler;
	}

	public void unbind(ISchedulerManager scheduler) {
		schedulerManager = null;
	}

	@Override
	public IVanillaWebServiceComponent getVanillaWebServiceComponent() {
		return vanillaWebServiceComponent;
	}

	@Override
	public ICommentService getCommentComponent() {
		return commentComponent;
	}

	@Override
	public IExcelManager getExcelManager() {
		return excelManager;
	}

	@Override
	public IImageManager getImageManager() {
		return imageManager;
	}

	@Override
	public IArchiveManager getArchiveManager() {
		return archiveManager;
	}

	@Override
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	@Override
	public IGlobalManager getGlobalManager() {
		return globalManager;
	}
	
	@Override
	public IExternalManager getExternalManager() {
		return externalManager;
	}
}
