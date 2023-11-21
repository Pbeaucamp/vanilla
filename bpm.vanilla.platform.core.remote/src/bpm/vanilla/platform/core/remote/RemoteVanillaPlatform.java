package bpm.vanilla.platform.core.remote;

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
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaLoggingManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.commands.IVanillaCommandManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.impl.RemoteArchiveManager;
import bpm.vanilla.platform.core.remote.impl.RemoteCommentService;
import bpm.vanilla.platform.core.remote.impl.RemoteExcelManager;
import bpm.vanilla.platform.core.remote.impl.RemoteExternalAccessibilityManager;
import bpm.vanilla.platform.core.remote.impl.RemoteExternalManager;
import bpm.vanilla.platform.core.remote.impl.RemoteGlobalManager;
import bpm.vanilla.platform.core.remote.impl.RemoteImageManager;
import bpm.vanilla.platform.core.remote.impl.RemoteMassReportMonitor;
import bpm.vanilla.platform.core.remote.impl.RemoteRepositoryManager;
import bpm.vanilla.platform.core.remote.impl.RemoteResourceManager;
import bpm.vanilla.platform.core.remote.impl.RemoteSchedulerManager;
import bpm.vanilla.platform.core.remote.impl.RemoteUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaAccessRequestManager;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaListenerService;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaLoggingManager;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaPreferencesManager;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSecurityManager;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSystemManager;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;

/**
 * entry point to access VanillaPlatform data from a distant client
 * 
 * @author ludo
 * 
 */
public class RemoteVanillaPlatform implements IVanillaAPI {
	private HttpCommunicator httpCommunicator = new HttpCommunicator();

	private IExternalAccessibilityManager externalAccessibilityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaLoggingManager logManager;
	private IPreferencesManager prefManager;
	private IVanillaSecurityManager secuManager;
	private IVanillaSystemManager systemManager;
	private IUnitedOlapPreloadManager unitedOlapPreloadManager;
	private IVanillaComponentListenerService listenerService;
	private IVanillaAccessRequestManager accessRequestManager;
	private IMassReportMonitor massReportManager;
	private ICommentService commentService;
	private ISchedulerManager schedulerManager;
	private IExcelManager excelManager;
	private IImageManager imageManager;
	private IArchiveManager archiveManager;
	private IResourceManager resourceManager;
	private IGlobalManager globalManager;
	private IExternalManager externalManager;

	private IVanillaContext vanillaCtx;
	private String vanillaExternalUrl;

	public RemoteVanillaPlatform(IVanillaContext vanillaContext) {
		this(vanillaContext.getVanillaUrl(), vanillaContext.getLogin(), vanillaContext.getPassword());
	}

	public RemoteVanillaPlatform(VanillaConfiguration conf) {
		this(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

	}

	public RemoteVanillaPlatform(String vanillaUrl, String login, String password) {
		this.vanillaCtx = new BaseVanillaContext(vanillaUrl, login, password);

		httpCommunicator.init(vanillaUrl, login, password);

		externalAccessibilityManager = new RemoteExternalAccessibilityManager(httpCommunicator);
		repositoryManager = new RemoteRepositoryManager(httpCommunicator);
		logManager = new RemoteVanillaLoggingManager(httpCommunicator);
		prefManager = new RemoteVanillaPreferencesManager(httpCommunicator);
		secuManager = new RemoteVanillaSecurityManager(httpCommunicator);
		systemManager = new RemoteVanillaSystemManager(httpCommunicator);
		unitedOlapPreloadManager = new RemoteUnitedOlapPreloadManager(httpCommunicator);
		listenerService = new RemoteVanillaListenerService(httpCommunicator);
		accessRequestManager = new RemoteVanillaAccessRequestManager(httpCommunicator);
		massReportManager = new RemoteMassReportMonitor(httpCommunicator);
		commentService = new RemoteCommentService(httpCommunicator);
		schedulerManager = new RemoteSchedulerManager(httpCommunicator);
		excelManager = new RemoteExcelManager(httpCommunicator);
		imageManager = new RemoteImageManager(httpCommunicator);
		archiveManager = new RemoteArchiveManager(httpCommunicator);
		resourceManager = new RemoteResourceManager(httpCommunicator);
		globalManager = new RemoteGlobalManager(httpCommunicator);
		externalManager = new RemoteExternalManager(httpCommunicator);
		
		// Trying to initialize external runtime URL
		try {
			this.vanillaExternalUrl = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_URL_EXTERNAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the unitedOlapPreloadManager
	 */
	public IMassReportMonitor getMassReportMonitor() {
		return massReportManager;
	}

	/**
	 * @return the unitedOlapPreloadManager
	 */
	public IUnitedOlapPreloadManager getUnitedOlapPreloadManager() {
		return unitedOlapPreloadManager;
	}

	/**
	 * @return the externalManager
	 */
	public IExternalAccessibilityManager getVanillaExternalAccessibilityManager() {
		return externalAccessibilityManager;
	}

	/**
	 * @return the repositoryManager
	 */
	public IRepositoryManager getVanillaRepositoryManager() {
		return repositoryManager;
	}

	/**
	 * @return the logManager
	 */
	public IVanillaLoggingManager getVanillaLoggingManager() {
		return logManager;
	}

	/**
	 * @return the prefManager
	 */
	public IPreferencesManager getVanillaPreferencesManager() {
		return prefManager;
	}

	/**
	 * @return the secuManager
	 */
	public IVanillaSecurityManager getVanillaSecurityManager() {
		return secuManager;
	}

	/**
	 * @return the systemManager
	 */
	public IVanillaSystemManager getVanillaSystemManager() {
		return systemManager;
	}

	@Override
	public String getVanillaUrl() {

		return vanillaCtx.getVanillaUrl();
	}

	@Override
	public IVanillaComponentListenerService getListenerService() {
		return listenerService;
	}

	@Override
	public IVanillaAccessRequestManager getVanillaAccessRequestManager() {
		return accessRequestManager;
	}

	@Override
	public ICommentService getCommentService() {
		return commentService;
	}

	@Override
	public ISchedulerManager getSchedulerManager() {
		return schedulerManager;
	}

	@Override
	// FIXME : really unelegant to be there but for now its ok because it
	// is the only one method
	// if someone needs another method like this one
	// just implement a new DedicatedRemote and remove the extends
	// IVanillaCommandManager
	public void historizeFolderContent(String vanillaRootRelativeFolderPath) throws Exception {
		httpCommunicator.executeActionAsStream(IVanillaCommandManager.SERVLET_MASS_HISTORIZATION, vanillaRootRelativeFolderPath);

	}

	@Override
	public IVanillaContext getVanillaContext() {
		return vanillaCtx;
	}

	public String getSessionId() {
		return httpCommunicator.getCurrentSessionId();
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

	@Override
	public String getVanillaExternalUrl() {
		return vanillaExternalUrl != null && !vanillaExternalUrl.isEmpty() ? vanillaExternalUrl : vanillaCtx.getVanillaUrl();
	}

}
