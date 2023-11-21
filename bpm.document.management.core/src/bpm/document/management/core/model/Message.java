package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int MessageId=0;
	private String subject="";
	private String sender="";
	private String message="";
	private String receiver="";
	private Date date=new Date();
	private String status="";
	private String owner="";
	private String attachments="";
	private String whoFirst="";
	private boolean copyRequest=false;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getMessageId() {
		return MessageId;
	}
	public void setMessageId(int messageId) {
		this.MessageId = messageId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getAttachments() {
		return attachments;
	}
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
	public String getWhoFirst() {
		return whoFirst;
	}
	public void setWhoFirst(String whoFirst) {
		this.whoFirst = whoFirst;
	}
	public boolean isCopyRequest() {
		return copyRequest;
	}
	public void setCopyRequest(boolean copyRequest) {
		this.copyRequest = copyRequest;
	}
}
