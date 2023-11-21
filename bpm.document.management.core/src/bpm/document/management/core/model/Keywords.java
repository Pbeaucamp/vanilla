package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Keywords implements Serializable{

	private static final long serialVersionUID = 1L;

	private int keywordId=0;
	private String keyword="";
	private int workFlowId=0;
	private String description="";
	private Date keywordDate= new Date();
	
	public int getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getWorkFlowId() {
		return workFlowId;
	}
	public void setWorkFlowId(int workFlowId) {
		this.workFlowId = workFlowId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getKeywordDate() {
		return keywordDate;
	}
	public void setKeywordDate(Date keywordDate) {
		this.keywordDate = keywordDate;
	}

}
