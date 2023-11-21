package bpm.document.management.core.model;

import java.io.Serializable;

import bpm.document.management.core.model.Enterprise.TypeUser;

public class UserEnterprise implements Serializable {

	private static final long serialVersionUID = 1L;

	private int ueId = 0;
	private int userId = 0;
	private int enterpriseId = 0;

	private TypeUser type;

	private User user;

	public int getUeId() {
		return ueId;
	}

	public void setUeId(int ueId) {
		this.ueId = ueId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	public TypeUser getType() {
		return type;
	}
	
	public void setType(TypeUser type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
