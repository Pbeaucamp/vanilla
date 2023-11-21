package bpm.gwt.aklabox.commons.server;

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import bpm.aklabox.workflow.core.model.Platform;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.gwt.aklabox.commons.client.services.LoginService;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;
import bpm.gwt.aklabox.commons.server.security.CommonConfiguration;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;
import bpm.gwt.aklabox.commons.server.security.ExceptionHelper;
import bpm.gwt.aklabox.commons.shared.InfoConnection;
import bpm.gwt.aklabox.commons.shared.InfoCookie;
import bpm.gwt.aklabox.commons.shared.InfoUser;
import bpm.vanilla.platform.core.config.ConfigurationAklaboxConstants;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	private static final long serialVersionUID = 1L;

	private String instanceUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.server.url");
	private HashMap<String, InfoCookie> sessionCookies = new HashMap<String, InfoCookie>();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	private String createSessionId(CommonSession session) {
		String sessionId = String.valueOf(new Object().hashCode());
		InfoCookie info = new InfoCookie(session.getUser().getUserId());
		sessionCookies.put(sessionId, info);
		return sessionId;
	}

	private void initSession(CommonSession session, User user) throws Exception {
		session.init(user);
	}

	@Override
	public InfoUser checkCookieSession(String sessionCookieId) throws Exception {
		InfoCookie infoCookie = sessionCookies.get(sessionCookieId);
		if (infoCookie == null) {
			System.out.println("Check session " + sessionCookieId + " session is null");
			return null;
		}
		else if (!infoCookie.isValid()) {
			System.out.println("Check session " + sessionCookieId + " session is not valid");
			sessionCookies.remove(sessionCookieId);
			return null;
		}

		IVdmManager remoteRoot = CommonConfiguration.getInstance().getRootAklaboxManager();
		/*------- We get the remote Root to check if the user is valid -----------*/
		User user = null;
		try {
			user = remoteRoot.getUserInfoThroughId(String.valueOf(infoCookie.getUserId()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("User is not valid." + e.getMessage());
		}

		if (user == null) {
			throw new ServiceException("User is not valid.");
		}

		CommonSession session = getSession();
		initSession(session, user);

		InfoUser infos;
		try {
			infos = buildInfoUser(session, user);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, e.getMessage(), true);
		}
		return infos;
	}

	@Override
	public InfoConnection getConnectionInformations() throws Exception {
		Platform platform = CommonConfiguration.getInstance().getRootAklaflowManager().getPlatform();
		return new InfoConnection(platform);
	}

	@Override
	public InfoUser login(String username, String password) throws Exception {
		CommonSession session = getSession();

		VdmContext ctx = new VdmContext(instanceUrl, username, password, 0);
		IVdmManager manager = new RemoteVdmManager(ctx);

		User myUser = new User();
		myUser.setEmail(username);
		myUser.setPassword(password);

//		User user = manager.getUserInfo(username);
//		UserConnectionProperty connectionProps = manager.getConnectionPropertyByUser(user.getUserId());
//		if (connectionProps.getId() == 0) {
//			throw new Exception("You are not allowed to access this application.");
//		}
//		else {
//			boolean canAccess = session.getApplicationId().equals(CommonConstants.AKLAD) && connectionProps.isAklaFlow()
//					|| session.getApplicationId().equals(CommonConstants.AKLADEMAT) && connectionProps.isAklaFlow()
//					|| session.getApplicationId().equals(CommonConstants.AKLAFLOW) && connectionProps.isAklaFlow();
//			
//			if (canAccess) {
				User user = manager.connect(myUser);

				initSession(session, user);

				String sessionId = createSessionId(session);
				try {
					InfoUser infoUser = buildInfoUser(session, user);
					infoUser.setSessionId(sessionId);

					return infoUser;
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
//			}
//			else {
//				throw new Exception("You are not allowed to access this application.");
//			}
//		}
	}

	private InfoUser buildInfoUser(CommonSession session, User user) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		InfoUser infoUser = new InfoUser();
		infoUser.setUser(user);
		infoUser.setRuntimeUrl(session.getRuntimeUrl());
		infoUser.setCustomer(session.getCustomer());

		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_ACTE_TYPE, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_ACTE_TYPE));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_TYPE, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_TYPE));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_SERVICE_FINANCE, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_SERVICE_FINANCE));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_SERVICE_FINANCE_FOLDER, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_SERVICE_FINANCE_FOLDER));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_STORAGE_LOCATION, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_STORAGE_LOCATION));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHAT, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHAT));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_REGISTRATION_COUNTRY, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_REGISTRATION_COUNTRY));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_PHYSICAL_LOCATION, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_PHYSICAL_LOCATION));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_BANNER_LOGO, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_BANNER_LOGO));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_FILES_PATH, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_FILES_PATH));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CEGID_CHORUS_URL, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CEGID_CHORUS_URL));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_COCKTAIL_ORACLE_URL, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_COCKTAIL_ORACLE_URL));
		infoUser.putProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_TYPE, config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_TYPE));
//		infoUser.putProperty(ConfigurationAklaboxConstants.P_TELETRANSMISSION_CLIENT, config.getProperty(ConfigurationAklaboxConstants.P_TELETRANSMISSION_CLIENT));
		infoUser.putProperty(VanillaConfiguration.P_WOPI_WS_URL, config.getProperty(VanillaConfiguration.P_WOPI_WS_URL));

		InfoConnection infoConnection = getConnectionInformations();
		infoUser.setPlatformInfo(infoConnection.getPlatform());
		return infoUser;
	}

	@Override
	public InfoUser loginWithSessionId(String login) throws Exception {
		return null;
//		CommonSession session = getSession();
//
//		IVdmManager manager = CommonConfiguration.getInstance().getRootAklaboxManager();
//		User user = manager.getUserInfo(login);
//		
//		initSession(session, user);
//
//		String sessionId = createSessionId(session);
//		try {
//			InfoUser infoUser = buildInfoUser(session, user);
//			infoUser.setSessionId(sessionId);
//
//			return infoUser;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
	}
}
