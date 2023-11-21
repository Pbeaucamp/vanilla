package bpm.gwt.commons.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Email implements IsSerializable {

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
