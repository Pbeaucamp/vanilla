package bpm.forms.runtime;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class VanillaContext implements IVanillaContext{
	private String login;
	private String password;
	private int groupId;
//	private String vanillaServerUrl;
	
	private String vanillaUrl;
	
	transient private IVanillaAPI vanillaApi;
	
	public VanillaContext(String vanillaUrl, String login, String password, String groupId) throws Exception{
		
		this.vanillaUrl = vanillaUrl;
		this.login = login;
		this.password = password;
		
		vanillaApi = new RemoteVanillaPlatform(vanillaUrl, login, password);
		
		try{
			this.groupId = Integer.parseInt(groupId);
		}catch(Exception ex){
			this.groupId = vanillaApi.getVanillaSecurityManager().getGroupByName(groupId).getId();
		}
	}
	
	public IVanillaAPI getVanillaApi(){
		return vanillaApi;
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
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	@Override
	public String getVanillaUrl() {
		return vanillaUrl;
	}

	
}
