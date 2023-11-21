package bpm.fwr.server.security;
//package com.bpm.fwr.server.security;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.log4j.Logger;
//
///**
// * Helper functions to set and extract information from requests
// * 
// * @author svi
// *
// */
//public class FwrSessionHelper {
//	
//	private static String FWR_SESSION_ID = "bpm.vanilla.fwr.sessionId"; 
//
//	private static Logger logger = Logger.getLogger(FwrSessionHelper.class);
//	
//	public static FwrSession getCurrentSession(HttpServletRequest request) throws Exception {
//		String sessionId = (String)request.getSession().getAttribute(FwrSessionHelper.FWR_SESSION_ID);
//        
//		if (sessionId == null || sessionId.isEmpty()) {
//			logger.error("Trying to get existing sessionId : No session id found");
//			throw new Exception("No session id found.");
//		} 
//		else {
//			logger.debug("Trying to get existing sessionId : found = " + sessionId);
//		}
//		
//        return FwrSessionManager.getSessionById(sessionId);
//	}
//	
//	public static FwrSession getCurrentSessionNoException(HttpServletRequest request) {
//		String sessionId = (String)request.getSession().getAttribute(FwrSessionHelper.FWR_SESSION_ID);
//        
//		if (sessionId == null || sessionId.isEmpty()) {
//			logger.error("Trying to get existing sessionId : No session id found");
//			return null;
//		} else {
//			logger.debug("Trying to get existing sessionId : found = " + sessionId);
//		}
//		
//        return FwrSessionManager.getSessionById(sessionId);
//	}
//	
//	/**
//	 * need to be called to set sessionId in requests.
//	 */
//	public static void setCurrentSessionId(String sessionId, HttpServletRequest request) {
//		logger.info("Setting current sessionId : " + sessionId); 
//		request.getSession().setAttribute(FwrSessionHelper.FWR_SESSION_ID, sessionId);
//	}
//	
//	
//	public static void removeSessionId(HttpServletRequest request) {
//		logger.info("Removing current sessionId.");
//		request.getSession().removeAttribute(FwrSessionHelper.FWR_SESSION_ID);
//	}
//
//}
