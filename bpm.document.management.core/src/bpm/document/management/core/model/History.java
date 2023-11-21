package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int historyId=0;
	private int userId=0;
	private String request="";
	private Date executionDate=new Date();
	private Date executionDelay=new Date();
	
	public int getHistoryId() {
		return historyId;
	}
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public Date getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
	public Date getExecutionDelay() {
		return executionDelay;
	}
	public void setExecutionDelay(Date executionDelay) {
		this.executionDelay = executionDelay;
	}
}
