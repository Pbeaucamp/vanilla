package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class Email implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String email;
	
	public Email() { }
	
	public Email(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public String toString() {
		return email;
	}
}
