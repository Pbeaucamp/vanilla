package bpm.vanilla.user.api.service;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.User;

public class UserService {

	private IVanillaAPI vanillaApi;
	private IVanillaSecurityManager vanillaSecurityManager;
	
	public UserService(IVanillaAPI vanillaApi) {
		this.vanillaApi = vanillaApi;
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}
	
	public List<User> getUsers() throws Exception {
		return vanillaSecurityManager.getUsers(); 
	}
	
	public void addUser(String name,String login, String password) throws Exception {
		User user = new User();
		user.setName(name);
		user.setLogin(login);
		user.setPassword(password);
		vanillaSecurityManager.addUser(user);
	}
	
}
