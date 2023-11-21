package bpm.vanilla.platform.core.components.impl;

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
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;

public interface IVanillaComponentProvider {
	public IExternalAccessibilityManager getExternalAccessibilityManager();

	public IPreferencesManager getPreferenceManager();

	public IRepositoryManager getRepositoryManager();

	public IVanillaLoggingManager getLoggingManager();

	public IVanillaSecurityManager getSecurityManager();

	public IVanillaSystemManager getSystemManager();

	public IUnitedOlapPreloadManager getUnitedOlapPreloadManager();

	public IVanillaComponentListenerService getVanillaListenerComponent();

	public VanillaParameterComponent getVanillaParameterComponent();

	public IVanillaAccessRequestManager getVanillaAccessRequestComponent();

	public ReportHistoricComponent getReportHistoricComponent();

	public IGedComponent getGedIndexComponent();

	public ISchedulerManager getSchedulerManager();

	public ICommentService getCommentComponent();

	public IMassReportMonitor getMassReportMonitor();

	public Status getStatus();

	public IVanillaWebServiceComponent getVanillaWebServiceComponent();

	public IExcelManager getExcelManager();

	public IArchiveManager getArchiveManager();

	public IImageManager getImageManager();

	public IResourceManager getResourceManager();

	public IGlobalManager getGlobalManager();

	public IExternalManager getExternalManager();
}
