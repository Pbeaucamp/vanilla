package bpm.document.management.core.model;

import java.io.Serializable;

public class DocSecurity implements Serializable{

	private static final long serialVersionUID = 1L;

	private int DS_Id=0;
	private int docId=0;
	private int userId=0;
	
	public int getDS_Id() {
		return DS_Id;
	}
	public void setDS_Id(int dS_Id) {
		DS_Id = dS_Id;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
