package bpm.gwt.aklabox.commons.server.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import bpm.gwt.aklabox.commons.client.services.exceptions.SecurityException;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;


/**
 * Helper functions to set and extract information from requests
 */
public class CommonSessionHelper {

	private static String PORTAL_SESSION_ID = "bpm.vanilla.portal.sessionId";

	private static Logger logger = Logger.getLogger(CommonSessionHelper.class);

	public static <T extends CommonSession> T getCurrentSession(HttpServletRequest request, Class<T> type) throws ServiceException {
		String sessionId = (String) request.getSession().getAttribute(CommonSessionHelper.PORTAL_SESSION_ID);
		
		if (sessionId == null || sessionId.isEmpty()) {
			logger.warn("Trying to get existing sessionId : No session id found");
			throw new SecurityException(SecurityException.ERROR_TYPE_SESSION_EXPIRED, "Session is expired.");
		}
		else {
			logger.debug("Trying to get existing sessionId : found = " + sessionId);
		}

		return type.cast(CommonSessionManager.getSessionById(sessionId));
	}

	/**
	 * need to be called to set sessionId in requests.
	 */
	public static void setCurrentSessionId(String sessionId, HttpServletRequest request) {
		logger.info("Setting current sessionId : " + sessionId);
		request.getSession().setAttribute(CommonSessionHelper.PORTAL_SESSION_ID, sessionId);
	}

	public static void removeSessionId(HttpServletRequest request) {
		logger.info("Removing current sessionId.");
		request.getSession().removeAttribute(CommonSessionHelper.PORTAL_SESSION_ID);
	}

}
