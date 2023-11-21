package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class LoginAttempt implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int userId;
	private String userName;
	private String status;
	private Date loginDate;
	private String role;
	private String reason;
	
	public LoginAttempt() {
		// TODO Auto-generated constructor stub
	}

	public LoginAttempt(int userId, String userName, String status, Date loginDate, String role, String reason) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.status = status;
		this.loginDate = loginDate;
		this.role = role;
		this.reason = reason;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	

}
