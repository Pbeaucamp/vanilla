package bpm.architect.web.shared;

import java.util.Date;

import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HistoricLog implements IsSerializable {

	public enum HistoricType {
		CREATION, ADD_VERSION, DOWNLOAD_VIEW, CHANGE_CURRENT_VERSION, CHANGE_DATABASE;
	}
	
	private HistoricType type;
	private Date date;
	private User user;
	private String message;
	
	public HistoricLog() { }
	
	public HistoricLog(HistoricType type, Date date, User user) {
		this.type = type;
		this.date = date;
		this.user = user;
	}

	public HistoricType getType() {
		return type;
	}

	public void setType(HistoricType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
