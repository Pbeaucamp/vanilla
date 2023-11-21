package bpm.vanillahub.web.shared;

import java.util.Date;

import bpm.vanillahub.core.Constants;

public class InfoCookie {

	private String login;
	private String password;
	
	private Date expires;
	
	public InfoCookie(String login, String password) {
		this.login = login;
		this.password = password;
		this.expires = new Date(System.currentTimeMillis() + Constants.DURATION);
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isValid() {
		return new Date().before(expires);
	}
}
