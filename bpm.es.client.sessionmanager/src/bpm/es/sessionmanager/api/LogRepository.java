package bpm.es.sessionmanager.api;

import java.util.Date;

import bpm.vanilla.platform.core.repository.RepositoryLog;


public class LogRepository implements ILog{

	private RepositoryLog log;
	
	public LogRepository(RepositoryLog log) {
		this.log = log;
	}
	
	public RepositoryLog getLog() {
		return log;
	}
	public int getObjectId() {
		return log.getObjectId();
	}
	
	public String getObjectName() {
		//return log.
		return log.getObjectName();
	}
	
	public void setObjectName(String name) {
		log.setObjectName(name);
	}
	
	public long getDelay() {
		return log.getDelay();
	}
	
	public Date getDate() {
		return log.getTime();
	}
	
	public int getUserId() {
		return log.getUserId();
	}
	
	public String getUserName() {
		return log.getUserName();
	}
	
	public void setUserName(String name) {
		log.setUserName(name);
	}
	
	public int getGroupId() {
		return log.getGroupId();
	}
	
	public void setGroupName(String name) {
		log.setGroupName(name);
	}
	
	public String getGroupName() {
		return log.getGroupName();
	}
	
	public String getApplicationName() {
		return log.getAppName();
	}
	
	public String getOperationName() {
		return log.getOperation();
	}
}
