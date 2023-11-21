package bpm.vanilla.platform.core.runtime.tools;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
	
	private String user;
	private String pass;
	
	public MailAuthenticator(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, pass);
	}
	
}