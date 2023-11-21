package bpm.gwt.commons.shared;

import java.util.Date;

import bpm.gwt.commons.shared.utils.CommonConstants;

public class InfoCookie {

	private String login;
	private String password;
	private int groupId;
	private int repositoryId;
	
	private Date expires;
	
	public InfoCookie(String login, String password, int groupId, int repositoryId) {
		this.login = login;
		this.password = password;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.expires = new Date(System.currentTimeMillis() + CommonConstants.DURATION);
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public boolean isValid() {
		return new Date().before(expires);
	}
}
