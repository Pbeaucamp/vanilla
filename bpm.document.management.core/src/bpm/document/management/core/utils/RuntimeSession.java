package bpm.document.management.core.utils;

import java.io.Serializable;
import java.util.Date;

import bpm.document.management.core.model.User;

public class RuntimeSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private User user;
	private Date creationDate;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
