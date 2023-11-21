package bpm.document.management.core.model;

import java.io.Serializable;

public class UserGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int ugId=0;
	private int userId=0;
	private int groupId=0;
	
	public int getUgId() {
		return ugId;
	}
	public void setUgId(int ugId) {
		this.ugId = ugId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}
