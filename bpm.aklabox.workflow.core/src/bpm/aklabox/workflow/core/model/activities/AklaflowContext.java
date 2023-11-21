package bpm.aklabox.workflow.core.model.activities;

import java.io.Serializable;

public class AklaflowContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mail;
	private String password;
	private String aklaflowUrl;

	public AklaflowContext() {
	}

	public AklaflowContext(String aklaflowUrl, String mail, String password) {
		this.setAklaflowUrl(aklaflowUrl);
		this.mail = mail;
		this.password = password;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getMail() {
		return mail;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getAklaflowUrl() {
		return aklaflowUrl;
	}

	public void setAklaflowUrl(String aklaflowUrl) {
		this.aklaflowUrl = aklaflowUrl;
	}

}
