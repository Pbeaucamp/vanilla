package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class WorkFlow implements Serializable{

	private static final long serialVersionUID = 1L;

	private int workFlowId=0;
	private String name="";
	private int docId=0;
	private String type="";
	private int userId=0;
	private boolean activate=false;
	private Date expirationDate= new Date();
	
	public int getWorkFlowId() {
		return workFlowId;
	}
	public void setWorkFlowId(int workFlowId) {
		this.workFlowId = workFlowId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public boolean isActivate() {
		return activate;
	}
	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
}
