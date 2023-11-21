package bpm.document.management.core.model;

import java.io.Serializable;

public class DocEncryption implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private boolean encrypt=false;
	private String password="";
	
	public boolean isEncrypt() {
		return encrypt;
	}
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}