package bpm.vanilla.workplace.server.security;

import org.apache.log4j.Logger;

import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

public class WorkplaceSession {
	public static final String SESSION_ID = "sessionID";

	private Logger logger = Logger.getLogger(this.getClass());
	
	private String sessionId;
	
	private PlaceWebUser user;
	private PlaceWebPackage packageInformations;
	
	protected WorkplaceSession(String sessionId, PlaceWebUser user) throws Exception {
		
		this.sessionId = sessionId;
		this.user = user;

		logger.info("Workplace Session created.");
	}
	
	/**
	 * This is the Workplace session id.
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	public PlaceWebUser getUser() {
		return user;
	}

	public void setPackageInformations(PlaceWebPackage packageInformations) {
		this.packageInformations = packageInformations;
	}

	public PlaceWebPackage getPackageInformations() {
		return packageInformations;
	}
}
