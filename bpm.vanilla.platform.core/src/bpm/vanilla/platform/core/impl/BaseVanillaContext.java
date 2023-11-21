package bpm.vanilla.platform.core.impl;

import bpm.vanilla.platform.core.IVanillaContext;

public class BaseVanillaContext implements IVanillaContext{
	private String login;
	private String password;
	private String vanillaUrl;
	
	public BaseVanillaContext(String vanillaUrl, String login, String password) {
		this.login = login;
		this.password = password;
		this.vanillaUrl = vanillaUrl;
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
	 * @return the vanillaUrl
	 */
	public String getVanillaUrl() {
		return vanillaUrl;
	}
	
	
	
}
