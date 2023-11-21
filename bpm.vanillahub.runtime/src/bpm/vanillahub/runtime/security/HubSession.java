package bpm.vanillahub.runtime.security;

import java.util.Locale;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.runtime.AdminManager;
import bpm.vanillahub.runtime.ComponentVanillaHub;
import bpm.vanillahub.runtime.ResourceManager;
import bpm.vanillahub.runtime.WorkflowManager;
import bpm.workflow.commons.remote.IAdminManager;
import bpm.workflow.commons.security.AbstractSession;

/**
 * Vanilla hub runtime session
 */
public class HubSession extends AbstractSession {

	private AdminManager adminManager;
	private WorkflowManager workflowManager;
	private ResourceManager resourceManager;
	
	public HubSession(ComponentVanillaHub component, Locale locale) {
		super();
		String filePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.HUB_FILE_PATH);
		
		this.adminManager = new AdminManager(component);
		this.workflowManager = new WorkflowManager(component);
		this.resourceManager = new ResourceManager(component, filePath);
		setLocale(locale);
	}
	
	public void setLocale(Locale locale) {
		this.adminManager.setLocale(locale);
		this.workflowManager.setLocale(locale);
		this.resourceManager.setLocale(locale);
	}
	
	public IAdminManager getAdminManager() {
		return adminManager;
	}
	
	public IHubWorkflowManager getWorkflowManager() {
		return workflowManager;
	}
	
	public IHubResourceManager getResourceManager() {
		return resourceManager;
	}
}
