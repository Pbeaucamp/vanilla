package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int chatMessageId=0;
	private String sender="";
	private String message="";
	private String receiver="";
	private Date messageDate=new Date();
	private boolean messageRead=false;
	private String chatMessageOwner="";
	
	public int getChatMessageId() {
		return chatMessageId;
	}
	public void setChatMessageId(int chatMessageId) {
		this.chatMessageId = chatMessageId;
	}
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
	public Date getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	public boolean isMessageRead() {
		return messageRead;
	}
	public void setMessageRead(boolean messageRead) {
		this.messageRead = messageRead;
	}
	public String getChatMessageOwner() {
		return chatMessageOwner;
	}
	public void setChatMessageOwner(String chatMessageOwner) {
		this.chatMessageOwner = chatMessageOwner;
	}
	
	
}
