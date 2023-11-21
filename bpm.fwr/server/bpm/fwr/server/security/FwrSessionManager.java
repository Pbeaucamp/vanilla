package bpm.fwr.server.security;
//package com.bpm.fwr.server.security;
//
//import java.util.HashMap;
//import java.util.UUID;
//
//import org.apache.log4j.Logger;
//
//import bpm.vanilla.platform.core.beans.Group;
//import bpm.vanilla.platform.core.beans.Repository;
//import bpm.vanilla.platform.core.beans.User;
//
//public class FwrSessionManager {
//
//	private static Logger logger = Logger.getLogger(FwrSessionManager.class);
//	
//	private static HashMap<String, FwrSession> sessions = new HashMap<String, FwrSession>();
//	
//	public static String createSession(String vanillaUrl, User user) throws Exception {
//		
//		String sessionId = generateSessionId();
//		
//		logger.info("Creating session with id " + sessionId + " for user " + user.getName());
//		
//		FwrSession session = new FwrSession(sessionId, vanillaUrl, user);
//		
//		logger.debug("Created Session.");
//		
//		logger.debug("Add: Waiting for lock on session list...");
//		
//		synchronized (sessions) {
//			logger.debug("Add: Got lock on session list, adding new session.");
//			sessions.put(sessionId, session);
//		}
//		
//		return sessionId;
//	}
//	
//	public static void createSessionWithSessionId(String sessionId, String vanillaUrl,
//			User user, Group group, Repository repository) throws Exception {
//		
//		logger.info("Creating session with id " + sessionId + " for user " + user.getName() 
//				+ " using group " + group + " on repository " + repository);
//		
//		FwrSession session = new FwrSession(sessionId, vanillaUrl, user, group, repository);
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
//	
//	/**
//	 * Generates and returns a unique session id.
//	 * 
//	 * @return
//	 */
//	private static String generateSessionId() {
//		
//		boolean isTaken;
//		String random;
//		
//		do {
//			random = UUID.randomUUID().toString();
//			synchronized (sessions) {
//				isTaken = sessions.containsKey(random);
//			}
//		} while (isTaken);
//		
//		return random;
//	}
//	
//	/**
//	 * Might return null
//	 * 
//	 * @param sessionId
//	 * @return
//	 */
//	public static FwrSession getSessionById(String sessionId) {
//		FwrSession session;
//		
//		//logger.debug("Get: Waiting for lock on session list...");
//		
//		synchronized (sessions) {
//			//logger.debug("Get: Got lock on session list, returning session.");
//			session = sessions.get(sessionId);
//		}
//		
//		return session;
//	}
//	
//	/**
//	 * 
//	 * @param sessionId
//	 */
//	public static void removeSession(FwrSession session) {
//		logger.debug("Remove: Waiting for lock on session list...");
//		
//		synchronized (sessions) {
//			logger.debug("Remove: Got lock on session list, removing session.");
//			sessions.remove(session.getSessionId());
//		}
//		logger.debug("Session removed.");
//	}
//}
