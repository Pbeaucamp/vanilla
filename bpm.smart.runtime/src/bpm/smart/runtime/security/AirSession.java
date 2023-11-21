package bpm.smart.runtime.security;

import java.util.Locale;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.runtime.AdminManager;
import bpm.smart.runtime.SmartManagerComponent;
import bpm.smart.runtime.SmartManagerService;
import bpm.vanilla.platform.core.beans.User;
import bpm.workflow.commons.remote.IAdminManager;
import bpm.workflow.commons.security.AbstractSession;

/**
 * Vanilla hub runtime session
 */
public class AirSession extends AbstractSession {

	private AdminManager adminManager;
	private SmartManagerService serviceManager;
//	private WorkflowManager workflowManager;
	
	private RScriptModel lastResult;
	
	public AirSession(SmartManagerComponent component, User user, Locale locale) {
		super();

		this.adminManager = new AdminManager(component);
		this.serviceManager = new SmartManagerService(component, user);
//		this.workflowManager = new WorkflowManager(component);
		
		setLocale(locale);
	}
	
	public void setLocale(Locale locale) {
		this.adminManager.setLocale(locale);
//		this.workflowManager.setLocale(locale);
//		this.resourceManager.setLocale(locale);
	}
	
	public IAdminManager getAdminManager() {
		return adminManager;
	}
	
	public SmartManagerService getServiceManager() {
		return serviceManager;
	}

	public RScriptModel getLastResult() {
		return lastResult;
	}

	public void setLastResult(RScriptModel lastResult) {
		this.lastResult = lastResult;
	}
	
//	public IWorkflowManager getWorkflowManager() {
//		return workflowManager;
//	}
}
