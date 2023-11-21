package bpm.vanilla.platform.core.repository;

import java.util.Date;


public class Revision {
	private int directoryItemId;
	private int revisionNumber;
	private int userId;
	private Date date;
//	private String comment;
	private int id;
	
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	public void setItem(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
//	public String getComment() {
//		return comment;
//	}
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
}
