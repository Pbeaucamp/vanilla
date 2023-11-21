package bpm.vanillahub.web.server.security;

import java.util.Locale;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.remote.RemoteAdminManager;
import bpm.vanillahub.remote.RemoteResourceManager;
import bpm.vanillahub.remote.RemoteWorkflowManager;
import bpm.workflow.commons.remote.IAdminManager;

public class VanillaHubSession extends CommonSession {

	private static final String P_HUB = "vanillahubmay2015";

	private Logger logger = Logger.getLogger(this.getClass());

	private IAdminManager adminManager;
	
	public VanillaHubSession() { }

	public void initSession(User user, String sessionId, Locale locale) throws ServiceException {
		logger.info("Init session...");

		String runtimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HUB_RUNTIME_URL);
		
		this.adminManager = new RemoteAdminManager(runtimeUrl, sessionId, locale);
		setResourceManager(new RemoteResourceManager(runtimeUrl, sessionId, locale));
		setWorkflowManager(new RemoteWorkflowManager(runtimeUrl, sessionId, locale));
		logger.info("Session created.");
	}
	
	public IAdminManager getAdminManager() {
		return adminManager;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.HUB;
	}
	
	@Override
	public String getApplicationPassword() {
		return P_HUB;
	}
	
	@Override
	public boolean hasLicence() {
		return true;
	}
	
	public IHubResourceManager getHubResourceManager() {
		return (IHubResourceManager) super.getResourceManager();
	}
}
