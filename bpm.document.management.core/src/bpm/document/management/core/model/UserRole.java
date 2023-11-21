package bpm.document.management.core.model;

import java.io.Serializable;

public class UserRole implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private int userId;
	private String role;
	
	public UserRole() {
		// TODO Auto-generated constructor stub
	}

	
	
	public UserRole(int userId, String role) {
		super();
		this.userId = userId;
		this.role = role;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
	
}