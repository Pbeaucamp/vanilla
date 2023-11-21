package bpm.united.olap.api.runtime.impl;

import bpm.united.olap.api.runtime.IRuntimeContext;

public class RuntimeContext implements IRuntimeContext{
	private String login;
	private String password;
	private String groupName;
	private int groupId;
	
//	public RuntimeContext(String login, String password, String groupName,	int groupId) {
//		super();
//		this.login = login;
//		this.password = password;
//		this.groupName = groupName;
//		this.groupId = groupId;
//	}
	public RuntimeContext(String login, String password, String groupName,	int groupId) {
		this.login = login;
		this.password = password;
		this.groupName = groupName;
		this.groupId = groupId;
	}
	
	
	


	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}
	
	
	
	
}
