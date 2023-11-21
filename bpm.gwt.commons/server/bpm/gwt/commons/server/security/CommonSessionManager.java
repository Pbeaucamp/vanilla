package bpm.gwt.commons.server.security;

import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;

public class CommonSessionManager {

	private static Logger logger = Logger.getLogger(CommonSessionManager.class);
	
	private static HashMap<String, CommonSession> sessions = new HashMap<String, CommonSession>();
	
	public static String createSession(Class<? extends CommonSession> type) throws Exception {
		
		String sessionId = generateSessionId();
		
		CommonSession session = type.newInstance();
		logger.debug("Created Session.");
		
		logger.debug("Add: Waiting for lock on session list...");
		synchronized (sessions) {
			logger.debug("Add: Got lock on session list, adding new session.");
			sessions.put(sessionId, session);
		}
		
		return sessionId;
	}
	
//	public static void createSessionWithSessionId(String sessionId, String vanillaUrl,
//			User user, Group group, Repository repository, Class<? extends CommonSession> type) throws Exception {
//		
//		logger.info("Creating session with id " + sessionId + " for user " + user.getName() 
//				+ " using group " + group + " on repository " + repository);
//
//		CommonSession session = type.newInstance();
//		session.setSessionId(sessionId);
//		session.setSessionInformations(user, vanillaUrl);
//		session.initSession(group, repository);
//		
//		logger.debug("Created Session.");
//		
//		logger.debug("Add: Waiting for lock on session list...");
//		
//		synchronized (sessions) {
//			logger.debug("Add: Got lock on session list, adding new session.");
//			sessions.put(sessionId, session);
//		}
//	}
	
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
	public static CommonSession getSessionById(String sessionId) {
		CommonSession session;
		
		synchronized (sessions) {
			session = sessions.get(sessionId);
		}
		
		return session;
	}
	
	public static void removeSession(CommonSession session) {
		logger.debug("Remove: Waiting for lock on session list...");
		
		synchronized (sessions) {
			logger.debug("Remove: Got lock on session list, removing session.");
			sessions.remove(session.getSessionId());
		}
		logger.debug("Session removed.");
	}
	
	/**
	 * This method remove the session from the stack, update his session id and add it again with his new id
	 * 
	 * @param sessionId
	 * @param session
	 */
	public static void updateSession(String sessionId, CommonSession session) {
		logger.debug("Remove: Waiting for lock on session list...");
		
		synchronized (sessions) {
			logger.debug("Remove: Got lock on session list, removing session.");
			sessions.remove(session.getSessionId());
		}
		logger.debug("Session removed.");
		
		logger.debug("Add: Waiting for lock on session list...");
		synchronized (sessions) {
			logger.debug("Add: Got lock on session list, adding new session.");
			sessions.put(sessionId, session);
		}
		
		session.setSessionId(sessionId);
	}
}
