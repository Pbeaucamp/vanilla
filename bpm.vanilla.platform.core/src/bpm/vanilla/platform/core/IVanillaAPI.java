package bpm.vanilla.platform.core;

import bpm.vanilla.platform.core.commands.IVanillaCommandManager;


public interface IVanillaAPI extends IVanillaCommandManager{
	public IExternalAccessibilityManager getVanillaExternalAccessibilityManager();
	public IPreferencesManager getVanillaPreferencesManager();
	public IRepositoryManager getVanillaRepositoryManager();
	public IVanillaLoggingManager getVanillaLoggingManager();
	public IVanillaSecurityManager getVanillaSecurityManager();
	public IVanillaSystemManager getVanillaSystemManager();
	public IUnitedOlapPreloadManager getUnitedOlapPreloadManager();
	public ICommentService getCommentService();
	
	public IExcelManager getExcelManager();
	
//	public void reset(String vanillaUrl, String login, String password);
	public String getVanillaUrl();
	public String getVanillaExternalUrl();
	
	public IVanillaComponentListenerService getListenerService();
	public IVanillaAccessRequestManager getVanillaAccessRequestManager();
	public IMassReportMonitor getMassReportMonitor();
	public ISchedulerManager getSchedulerManager();
	
	public IVanillaContext getVanillaContext();
	
	public IImageManager getImageManager();
	
	public IArchiveManager getArchiveManager();
	public IResourceManager getResourceManager();
	public IGlobalManager getGlobalManager();
	public IExternalManager getExternalManager();
}
