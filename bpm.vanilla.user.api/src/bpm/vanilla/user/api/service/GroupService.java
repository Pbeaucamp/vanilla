package bpm.vanilla.user.api.service;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;

public class GroupService {
	private IVanillaAPI vanillaApi;
	private IVanillaSecurityManager vanillaSecurityManager;
	
	public GroupService(IVanillaAPI vanillaApi) {
		this.vanillaApi = vanillaApi;
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}
	
	public List<Group> getGroups() throws Exception {
		return vanillaSecurityManager.getGroups();
	}
	
	public void addUserToGroup(String userLogin, String groupName) throws Exception {
		User user = vanillaSecurityManager.getUserByLogin(userLogin);
		if (user == null) {
			throw new Exception("User not found.");
		}
		Group group = vanillaSecurityManager.getGroupByName(groupName);
		if (group == null) {
			throw new Exception("Group not found.");
		}
		UserGroup userGroup = new UserGroup();
		userGroup.setGroupId(group.getId());
		userGroup.setUserId(user.getId());
		vanillaSecurityManager.addUserGroup(userGroup);
	}
	
	public void addUserToGroup(int userId, int groupId) throws Exception {
		if (vanillaSecurityManager.getUserById(userId) == null) {
			throw new Exception("User not found.");
		}
		if (vanillaSecurityManager.getGroupById(groupId) == null) {
			throw new Exception("Group not found.");
		}
		UserGroup userGroup = new UserGroup();
		userGroup.setGroupId(groupId);
		userGroup.setUserId(userId);
		vanillaSecurityManager.addUserGroup(userGroup);
	}
	
}
