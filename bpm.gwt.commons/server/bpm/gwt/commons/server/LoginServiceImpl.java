package bpm.gwt.commons.server;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import bpm.document.management.core.IVdmManager;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.Authentication;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.server.security.Security;
import bpm.gwt.commons.server.security.Authentication.TypeAuthentication;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoCookie;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.PasswordState;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.Setting.SettingType;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.LicenceExpiredException;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent.VanillaActionType;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSystemManager;
import bpm.vanilla.platform.core.utils.LicenceChecker;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	private static final long serialVersionUID = -5796095198921620090L;
	private String vanillaRuntimeUrl;

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class);

	private HashMap<String, InfoCookie> sessionCookies = new HashMap<String, InfoCookie>();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		vanillaRuntimeUrl = conf.getVanillaServerUrl();
	}

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}
	
	@Override
	public InfoConnection getConnectionProperties() throws ServiceException {
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String useKeycloak = conf.getProperty(VanillaConfiguration.P_WEBAPPS_KEYCLOAK_ENABLED);
		String keycloakKey = conf.getProperty(VanillaConfiguration.P_WEBAPPS_KEYCLOAK_KEY);
		String keycloakConfigUrl = conf.getProperty(VanillaConfiguration.P_WEBAPPS_KEYCLOAK_CONFIG_URL);
		
		InfoConnection infos = new InfoConnection();
		infos.setUseKeycloak(useKeycloak != null && Boolean.parseBoolean(useKeycloak));
		infos.setKeycloakKey(keycloakKey);
		infos.setKeycloakConfigUrl(keycloakConfigUrl);
		return infos;
	}

	@Override
	public InfoConnection getConnectionInformations(boolean fromPortal) throws ServiceException {
		CommonSession session = getSession();
		boolean connectedToVanilla = false;
		try {
			connectedToVanilla = session.isConnectedToVanilla();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

		InfoConnection infos = new InfoConnection();
		if (connectedToVanilla) {
			IVanillaContext context = CommonConfiguration.getInstance().getRootContext();

			bpm.vanilla.platform.core.IVanillaAPI api = new RemoteVanillaPlatform(context);
			RemoteVanillaPlatform remote = new RemoteVanillaPlatform(context);

			List<VanillaLocale> locales;
			try {
				locales = remote.getVanillaSystemManager().getVanillaLocales(fromPortal);
			} catch (Exception e) {
				if (e.getMessage().contains("Max number")) {
					throw new ServiceException(e.getMessage());
				}
				e.printStackTrace();
				throw new ServiceException("Unable to get the available locale for this Web Application.", e);
			}

			String customLogo = null;
			try {
				customLogo = api.getVanillaPreferencesManager().getCustomLogoUrl();

				if (customLogo != null) {
					if (customLogo.contains("webapps")) {
						customLogo = customLogo.substring(customLogo.indexOf("webapps") + "webapps".length(), customLogo.length());
					}

					String url = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
					customLogo = url + customLogo;
				}

			} catch (Exception e) {
				e.printStackTrace();
				customLogo = null;
			}

			boolean includeFastConnect;
			try {
				includeFastConnect = api.getVanillaPreferencesManager().includeFastConnection();
			} catch (Exception e) {
				e.printStackTrace();
				includeFastConnect = false;
			}

			infos.setCustomLogo(customLogo);
			infos.setIncludeFastConnect(includeFastConnect);
			infos.setLocales(locales);

			infos.setVanillaUrl(vanillaRuntimeUrl.endsWith("/") ? vanillaRuntimeUrl : vanillaRuntimeUrl + "/");
		}
		else {
			infos = session.getLoginManager().getConnectionInformations();
		}

		return infos;
	}

	@Override
	public InfoUser testConnectionCAS() throws ServiceException {
		System.out.println("Logging CAS");
		
		Assertion casAssertion = (Assertion) getThreadLocalRequest().getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);

		if (casAssertion != null) {
			System.out.println("CAS Session detected");
			
			logger.info("CAS Session detected");
			String remoteUser = null;

			try {
				remoteUser = (String) casAssertion.getPrincipal().getAttributes().get(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_CAS_ATTRIBUTE));
			} catch (Exception e) {
				System.out.println("Could not find user : " + e.getMessage());
				
				remoteUser = casAssertion.getPrincipal().getName();
			}
			
			System.out.println("Found User = " + remoteUser);

			if (remoteUser == null || remoteUser.isEmpty()) {
				remoteUser = casAssertion.getPrincipal().getName();
				
				System.out.println("Remote user not found - Trying to get it - Remote user = " + remoteUser);
			}

			if (remoteUser != null) {
				IVanillaAPI remote = CommonConfiguration.getInstance().getRootVanillaApi();

				User user = null;
				try {
					user = remote.getVanillaSecurityManager().getUserByLogin(remoteUser);
					
					System.out.println("Found Vanilla User " + user.getName());
				} catch (Exception e) {
					
					System.out.println("Vanilla User not found");
					
					e.printStackTrace();
					return null;
				}
				
				System.out.println("Login automatically");

				return login(user.getLogin(), user.getPassword());
			}
		}

		return null;
	}

	@Override
	public InfoUser testAutoLogin(String keyAutoLogin) throws ServiceException {
		if (keyAutoLogin != null) {
			logger.info("AutoLogin attempt detected");
			IVanillaAPI remote = CommonConfiguration.getInstance().getRootVanillaApi();

			User user = null;
			try {
				user = remote.getVanillaSecurityManager().checkAutoLogin(keyAutoLogin);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return login(user.getLogin(), user.getPassword());
		}

		return null;
	}

	@Override
	public InfoUser login(String username, String password) throws ServiceException {
		return authentify(new Authentication(username, password));
	}

	private void checkLicence(CommonSession session) throws LicenceExpiredException {
		String applicationId = session.getApplicationId();
		String password = session.getApplicationPassword();
		boolean hasLicence = session.hasLicence();

		if (hasLicence) {
			boolean check = LicenceChecker.checkLicence(applicationId, password, true);
			if (check) {
				// throw new LicenceExpiredException();
			}
		}
	}

	@Override
	public void initRepositoryConnection(Group selectedGroup, Repository selectedRepository) throws ServiceException {
		CommonSession session = getSession();
		session.initSession(selectedGroup, selectedRepository);
	}

	@Override
	public List<Repository> getAvailableRepositories() throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaRepositoryManager().getUserRepositories(session.getUser().getLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get repositories for user " + session.getUser().getLogin() + ": " + e.getMessage());
		}
	}

	@Override
	public List<Repository> getAvailableRepositories(String vanillaUrl, String login, String password) throws ServiceException {
		RemoteVanillaPlatform remote = new RemoteVanillaPlatform(vanillaUrl, login, password);

		try {
			return remote.getVanillaRepositoryManager().getUserRepositories(login);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get repositories for user " + login + ": " + e.getMessage());
		}
	}

	@Override
	public List<Group> getAvailableGroups() throws ServiceException {
		CommonSession session = getSession();
		try {
			List<Group> groups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get groups for user " + session.getUser().getLogin() + ": " + e.getMessage());
		}
	}

	@Override
	public List<Group> getAvailableGroups(String vanillaUrl, String login, String password) throws ServiceException {
		RemoteVanillaPlatform remote = new RemoteVanillaPlatform(vanillaUrl, login, password);
		try {
			User user = remote.getVanillaSecurityManager().getUserByLogin(login);
			List<Group> groups = remote.getVanillaSecurityManager().getGroups(user);
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get groups for user " + login + ": " + e.getMessage());
		}
	}

	@Override
	public InfoUser checkCookieSession(String sessionCookieId) throws ServiceException {
		InfoCookie infoCookie = sessionCookies.get(sessionCookieId);
		if (infoCookie == null) {
			return null;
		}
		else if (!infoCookie.isValid()) {
			sessionCookies.remove(sessionCookieId);
			return null;
		}

		String login = infoCookie.getLogin();
		String password = infoCookie.getPassword();
		int groupId = infoCookie.getGroupId();
		int repositoryId = infoCookie.getRepositoryId();

		return connect(login, password, groupId, repositoryId);
	}

	private InfoUser connect(String login, String password, int repositoryId, int groupId) throws ServiceException {
		InfoUser infoUser = login(login, password);
		if (infoUser == null || infoUser.getUser() == null) {
			throw new ServiceException("User is not valid.");
		}

		CommonSession session = getSession();
		boolean connectedToVanilla = false;
		try {
			connectedToVanilla = session.isConnectedToVanilla();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

		if (connectedToVanilla && groupId > 0 && repositoryId > 0) {
			/*------- If the user is valid we create his remote -----------*/
			IVanillaContext ctxUser = new BaseVanillaContext(vanillaRuntimeUrl, login, password);
			RemoteVanillaPlatform remote = new RemoteVanillaPlatform(ctxUser);

			/*------- We check the groups -----------*/
			Group group = null;
			try {
				group = remote.getVanillaSecurityManager().getGroupById(groupId);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new ServiceException("Group with Id " + groupId + " was not found." + e1.getMessage());
			}
			if (group == null) {
				throw new ServiceException("Group with Id " + groupId + " was not found.");
			}

			/*------- We check the repository -----------*/
			Repository repository;
			try {
				repository = remote.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new ServiceException("Repository with Id " + repositoryId + " was not found. " + e1.getMessage());
			}

			return connect(infoUser, repository, group);
		}
		else {
			return infoUser;
		}
	}

	@Override
	public InfoUser initFromPortal(String sessionId, int groupId, int repositoryId) throws ServiceException {
		/*------- We get the remote Root to check if the user is valid -----------*/
		IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
		User user = null;
		try {
			user = remoteRoot.getVanillaSystemManager().getSession(sessionId).getUser();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("User is not valid." + e.getMessage());
		}

		if (user == null) {
			throw new ServiceException("User is not valid.");
		}

		InfoUser infos = connect(user.getLogin(), user.getPassword(), repositoryId, groupId);

		CommonSession session = getSession();
		try {
			CommonSessionManager.updateSession(sessionId, session);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update a session.", e);
		}

		return infos;
	}

	@Override
	public void disconnectUser() throws ServiceException {
		String clientIp = getThreadLocalRequest().getRemoteAddr();

		CommonSession session = getSession();

		boolean connectedToVanilla = false;
		try {
			connectedToVanilla = session.isConnectedToVanilla();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

		if (connectedToVanilla) {
			try {
				String realSessionId = ((RemoteVanillaSystemManager) session.getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();

				// We create an event to tell that someone is connected
				IVanillaEvent event = new VanillaActionEvent(null, realSessionId, session.getUser().getId(), session.getCurrentGroup().getId(), session.getCurrentRepository().getId(), VanillaActionType.DISCONNECT);
				session.getVanillaApi().getListenerService().fireEvent(event);

				SecurityLog logoutLog = new SecurityLog(TypeSecurityLog.LOGOUT, session.getUser().getId(), clientIp);
				session.getVanillaApi().getVanillaLoggingManager().saveSecurityLog(logoutLog);

				session.getVanillaApi().getVanillaSystemManager().deleteSession(realSessionId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CommonSessionManager.removeSession(session);
	}

	// @Override
	// public InfoUser connectUserWithBackup(String login, String backupCode) throws ServiceException {
	// IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
	//
	// User user = null;
	// try {
	// user = remoteRoot.getVanillaSecurityManager().getUserByLogin(login);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new ServiceException("Unable to get the user " + login + ": " + e.getMessage());
	// }
	//
	// if (user.getCodeBackupPassword() == null || !user.getCodeBackupPassword().equals(backupCode)) {
	// throw new ServiceException("The backup code does not match with the one sent by mail. Please, try again.");
	// }
	//
	// user.setPasswordChange(1);
	// user.setCodeBackupPassword("");
	//
	// try {
	// remoteRoot.getVanillaSecurityManager().updateUser(user);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new ServiceException("Unable to update the user " + login + ": " + e.getMessage());
	// }
	//
	// return login(user.getLogin(), user.getPassword());
	// }

	private boolean canAccessApplication(Integer groupId) throws ServiceException {
		CommonSession session = getSession();

		IVanillaContext context = CommonConfiguration.getInstance().getRootContext();

		IVanillaAPI api = new RemoteVanillaPlatform(context);

		try {
			return api.getVanillaSecurityManager().canAccessApp(groupId, session.getApplicationId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to access this application.", e);
		}
	}

	@Override
	public String getUserImg(int userId) throws ServiceException {
		CommonConfiguration config = CommonConfiguration.getInstance();
		try {
			byte[] imageByteArray = config.getImageForUser(userId);
			String base64 = new String(imageByteArray);
			base64 = "data:image/png;base64," + base64;
			return base64;
		} catch (Exception e) {
			logger.warn("An error happend during image recuperation");
			return null;
		}
	}

	@Override
	public String getGroupImg(int groupId) throws ServiceException {
		CommonConfiguration config = CommonConfiguration.getInstance();
		try {
			byte[] imageByteArray = config.getImageForGroup(groupId);
			if (imageByteArray == null) {
				return null;
			}
			String base64 = new String(imageByteArray);
			base64 = "data:image/png;base64," + base64;
			return base64;
		} catch (Exception e) {
			logger.warn("An error happend during group image recuperation");
			return null;
		}
	}

	@Override
	public List<bpm.document.management.core.model.User> getAklaboxUsers() throws ServiceException {
		CommonSession session = getSession();

		IVdmManager aklaManager = session.getAklaboxManager();
		try {
			return aklaManager.getOtherUsers(session.getUser().getLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the users from Aklabox.");
		}
	}

	@Override
	public List<bpm.document.management.core.model.Group> getAklaboxGroups() throws ServiceException {
		CommonSession session = getSession();

		IVdmManager aklaManager = session.getAklaboxManager();
		try {
			return aklaManager.getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the groups from Aklabox.");
		}
	}

	@Override
	public InfoUser connectToAklabox(InfoUser infoUser, String login, String password) throws ServiceException {
		CommonSession session = getSession();

		String md5Password = MD5Helper.encode(password);

		session.getUser().setAklaboxLogin(login);
		session.getUser().setAklaboxPassword(md5Password);

		session.initAklaboxSession(infoUser, true);

		try {
			User user = session.getVanillaApi().getVanillaSecurityManager().getUserById(session.getUser().getId());

			user.setAklaboxLogin(login);
			user.setAklaboxPassword(md5Password);

			session.getVanillaApi().getVanillaSecurityManager().updateUser(user);

			infoUser.getUser().setAklaboxLogin(login);
			infoUser.getUser().setAklaboxPassword(md5Password);
		} catch (Exception e) {
			e.printStackTrace();
			return infoUser;
		}

		return infoUser;
	}

	@Override
	public InfoUser disconnectFromAklabox(InfoUser infoUser) throws ServiceException {
		CommonSession session = getSession();

		session.getUser().setAklaboxLogin(null);
		session.getUser().setAklaboxPassword(null);

		session.initAklaboxSession(infoUser, false);

		try {
			User user = session.getVanillaApi().getVanillaSecurityManager().getUserById(session.getUser().getId());

			user.setAklaboxLogin(null);
			user.setAklaboxPassword(null);

			session.getVanillaApi().getVanillaSecurityManager().updateUser(user);

			infoUser.getUser().setAklaboxLogin(null);
			infoUser.getUser().setAklaboxPassword(null);
		} catch (Exception e) {
			e.printStackTrace();
			return infoUser;
		}

		return infoUser;
	}

	@Override
	public String connectToAklabox(String aklaboxUrl, String paramLocal) throws ServiceException {
		CommonSession session = getSession();

		String hash = null;
		try {
			hash = session.initAklaboxConnection(session);
		} catch (Exception e) {
			throw new ServiceException("Unable to connect to Aklabox and initialize the connection.");
		}

		if (hash == null) {
			throw new ServiceException("Unable to connect to Aklabox and initialize the connection.");
		}

		try {
			if (aklaboxUrl.contains("?")) {
				aklaboxUrl += "&hash=" + hash;
			}
			else {
				aklaboxUrl += "?hash=" + hash;
			}
			aklaboxUrl += "&locale=" + paramLocal;

			logger.info("forwarding to secured url : " + aklaboxUrl);

			return aklaboxUrl;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}
	}

	private String createCookieSessionId(CommonSession session) {
		String login = session.getUser().getLogin();
		String password = session.getUser().getPassword();
		int groupId = session.getCurrentGroup() != null ? session.getCurrentGroup().getId() : -1;
		int repositoryId = session.getCurrentRepository() != null ? session.getCurrentRepository().getId() : -1;

		String sessionId = String.valueOf(new Object().hashCode());
		InfoCookie info = new InfoCookie(login, password, groupId, repositoryId);
		sessionCookies.put(sessionId, info);

		return sessionId;
	}

	@Override
	public InfoUser connect(InfoUser infoUser, Repository repository, Group group) throws ServiceException {
		CommonSession session = getSession();

		String serverSessionId = null;
		try {
			serverSessionId = session.getLoginManager() != null ? session.getLoginManager().getServerSessionId(infoUser.getUser()) : null;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			if (session.isConnectedToVanilla()) {

				if (repository != null && group != null) {
					canAccessApplication(group.getId());

					session.initSession(group, repository);

					String vanillaSessionId = ((RemoteVanillaSystemManager) session.getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();
					if (serverSessionId == null) {
						serverSessionId = vanillaSessionId;
					}

					// We create an event to tell that someone is connected
					IVanillaEvent event = new VanillaActionEvent(null, vanillaSessionId, session.getUser().getId(), session.getCurrentGroup().getId(), session.getCurrentRepository().getId(), VanillaActionType.CONNECT);
					try {
						session.getVanillaApi().getListenerService().fireEvent(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			String sessionId = createCookieSessionId(session);

			infoUser.setSessionId(sessionId);
			updateInfoUser(session, infoUser, repository, group);
			session.setServerSessionId(serverSessionId);
			session.initAklaboxSession(infoUser, false);
			
			infoUser.setWopiServiceUrl(ConfigurationManager.getProperty(VanillaConfiguration.P_WOPI_WS_URL));

			if (infoUser.getUser() != null) {
				session.sendInfoUser(infoUser.getUser().getLogin(), infoUser.getUser().getPassword());
			}

			return infoUser;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available groups for the user.", e);
		}
	}

	private InfoUser buildInfoUser(CommonSession session, User user) throws Exception {
		CommonConfiguration config = CommonConfiguration.getInstance();

		InfoUser infoUser = new InfoUser();
		infoUser.setVanillaRuntimeUrl(config.getVanillaRuntimeUrl());
		infoUser.setVanillaRuntimeExternalUrl(config.getVanillaRuntimeExternalUrl());
		infoUser.setUrlDeconnect(config.getUrlDeconnect());
		infoUser.setUpdateManagerUrl(config.getUpdateManagerUrl());
		infoUser.setCanExportDashboard(config.canExportDashboard());
		infoUser.setFeedbackIsNotInited(config.feedbackIsNotInited());
		infoUser.setCanSendFeedback(config.isFeedbackAllowed());
		infoUser.setConnectedToVanilla(session.isConnectedToVanilla());

		infoUser.setUser(user);
		//TODO: Look in database
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			infoUser.setUserLastConnectionDate(sdf.parse("2016-06-12 18:07:04"));
		} catch (ParseException e) { }

		List<Group> availableGroups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
		infoUser.setAvailableGroups(availableGroups);
		
		return infoUser;
	}

	private void updateInfoUser(CommonSession session, InfoUser infoUser, Repository selectedRepository, Group selectedGroup) throws Exception {
		if (selectedGroup != null && selectedRepository != null) {
			infoUser.setGroup(selectedGroup);
			infoUser.setRepository(selectedRepository);

			List<Group> availableGroups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
			infoUser.setAvailableGroups(availableGroups);
		}

		session.setWebapplicationRights(infoUser);
	}

	@Override
	public HashMap<String, String> getVanillaContext() throws ServiceException {
		CommonSession session = getSession();
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			IVanillaContext context = session.getVanillaContext();
			result.put("login", context.getLogin());
			result.put("password", context.getPassword());
			result.put("url", context.getVanillaUrl());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get context" + ": " + e.getMessage());
		}
	}

	@Override
	public boolean checkHashCode(String hash) throws ServiceException {
		IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
		try {
			return remoteRoot.getVanillaSecurityManager().checkPasswordBackupHash(hash);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Settings checkLoginWithHash(String login, String hash) throws ServiceException {
		IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
		try {
			User user = remoteRoot.getVanillaSecurityManager().getUserByPasswordBackupHash(hash);
			if (user == null || !user.getLogin().equals(login)) {
				return null;
			}

			return remoteRoot.getVanillaSecurityManager().getSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void changePassword(String hash, String newPassword) throws ServiceException {
		try {
			IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
			User user = remoteRoot.getVanillaSecurityManager().getUserByPasswordBackupHash(hash);

			if (user.isSuperUser()) {
				throw new ServiceException("Administrator cannot change password using this screen.");
			}

			user.setPassword(Security.encodeMD5(newPassword));
			user.setDatePasswordModification(new Date());
			user.setPasswordChange(0);
			user.setPasswordReset(0);

			remoteRoot.getVanillaSecurityManager().updateUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PasswordState checkPassword(String hash, String newPassword) throws ServiceException {
		try {
			IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();
			User user = remoteRoot.getVanillaSecurityManager().getUserByPasswordBackupHash(hash);

			if (user != null && user.getPassword().equals(Security.encodeMD5(newPassword))) {
				return PasswordState.SAME_AS_BEFORE;
			}

			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String dictionnaryFile = config.getProperty(VanillaConfiguration.P_FORBIDDEN_WORDS_FILE_PATH);
			if (dictionnaryFile != null && !dictionnaryFile.isEmpty()) {
				String content = new String(Files.readAllBytes(Paths.get(dictionnaryFile)));
				String[] words = content.split(",");

				for (String word : words) {
					if (word.equals(newPassword)) {
						return PasswordState.NOT_ALLOWED;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return PasswordState.VALID;
		}

		return PasswordState.VALID;
	}

	@Override
	public boolean forgotPassword(String webappUrl, String username, boolean newAccount) throws ServiceException {
		IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();

		User user = null;
		try {
			user = remoteRoot.getVanillaSecurityManager().getUserByLogin(username);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("The user " + username + " does not exist : " + e.getMessage());
		}

		String hashCode = String.valueOf(new Object().hashCode());
		try {
			VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
			boolean needAdminConfirmation = vanillaConfig.getProperty(VanillaConfiguration.P_ADMINISTRATION_PASSWORD_NEEDADMIN) != null ? Boolean.parseBoolean(vanillaConfig.getProperty(VanillaConfiguration.P_ADMINISTRATION_PASSWORD_NEEDADMIN)) : false;
			
			Date endValidityDate = getPasswordChangeEndDateValidity(remoteRoot, newAccount);

			PasswordBackup backup = new PasswordBackup(hashCode, user.getId(), endValidityDate, !needAdminConfirmation);
			remoteRoot.getVanillaSecurityManager().managePasswordBackup(backup);

			if (!needAdminConfirmation) {
				sendPasswordChangeMail(remoteRoot, webappUrl, hashCode, user);
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to send a mail for the user " + username + " : " + e.getMessage());
		}
		return false;
	}
	
	private Date getPasswordChangeEndDateValidity(IVanillaAPI remoteRoot, boolean newAccount) throws Exception {
		Settings settings = remoteRoot.getVanillaSecurityManager().getSettings();
		int newAccountValidity = settings.getSettingAsInteger(SettingType.MAIL_FIRST_LOGIN_VALIDITY_DELAY);
		int changePasswordValidity = settings.getSettingAsInteger(SettingType.MAIL_CHANGE_PASSWORD_VALIDITY_DELAY);

		int validity = 30;
		if (newAccount && newAccountValidity > 0) {
			validity = newAccountValidity * 24 * 60;
		}
		else if (changePasswordValidity > 0) {
			validity = changePasswordValidity;
		}

		Calendar date = Calendar.getInstance();
		long t = date.getTimeInMillis();
		return new Date(t + (validity * 60 * 1000));
	}

	private String buildRecoveryPasswordText(String webappUrl, String backupUrl) {
		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		buf.append("    <head>");
		buf.append("    </head>");
		buf.append("    <body>");
		buf.append("        <h1> Vanilla Password Recovery</h1>");
		buf.append("        <p> A request to reset the password for your account has been made.<p>");
		buf.append("        <p>You may now enter use the following link to reset your password.<p>");
		buf.append("        <p><b>" + webappUrl + "?" + LoginHelper.HASH_KEY + "=" + backupUrl + "</b><p>");
		buf.append("        <p>--  BPM-Conseil team</p>");
		buf.append("    </body>");
		buf.append("</html>");
		return buf.toString();
	}
	
	private void sendPasswordChangeMail(IVanillaAPI remoteRoot, String webappUrl, String hashCode, User user) throws Exception {
		String htmlText = buildRecoveryPasswordText(webappUrl, hashCode);
		IMailConfig config = new MailConfig(user.getBusinessMail(), "do-not-respond@bpm-conseil.com", htmlText, "Password Recovery", true);

		logger.info("Password recovery : Sending email...");
		String res = remoteRoot.getVanillaSystemManager().sendEmail(config, new HashMap<String, InputStream>());
		logger.info("Password recovery : Mailing system says : " + res);
	}

	@Override
	public void changeUserPassword(String username, String password, String newPassword, boolean withoutConfirm) throws ServiceException {
		CommonSession session = getSession();
		IVanillaAPI remoteRoot = CommonConfiguration.getInstance().getRootVanillaApi();

		User user = null;
		if (withoutConfirm) {
			try {
				user = remoteRoot.getVanillaSecurityManager().getUserByLogin(username);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("The user " + username + " does not exist : " + e.getMessage());
			}
		}
		else {
			user = session.getUser();
		}

		CommonConfiguration config = CommonConfiguration.getInstance();

		try {
			config.modifyPassword(user, password, newPassword, withoutConfirm);

			String rootUser = config.getRootUser();
			if (user.getLogin().equals(rootUser)) {
				config.setRootPassword(newPassword);
			}

			String sessionId = ((RemoteVanillaSystemManager) session.getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();
			session.getVanillaApi().getVanillaSystemManager().deleteSession(sessionId);

			config.reconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<PasswordBackup> getPendingPasswordChangeDemands() throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaSecurityManager().getPendingPasswordChangeDemands();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get pending change password request : " + e.getMessage());
		}
	}
	
	@Override
	public void acceptPasswordChangeDemand(String webappUrl, PasswordBackup passwordBackup, boolean acceptDemand) throws ServiceException {
		CommonSession session = getSession();

		try {
			if (acceptDemand) {
				Date endValidityDate = getPasswordChangeEndDateValidity(session.getVanillaApi(), false);
		
				passwordBackup.setEndValidityDate(endValidityDate);
				passwordBackup.setAccepted(true);
				
				session.getVanillaApi().getVanillaSecurityManager().managePasswordBackup(passwordBackup);
				sendPasswordChangeMail(session.getVanillaApi(), webappUrl, passwordBackup.getCode(), passwordBackup.getUser());
			}
			else {
				session.getVanillaApi().getVanillaSecurityManager().removePasswordBackup(passwordBackup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to change demand status : " + e.getMessage());
		}
	}

	@Override
	public boolean isUserChangePassword() throws ServiceException {
		CommonSession session = getSession();

		User user = null;
		try {
			user = session.getVanillaApi().getVanillaSecurityManager().getUserById(session.getUser().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		int nbDayValidity = user.getNbDayPasswordValidity();

		if (nbDayValidity != 0) {
			Date lastModification = user.getDatePasswordModification();
			Date today = new Date();
			int days = daysBetween(lastModification, today);

			if (nbDayValidity <= days) {
				return true;
			}
		}

		if (user.getPasswordChange() == 0) {
			if (user.getPasswordReset() == 1) {
				return true;
			}
			return false;
		}
		else {
			return true;
		}
	}

	private int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	@Override
	public InfoUser getInfoUser() throws ServiceException {
		CommonSession session = getSession();
		return session.getInfoUser();
	}
	
	@Override
	public InfoUser initFromKeycloak(String token) throws ServiceException {
		if (token != null && !token.isEmpty()) {
			logger.info("Keycloak login attempt detected");
			return authentify(new Authentication(token));
		}

		return null;
	}
	
	private InfoUser authentify(Authentication auth) throws ServiceException {
		String clientIp = getThreadLocalRequest().getRemoteAddr();

		CommonSession session = getSession();
		try {
			checkLicence(session);
		} catch (LicenceExpiredException e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

		boolean connectedToVanilla = false;
		try {
			connectedToVanilla = session.isConnectedToVanilla();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

		User user = null;
		if (connectedToVanilla) {
//			IVanillaContext context = new BaseVanillaContext(vanillaRuntimeUrl, username, password);
//			RemoteVanillaPlatform remote = new RemoteVanillaPlatform(context);
			IVanillaAPI rootVanillaApi = CommonConfiguration.getInstance().getRootVanillaApi();

			try {
				if (auth.getType() == TypeAuthentication.CREDENTIALS) {
					user = rootVanillaApi.getVanillaSecurityManager().authentify(clientIp, auth.getLogin(), auth.getPassword(), false);
				}
				else if (auth.getType() == TypeAuthentication.TOKEN) {
					user = rootVanillaApi.getVanillaSecurityManager().checkKeycloak(auth.getToken());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(e.getMessage());
			}
		}
		else {
			try {
				user = session.getLoginManager().authentify(auth.getLogin(), auth.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to authentify user " + auth.getLogin() + ": " + e.getMessage());
			}
		}

		session.setSessionInformations(user, vanillaRuntimeUrl);
		
		try {
			InfoUser infoUser = buildInfoUser(session, user);
			session.setInfoUser(infoUser);
			return infoUser;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
}
