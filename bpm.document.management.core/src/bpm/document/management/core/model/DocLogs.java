package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class DocLogs implements Serializable {

	private static final long serialVersionUID = 1L;

	private int docLogId;
	private int docId;
	private String type;
	private String log;
	private int userId;
	private Date date = new Date();
	private String userRole = "";

	public DocLogs() {
		// TODO Auto-generated constructor stub
	}

	public DocLogs(int docId, String type, String log, int userId, String userRole) {
		super();
		this.docId = docId;
		this.type = type;
		this.log = log;
		this.userId = userId;
		this.userRole = userRole;
	}

	public int getDocLogId() {
		return docLogId;
	}

	public void setDocLogId(int docLogId) {
		this.docLogId = docLogId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

}
