package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class FileLocation implements Serializable{

	private static final long serialVersionUID = 1L;

	private int locationId=0;
	private int fileId=0;
	private String fileType="";
	private Date takenDate=new Date();
	private String takenReason="";
	private Date returnedDate=null;
	private String comment="";
	private String returnComment="";
	private String takerEmail="";
	private String loggerEmail="";

	private String country;
	private String city;
	private String state;
	private String cabinetName;
	private int daId=0;
	private int levelNo=0;
	private String officeNo="";
	
	private Documents doc=new Documents();
	private Tree folder= new Tree();
	
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Date getTakenDate() {
		return takenDate;
	}
	public void setTakenDate(Date takenDate) {
		this.takenDate = takenDate;
	}
	public String getTakenReason() {
		return takenReason;
	}
	public void setTakenReason(String takenReason) {
		this.takenReason = takenReason;
	}
	public Date getReturnedDate() {
		return returnedDate;
	}
	public void setReturnedDate(Date returnedDate) {
		this.returnedDate = returnedDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTakerEmail() {
		return takerEmail;
	}
	public void setTakerEmail(String takerEmail) {
		this.takerEmail = takerEmail;
	}
	public String getLoggerEmail() {
		return loggerEmail;
	}
	public void setLoggerEmail(String loggerEmail) {
		this.loggerEmail = loggerEmail;
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
	public Documents getDoc() {
		return doc;
	}
	public void setDoc(Documents doc) {
		this.doc = doc;
	}
	public Tree getFolder() {
		return folder;
	}
	public void setFolder(Tree folder) {
		this.folder = folder;
	}
	public String getOfficeNo() {
		if(officeNo==null)
			return "";
		else
			return officeNo;
	}
	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
	public String getReturnComment() {
		if(returnComment==null)
			return "";
		else
			return returnComment;
	}
	public void setReturnComment(String returnComment) {
		this.returnComment = returnComment;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCabinetName() {
		return cabinetName;
	}
	public void setCabinetName(String cabinetName) {
		this.cabinetName = cabinetName;
	}
	
}