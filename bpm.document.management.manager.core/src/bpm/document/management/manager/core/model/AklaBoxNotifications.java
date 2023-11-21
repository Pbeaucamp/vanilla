package bpm.document.management.manager.core.model;

import java.io.Serializable;
import java.util.Date;

public class AklaBoxNotifications implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private String notificationType;
	private String message;
	private Date notificationDate;
	private boolean trigger;
	

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public Date getNotificationDate() {
		return notificationDate;
	}
	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}
	public boolean isTrigger() {
		return trigger;
	}
	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
