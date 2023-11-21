package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Campaign implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int campaignId=0;
	private String subject="";
	private String comment="";
	private String campaignManager="";
	private Date creationDate=new Date();
	private Date expirationDate=new Date();
	private boolean activate=false;
	private Boolean deleted = false;
	
	
	public int getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public boolean isActivate() {
		return activate;
	}
	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getCampaignManager() {
		return campaignManager;
	}
	public void setCampaignManager(String campaignManager) {
		this.campaignManager = campaignManager;
	}
	public Boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	
	
}
