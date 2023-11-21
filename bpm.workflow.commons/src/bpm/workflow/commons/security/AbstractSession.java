package bpm.workflow.commons.security;

import java.util.Calendar;
import java.util.Date;

import bpm.vanilla.platform.core.beans.User;

/**
 * Store the datas for a session.
 * 
 * When this session is used, a call to setUsedTime is required to update the object
 * and disallow the SessionHolder cleaner thread to remove this SessionContent.
 */
public abstract class AbstractSession {
	public static final long maxDurationSession = 1000 * 60 * 300;
	
	private String sessionId;
	private Date lastUsedTime = Calendar.getInstance().getTime();
	
	private User user;
	
	public AbstractSession() { }

	public void setUsedTime(){
		synchronized (lastUsedTime) {
			lastUsedTime = Calendar.getInstance().getTime();
		}
	}
	
	public String getSessionId(){
		return sessionId;
	}
	
	public User getUser() {
		return user;
	}

	public boolean timeOutReached() {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		
		return currentTime > lastUsedTime.getTime() + maxDurationSession;
	}
	
	public void setInfos(String sessionId, User user) {
		this.sessionId = sessionId;
		this.user = user;
	}
}
