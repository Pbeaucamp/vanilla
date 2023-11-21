package bpm.document.management.core.model;

import java.io.Serializable;

public class Favorites implements Serializable{

	private static final long serialVersionUID = 1L;

	private int favId=0;
	private int docId=0;
	private String type;
	private String user="";
	
	public int getFavId() {
		return favId;
	}
	public void setFavId(int favId) {
		this.favId = favId;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
