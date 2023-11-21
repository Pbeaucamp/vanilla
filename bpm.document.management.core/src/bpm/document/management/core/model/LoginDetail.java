package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class LoginDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int userId;
	private Date loginDate = new Date();
	private Date logoutDate = new Date();
	private String type;

	public LoginDetail() {
	}

	public LoginDetail(int userId, Date loginDate, Date logoutDate, String type) {
		super();
		this.userId = userId;
		this.loginDate = loginDate;
		this.logoutDate = logoutDate;
		this.type = type;
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

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
