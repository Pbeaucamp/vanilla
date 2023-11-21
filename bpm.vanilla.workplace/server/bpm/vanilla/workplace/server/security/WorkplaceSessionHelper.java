package bpm.vanilla.workplace.server.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;
import bpm.vanilla.workplace.shared.model.PlaceWebLog;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;
import bpm.vanilla.workplace.shared.model.PlaceWebLog.LogType;

/**
 * Helper functions to set and extract information from requests
 * 
 * @author svi
 *
 */
public class WorkplaceSessionHelper {
	
	private static String WORKPLACE_SESSION_ID = "bpm.vanilla.portal.sessionId"; 

	private static Logger logger = Logger.getLogger(WorkplaceSessionHelper.class);
	
	public static WorkplaceSession getCurrentSession(HttpServletRequest request) throws ServiceException {
		String sessionId = (String)request.getSession().getAttribute(WorkplaceSessionHelper.WORKPLACE_SESSION_ID);
        
		if (sessionId == null || sessionId.isEmpty()) {
			logger.error("Trying to get existing sessionId : No session id found");
			throw new ServiceException("No session id found.");
		} else {
			logger.debug("Trying to get existing sessionId : found = " + sessionId);
		}
		
        return WorkplaceSessionManager.getSessionById(sessionId);
	}
	
	/**
	 * need to be called to set sessionId in requests.
	 */
	public static void setCurrentSessionId(String sessionId, HttpServletRequest request) {
		logger.info("Setting current sessionId : " + sessionId); 
		request.getSession().setAttribute(WorkplaceSessionHelper.WORKPLACE_SESSION_ID, sessionId);
	}
	
	
	public static void removeSessionId(HttpServletRequest request) {
		logger.info("Removing current sessionId.");
		request.getSession().removeAttribute(WorkplaceSessionHelper.WORKPLACE_SESSION_ID);
	}
	
	public static void createSession(PlaceWebUser user, HttpServletRequest request, 
			PlaceConfiguration config) throws ServiceException{
		String sessionId;
		try {
			sessionId = WorkplaceSessionManager.createSession(user);
			WorkplaceSessionHelper.setCurrentSessionId(sessionId, request);
			
			PlaceWebLog log = new PlaceWebLog(LogType.LOGIN, user.getId(), new Date(), null, null);
			config.getLogDao().save(log);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (Throwable e) {
			String msg = "Failed to create session : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

}
