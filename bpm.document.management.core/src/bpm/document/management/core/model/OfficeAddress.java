package bpm.document.management.core.model;

import java.io.Serializable;

public class OfficeAddress implements Serializable{

	private static final long serialVersionUID = 1L;

	private int oaId=0;
	private int daId=0;
	private int levelNo=0;
	private String officeNo="";
	
	public int getOaId() {
		return oaId;
	}
	public void setOaId(int oaId) {
		this.oaId = oaId;
	}
	public int getDaId() {
		return daId;
	}
	public void setDaId(int daId) {
		this.daId = daId;
	}
	public int getLevelNo() {
		return levelNo;
	}
	public void setLevelNo(int levelNo) {
		this.levelNo = levelNo;
	}
	public String getOfficeNo() {
		return officeNo;
	}
	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
}