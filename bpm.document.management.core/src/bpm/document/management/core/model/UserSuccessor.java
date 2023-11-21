package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserSuccessor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int userId;
	private int userSuccessorId;
	private User user;
	private User successor;
	
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

	public UserSuccessor(){
		
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getSuccessor() {
		return successor;
	}

	public void setSuccessor(User successor) {
		this.successor = successor;
	}

	public int getUserSuccessorId() {
		return userSuccessorId;
	}

	public void setUserSuccessorId(int userSuccessorId) {
		this.userSuccessorId = userSuccessorId;
	}
}
