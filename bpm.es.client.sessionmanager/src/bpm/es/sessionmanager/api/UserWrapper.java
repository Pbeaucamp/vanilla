package bpm.es.sessionmanager.api;

import java.util.Date;

import bpm.vanilla.platform.core.beans.User;


public class UserWrapper {

	private User user;
	
	private boolean isConnected;
	private Date connectedSince;
	private Date lastAction;
	
	public UserWrapper(User u) {
		user = u;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	protected void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public Date getConnectedSince() {
		return connectedSince;
	}
	
	protected void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}
	
	public Date getLastAction() {
		return lastAction;
	}
	
	protected void setLastAction(Date lastAction) {
		this.lastAction = lastAction;
	}
}
