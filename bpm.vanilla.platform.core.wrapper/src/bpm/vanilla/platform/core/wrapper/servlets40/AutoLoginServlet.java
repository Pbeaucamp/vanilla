package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class AutoLoginServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;
	
	private static final String PARAM_LOGIN = "login";
	private static final long EXPIRATION_DELAI_IN_MS = 1800000;

	private IVanillaLogger logger;
	private IVanillaComponentProvider component;
	private boolean isAutoLoginActivated = false;
	
	public AutoLoginServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		logger.info("Initializing AutoLoginServlet...");
		this.component = componentProvider;
		this.logger = logger;
		
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String autoLogin = config.getProperty(VanillaConfiguration.P_WEBAPPS_SSO_AUTOLOGIN);
		this.isAutoLoginActivated = autoLogin != null && Boolean.parseBoolean(autoLogin);

		logger.info("AutoLogin is " + (isAutoLoginActivated ? "" : "not") + " activated");
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		if (isAutoLoginActivated) {
			String authorizedIp = config.getProperty(VanillaConfiguration.P_WEBAPPS_SSO_AUTHORIZED_IPS);
			
			String clientIP = req.getRemoteAddr();
			if (clientIP.equals(authorizedIp)) {
				try {
					String login = req.getParameter(PARAM_LOGIN);
					User user = component.getSecurityManager().getUserByLogin(login);
					if (user == null) {
						logger.info("User with login " + login + " not found");
						resp.sendError(HttpServletResponse.SC_NOT_FOUND);
						return;
					}
					
					String portal = config.getProperty(VanillaConfiguration.P_WEBAPPS_PORTAL);
					
					String keyAutoLogin = component.getSecurityManager().buildAutoLoginKey(user, EXPIRATION_DELAI_IN_MS);
					String url = portal + (portal.contains("?") ? "&" : "?") + "bpm.vanilla.autoLogin=true" + "&bpm.vanilla.hash=" + keyAutoLogin;
					
					Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION_DELAI_IN_MS);
					
					JSONObject result = new JSONObject();
					result.put("result", "success");
					result.put("login_url", url);
					result.put("expiration_date", expireDate);

					resp.getWriter().write(result.toString());
					resp.getWriter().flush();
					resp.getWriter().close();
					
				} catch (Exception e) {
					e.printStackTrace();
					
					JSONObject result = new JSONObject();
					try {
						result.put("result", "error");
						result.put("message", e.getMessage());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					resp.getWriter().write(result.toString());
					resp.getWriter().flush();
					resp.getWriter().close();

				}
			}
			else {
				logger.info("The IP " + clientIP + " is not authorized.");
			}
		}
		else {
			logger.info("AutoLogin is not activated");
		}
		
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}
