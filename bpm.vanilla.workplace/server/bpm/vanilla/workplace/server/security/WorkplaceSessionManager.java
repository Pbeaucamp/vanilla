package bpm.vanilla.workplace.server.security;

import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import bpm.vanilla.workplace.shared.model.PlaceWebUser;

public class WorkplaceSessionManager {

	private static Logger logger = Logger.getLogger(WorkplaceSessionManager.class);
	
	private static HashMap<String, WorkplaceSession> sessions = new HashMap<String, WorkplaceSession>();
	
	public static String createSession(PlaceWebUser user) throws Exception {
		
		String sessionId = generateSessionId();

		logger.info("Creating session with id " + sessionId + " for user " + user.getName());
		
		WorkplaceSession session = new WorkplaceSession(sessionId, user);
		
		logger.debug("Created Session.");
		
		logger.debug("Add: Waiting for lock on session list...");
		
		synchronized (sessions) {
			logger.debug("Add: Got lock on session list, adding new session.");
			sessions.put(sessionId, session);
		}
		
		return sessionId;
	}
	
	/**
	 * Generates and returns a unique session id.
	 * 
	 * @return
	 */
	private static String generateSessionId() {
		
		boolean isTaken;
		String random;
		
		do {
			random = UUID.randomUUID().toString();
			synchronized (sessions) {
				isTaken = sessions.containsKey(random);
			}
		} while (isTaken);
		
		return random;
	}
	
	/**
	 * Might return null
	 * 
	 * @param sessionId
	 * @return
	 */
	public static WorkplaceSession getSessionById(String sessionId) {
		WorkplaceSession session;
		
		//logger.debug("Get: Waiting for lock on session list...");
		
		synchronized (sessions) {
			//logger.debug("Get: Got lock on session list, returning session.");
			session = sessions.get(sessionId);
		}
		
		return session;
	}
	
	/**
	 * 
	 * @param sessionId
	 */
	public static void removeSession(WorkplaceSession session) {
		logger.debug("Remove: Waiting for lock on session list...");
		
		synchronized (sessions) {
			logger.debug("Remove: Got lock on session list, removing session.");
			sessions.remove(session.getSessionId());
		}
		logger.debug("Session removed.");
	}
}
