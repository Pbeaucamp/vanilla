package bpm.document.management.core.model;

import java.io.Serializable;

public class Rate implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int rateId=0;
	private String user="";
	private int docId=0;
	private int stars=0;
	
	public int getRateId() {
		return rateId;
	}
	public void setRateId(int rateId) {
		this.rateId = rateId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	
}
