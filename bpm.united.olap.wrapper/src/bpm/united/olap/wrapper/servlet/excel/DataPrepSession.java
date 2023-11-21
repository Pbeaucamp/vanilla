package bpm.united.olap.wrapper.servlet.excel;

import java.io.Serializable;

import bpm.vanilla.platform.core.IRepositoryApi;

public class DataPrepSession implements Serializable{

	private String user;
	private String password;
	private String url;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
