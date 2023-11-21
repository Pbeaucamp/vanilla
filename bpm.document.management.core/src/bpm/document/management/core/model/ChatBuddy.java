package bpm.document.management.core.model;

import java.io.Serializable;

public class ChatBuddy implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id=0;
	private String email="";
	private String buddy="";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getBuddy() {
		return buddy;
	}
	public void setBuddy(String buddy) {
		this.buddy = buddy;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
