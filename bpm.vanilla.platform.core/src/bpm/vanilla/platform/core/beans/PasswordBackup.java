package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;

public class PasswordBackup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String code;
	private int userId;
	private Date creationDate;
	private Date endValidityDate;
	private boolean accepted;
	
	@Transient
	private User user;

	public PasswordBackup() { }
	
	public PasswordBackup(String code, int userId, Date endValidityDate, boolean accepted) {
		this.code = code;
		this.userId = userId;
		this.endValidityDate = endValidityDate;
		this.accepted = accepted;
		this.creationDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
	
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
}
