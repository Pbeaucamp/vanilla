package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class CampaignNotes implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int noteId=0;
	private int campaignLoaderId=0;
	private int campaignId=0;
	private String note="";
	private String email="";
	private Date creationDate=new Date();
	
	public int getNoteId() {
		return noteId;
	}
	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}
	public int getCampaignLoaderId() {
		return campaignLoaderId;
	}
	public void setCampaignLoaderId(int campaignLoaderId) {
		this.campaignLoaderId = campaignLoaderId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public int getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
