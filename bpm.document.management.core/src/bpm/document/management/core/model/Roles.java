package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Roles implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int roleId=0;
	private String roleName="";
	private String roleDescription="";
	private String roleType="";
	private String roleGrants="";
	private Date creationDate= new Date();
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoleGrants() {
		return roleGrants;
	}
	public void setRoleGrants(String roleGrants) {
		this.roleGrants = roleGrants;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	
}
