package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Distribution implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id = 0;

	private String name = "";

	private String description = "";

	private Date runDate = new Date();

	private int directoryId = 0;

	private boolean sendMail = false;

	private boolean documentAttachInMail = false;

	private boolean getNotificationByReading = false;

	private String periodicity = "";
	private Boolean deleted = false;

	public Distribution() {
		super();
	}

	public Distribution(int id, String name, String description, Date runDate, int directoryId, boolean sendMail, boolean documentAttachInMail, boolean getNotificationByReading, String periodicity) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.runDate = runDate;
		this.directoryId = directoryId;
		this.sendMail = sendMail;
		this.documentAttachInMail = documentAttachInMail;
		this.getNotificationByReading = getNotificationByReading;
		this.periodicity = periodicity;
	}

	public Distribution(String name, String description, Date runDate, int directoryId, boolean sendMail, boolean documentAttachInMail, boolean getNotificationByReading, String periodicity) {
		super();
		this.name = name;
		this.description = description;
		this.runDate = runDate;
		this.directoryId = directoryId;
		this.sendMail = sendMail;
		this.documentAttachInMail = documentAttachInMail;
		this.getNotificationByReading = getNotificationByReading;
		this.periodicity = periodicity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public boolean isDocumentAttachInMail() {
		return documentAttachInMail;
	}

	public void setDocumentAttachInMail(boolean documentAttachInMail) {
		this.documentAttachInMail = documentAttachInMail;
	}

	public boolean isGetNotificationByReading() {
		return getNotificationByReading;
	}

	public void setGetNotificationByReading(boolean getNotificationByReading) {
		this.getNotificationByReading = getNotificationByReading;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

}
