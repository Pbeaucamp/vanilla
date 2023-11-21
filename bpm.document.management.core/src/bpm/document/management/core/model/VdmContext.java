package bpm.document.management.core.model;

import java.io.Serializable;

public class VdmContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mail="";
	private String password="";
	private String vdmUrl="";
	private int userId=0;

	public VdmContext() {
	}

	public VdmContext(String url, String mail, String password,int userId) {
		this.vdmUrl = url;
		this.mail = mail;
		this.password = password;
		this.userId = userId;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
