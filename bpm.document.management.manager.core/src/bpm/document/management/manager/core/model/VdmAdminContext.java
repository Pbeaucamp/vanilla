package bpm.document.management.manager.core.model;

import java.io.Serializable;

public class VdmAdminContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mail;
	private String password;
	private String vdmUrl;

	public VdmAdminContext() {
	}
	
	public VdmAdminContext(String url, String mail, String password) {
		this.vdmUrl = url;
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

	public void setVdmUrl(String vdmUrl) {
		this.vdmUrl = vdmUrl;
	}

	public String getVdmUrl() {
		return vdmUrl;
	}

}
