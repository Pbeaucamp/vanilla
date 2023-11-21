package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Announcements implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int announcementId=0;
	private String message="";
	private int adminId=0;
	private Date creationDate= new Date();
	
	public int getAnnouncementId() {
		return announcementId;
	}
	public void setAnnouncementId(int announcementId) {
		this.announcementId = announcementId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	
}
