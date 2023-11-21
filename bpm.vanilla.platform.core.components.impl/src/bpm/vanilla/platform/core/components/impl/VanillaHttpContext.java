package bpm.vanilla.platform.core.components.impl;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.osgi.service.http.HttpContext;

import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.exceptions.LimitNumberConnectionException;
import bpm.vanilla.platform.core.exceptions.PasswordException;
import bpm.vanilla.platform.core.exceptions.UserNotFoundException;

public class VanillaHttpContext implements HttpContext {

	private IVanillaSecurityManager securityManager;
	private IVanillaSystemManager systemManager;

	public VanillaHttpContext(IVanillaSecurityManager securityManager, IVanillaSystemManager systemManager) {
		this.securityManager = securityManager;
		this.systemManager = systemManager;
	}

	@Override
	public String getMimeType(String name) {
		return null;
	}

	@Override
	public URL getResource(String name) {
		return null;
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = (String) request.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);

		if (sessionId != null) {
			try {
				VanillaSession session = systemManager.getSession(sessionId);
				if (session == null) {
					response.getWriter().write("<error><session>The Session has expired</session></error>");
					return false;
				}
				session.refreshTime();

				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		else {

			String authhead = request.getHeader("Authorization");

			if (authhead == null) {
				response.addHeader("WWW-Authenticate", "missing authentication HTTP header");
				response.setStatus(401);
				response.getWriter().write("<error>missing authentication HTTP header</error>");
				return false;
			}
			byte[] authentication = null;
			byte[] usernpass = null;

			String[] datas = null;
			String login = null;
			String password = null;
			String ip = null;
			try {
				authentication = authhead.substring(6).getBytes();
				usernpass = Base64.decodeBase64(authentication);
				datas = new String(usernpass).split(":");

				login = datas[0];
				password = datas[1];
				if (datas.length > 2) {
					ip = datas[2];
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				response.addHeader("WWW-Authenticate", "Wrong format for Basic Authorization in HttpHeader");
				response.setStatus(401);
				response.getWriter().write("<error>Wrong format for Basic Authorization in HttpHeader</error>");
				return false;
			}

			try {
				if (ip == null || ip.equalsIgnoreCase("null") || ip.isEmpty()) {
					ip = request.getRemoteAddr();
				}

				// check if a session exists
				VanillaSession session = systemManager.getSession(login, password);
				User user = null;
				if (session == null) {
					user = securityManager.authentify(ip, login, password, false);
				}
				else {
					user = session.getUser();
				}

				if (user != null) {

					if (session == null) {
						session = systemManager.createSession(user);
					}

					request.setAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, session.getUuid());
					request.getSession().setAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, session.getUuid());

					response.addHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, session.getUuid());
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (e instanceof LimitNumberConnectionException) {
						response.getWriter().write("<error>" + e.getMessage() + "</error>");
					}
					else if (e instanceof PasswordException || e instanceof UserNotFoundException) {
						User user = securityManager.getUserByLogin(login);
						if (user == null) {
							response.getWriter().write("<error><user>" + login + "</user></error>");
						}
						else {
							response.getWriter().write("<error><password></password></error>");
						}
					}
					else {
						response.getWriter().write("<error>" + e.getMessage() + "</error>");
					}
				} catch (Exception ex) {
					response.getWriter().write("<error><authentication>" + e.getMessage() + "</authentication></error>");
				}

			}

		}

		return false;
	}

}
