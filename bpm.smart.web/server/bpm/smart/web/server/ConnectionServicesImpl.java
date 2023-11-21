package bpm.smart.web.server;

import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.ILoginManager;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.web.client.UserSession;
import bpm.smart.web.client.services.ConnectionServices;
import bpm.smart.web.server.security.SmartWebSession;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConnectionServicesImpl extends RemoteServiceServlet implements ConnectionServices, ILoginManager {

	private static final long serialVersionUID = 7630616301811834767L;

	private String vanillaRuntimeUrl;
	private String airRuntimeUrl;

	@Override
	public void init() throws ServletException {
		super.init();
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaRuntimeUrl = config.getVanillaServerUrl();
		this.airRuntimeUrl = config.getProperty(VanillaConfiguration.P_SMART_RUNTIME_URL);
	}

	private SmartWebSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), SmartWebSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(SmartWebSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());

			SmartWebSession session = getSession();
			session.setLoginManager(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public UserSession initSmartWebSession(InfoUser infoUser) throws Exception {
		SmartWebSession session = getSession();

		try {
			session.initSession(infoUser.getUser(), session.getServerSessionId(), getLocale());

			Date date = new Date();

			String Rlibs = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_R_LIBRARIES_PATH);

			UserSession userSession = new UserSession();
			userSession.setDefaultDate(date);
			userSession.setInfoUser(infoUser);
			userSession.setRlibs(Rlibs);

			return userSession;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public boolean isConnectedToVanilla() throws Exception {
		if (vanillaRuntimeUrl == null || vanillaRuntimeUrl.isEmpty()) {
			throw new Exception("This application need to be connected to Vanilla. Please check your properties file.");
		}
		return true;
	}

	@Override
	public User authentify(String login, String password) throws Exception {
		RemoteAdminManager manager = new RemoteAdminManager(airRuntimeUrl, null, getLocale());

		try {
			String locale = getLocale() != null ? getLocale().getLanguage() : "";

			return manager.login(login, password, locale);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getServerSessionId(User user) throws Exception {
		RemoteAdminManager manager = new RemoteAdminManager(airRuntimeUrl, null, getLocale());
		return manager.connect(user);
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	@Override
	public InfoConnection getConnectionInformations() {
		return null;
	}
}
