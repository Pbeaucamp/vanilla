package bpm.freemetrics.api.security.relations;

import java.util.Date;

public class FmUser_Roles {
	private int id;
	private Integer userId;
	private Integer roleId;
	private Date creationDate;
	private String comment;
	
	
	public FmUser_Roles() {
		super();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * @return the roleId
	 */
	public Integer getRoleId() {
		return roleId;
	}


	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	

}
