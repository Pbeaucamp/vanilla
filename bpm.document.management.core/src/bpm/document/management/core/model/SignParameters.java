package bpm.document.management.core.model;

import java.io.Serializable;

public class SignParameters implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String location;
	private String reason;
	private String contact;
	private boolean visible;
	
	public SignParameters() { }
	
	public SignParameters(String location, String reason, String contact, boolean visible) {
		this.location = location;
		this.reason = reason;
		this.contact = contact;
		this.visible = visible;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
