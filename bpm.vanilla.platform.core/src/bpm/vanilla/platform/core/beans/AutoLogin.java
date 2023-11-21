package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

public class AutoLogin implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String keyAutoLogin;
	private int userId;
	private Date creationDate;
	private Date endValidityDate;

	public AutoLogin() { }
	
	public AutoLogin(String keyAutoLogin, int userId, long expirationDelai) {
		this.keyAutoLogin = keyAutoLogin;
		this.userId = userId;
		this.creationDate = new Date();
		this.endValidityDate = new Date(System.currentTimeMillis() + expirationDelai);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyAutoLogin() {
		return keyAutoLogin;
	}
	
	public void setKeyAutoLogin(String keyAutoLogin) {
		this.keyAutoLogin = keyAutoLogin;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getEndValidityDate() {
		return endValidityDate;
	}

	public void setEndValidityDate(Date endValidityDate) {
		this.endValidityDate = endValidityDate;
	}
}
