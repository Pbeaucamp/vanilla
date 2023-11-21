package bpm.document.management.core.model;

import java.io.Serializable;

public class DetailedLevel implements Serializable{

	private static final long serialVersionUID = 1L;

	private int dlId=0;
	private int daId=0;
	private int levelNo=0;
	
	public int getDlId() {
		return dlId;
	}
	public void setDlId(int dlId) {
		this.dlId = dlId;
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
}
