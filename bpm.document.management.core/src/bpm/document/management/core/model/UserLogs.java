package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class UserLogs implements Serializable {
	private static final long serialVersionUID = 1L;

	private int logId = 0;
	private String tasks = "";
	private int userId = 0;
	private String type = "";
	private Date date = new Date();
	private Integer docId = 0;
	private String userType = "";
	
	private User user;

	public UserLogs() { }

	public UserLogs(int userId, String type, Date date, int docId, String userType) {
		super();
		this.userId = userId;
		this.type = type;
		this.date = date;
		this.docId = docId;
		this.userType = userType;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public String getTasks() {
		return tasks;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
}
