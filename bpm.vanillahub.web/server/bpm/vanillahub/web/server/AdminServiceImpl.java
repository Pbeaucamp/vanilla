package bpm.vanillahub.web.server;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.ILoginManager;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.core.config.ConfigurationAklaboxConstants;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanillahub.remote.RemoteAdminManager;
import bpm.vanillahub.web.client.services.AdminService;
import bpm.vanillahub.web.server.security.VanillaHubSession;
import bpm.vanillahub.web.shared.Dummy;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService, ILoginManager {

	private static final long serialVersionUID = 1L;

	private String hubRuntimeUrl;
	private String vanillaRuntimeUrl;
	
	private boolean aklaboxOrigin;

	@Override
	public void init() throws ServletException {
		super.init();

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaRuntimeUrl = config.getVanillaServerUrl();
		this.hubRuntimeUrl = config.getProperty(VanillaConfiguration.P_HUB_RUNTIME_URL);
		this.aklaboxOrigin = config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_ORIGIN) != null ? Boolean.parseBoolean(config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_ORIGIN)) : false;
		
		// Create a trust manager that does not validate certificate chains for OpenData HTTPS connection
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {}
	}

	private VanillaHubSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), VanillaHubSession.class);
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	/**
	 * For serialization purpose. Needed by GWT. Don't touch this.
	 */
	@Override
	public Dummy dummy(Dummy d) {
		return new Dummy();
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(VanillaHubSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());

			VanillaHubSession session = getSession();
			session.setLoginManager(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public void initVanillaHubSession(InfoUser infoUser) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			session.initSession(infoUser.getUser(), session.getServerSessionId(), getLocale());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public User authentify(String login, String password) throws Exception {
		RemoteAdminManager manager = new RemoteAdminManager(hubRuntimeUrl, null, getLocale());

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
		RemoteAdminManager manager = new RemoteAdminManager(hubRuntimeUrl, null, getLocale());
		return manager.connect(user);
	}

	@Override
	public boolean isConnectedToVanilla() throws Exception {
		if (vanillaRuntimeUrl == null || vanillaRuntimeUrl.isEmpty() || aklaboxOrigin) {
			return false;
		}
		return true;
	}

	@Override
	public InfoConnection getConnectionInformations() {
		VanillaLocale locEn = new VanillaLocale();
		locEn.setId(1);
		locEn.setName("English");
		locEn.setLocaleValue("en_EN");
		locEn.setLocaleOrder(1);

		VanillaLocale locFr = new VanillaLocale();
		locEn.setId(2);
		locFr.setName("French");
		locFr.setLocaleValue("fr_FR");
		locFr.setLocaleOrder(2);

		List<VanillaLocale> locales = new ArrayList<>();
		locales.add(locEn);
		locales.add(locFr);

		InfoConnection infos = new InfoConnection();
		infos.setIncludeFastConnect(false);
		infos.setLocales(locales);
		return infos;
	}
}
