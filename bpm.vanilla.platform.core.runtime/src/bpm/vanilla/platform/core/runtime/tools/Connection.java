package bpm.vanilla.platform.core.runtime.tools;

import java.util.Calendar;

public class Connection {

	private String ip;
	private String login;

	private int nbMaxConnection;
	private int nbTryConnection = 0;
	private int inactivationDelay;
	
	private long maxTryReachedTime = -1;
	
	public Connection(String ip, String login, int nbMaxConnection, int inactivationDelay) {
		this.ip = ip;
		this.login = login;
		this.nbMaxConnection = nbMaxConnection;
		this.nbTryConnection = 1;
		this.inactivationDelay = inactivationDelay;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getLogin() {
		return login;
	}

	public void increaseNbConnection() {
		this.nbTryConnection = nbTryConnection + 1;
		if(nbTryConnection > nbMaxConnection) {
			this.maxTryReachedTime = Calendar.getInstance().getTimeInMillis();
		}
	}

	public void resetNbConnection() {
		this.nbTryConnection = 0;
	}

	public boolean isMaxTryReached() {
		return nbTryConnection > nbMaxConnection;
	}
	
	public int getInactivationDelay() {
		return inactivationDelay;
	}

	public long getMaxTryReachedTime() {
		return maxTryReachedTime;
	}
}
