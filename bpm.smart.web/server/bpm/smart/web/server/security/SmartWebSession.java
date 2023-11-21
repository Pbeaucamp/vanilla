package bpm.smart.web.server.security;

import java.util.Locale;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.ISmartWorkflowManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.smart.core.xstream.RemoteWorkflowManager;
import bpm.smart.web.client.UserSession;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.workflow.commons.remote.IAdminManager;

public class SmartWebSession extends CommonSession {
	
	private static final String P_AIR = "u99fds$*f_a=v51";

	private Logger logger = Logger.getLogger(this.getClass());

	private IAdminManager adminManager;
	private ISmartManager smartManager;

	private UserSession userSession;

	public SmartWebSession() {
	}

	public void initSession(User user, String sessionId, Locale locale) throws ServiceException {
		logger.info("Init session...");
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String smartRuntimUrl = config.getProperty(VanillaConfiguration.P_SMART_RUNTIME_URL);

		this.adminManager = new RemoteAdminManager(smartRuntimUrl, sessionId, locale);
		this.smartManager = new RemoteSmartManager(smartRuntimUrl, sessionId, locale);
		
		ISmartWorkflowManager workflowManager = new RemoteWorkflowManager(smartRuntimUrl, sessionId, locale);
		setResourceManager(workflowManager);
		setWorkflowManager(workflowManager);
		
		logger.info("Session created.");
	}

	public ISmartManager getManager() {
		return smartManager;
	}

	public void setManager(ISmartManager smartManager) {
		this.smartManager = smartManager;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.SMART_WEB;
	}
	
	@Override
	public String getApplicationPassword() {
		return P_AIR;
	}
	
	@Override
	public boolean hasLicence() {
		return true;
	}

	public IAdminManager getAdminManager() {
		return adminManager;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
	
	public UserSession getUserSession() {
		return userSession;
	}
}
