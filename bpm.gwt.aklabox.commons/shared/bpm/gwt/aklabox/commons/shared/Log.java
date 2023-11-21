package bpm.gwt.aklabox.commons.shared;

import java.io.Serializable;
import java.util.Date;

import bpm.aklabox.workflow.core.model.activities.Activity;

public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;
	private Date date;
	private String type;
	private Activity activity;

	public Log(String message, Date date, String type, Activity activity) {
		super();
		this.message = message;
		this.date = date;
		this.type = type;
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Log(String message, Date date, String type) {
		super();
		this.message = message;
		this.date = date;
		this.type = type;
	}

	public Log(String message, String type) {
		this.message = message;
		this.type = type;
	}

	public Log() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
