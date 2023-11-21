package bpm.vanilla.api.service.user;

import java.util.List;
import java.util.stream.Collectors;

import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class GroupService {
	private IVanillaAPI vanillaApi;
	private IVanillaSecurityManager vanillaSecurityManager;

	public GroupService(IVanillaAPI vanillaApi) {
		this.vanillaApi = vanillaApi;
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}

	public GroupService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		this.vanillaApi = vanillaApi;
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}

	public List<Group> getGroups() {
		try {
			return vanillaSecurityManager.getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUPS_NOT_FOUND);
		}
	}
	
	public Group getGroup(String name) {
		try {
			return vanillaSecurityManager.getGroupByName(name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
	}

	public String addUserToGroup(String userLogin, int groupId) {

		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}

		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}

		List<Group> userGroups = null;
		try {
			userGroups = vanillaSecurityManager.getGroups(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_GROUPS_NOT_FOUND);
		}

		// Pour une raison inconnue, userGroups.contains(group) ne marche pas ni
		// group.equals(userGroups.get(0)) (quand le groupe userGroups.get(0)
		// est dans le même que groupe)
		int currentGroupId = group.getId();
		List<Group> fGroups = userGroups.stream().filter(g -> g.getId() == currentGroupId).collect(Collectors.toList());
		// Vérification que le lien entre user et groupe n'existe pas déjà
		if (fGroups.size() > 0) {
			throw new VanillaApiException(VanillaApiError.USER_ALREADY_IN_GROUP);
		}

		UserGroup userGroup = new UserGroup();
		userGroup.setGroupId(group.getId());
		userGroup.setUserId(user.getId());
		try {
			vanillaSecurityManager.addUserGroup(userGroup);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_ADD_USERGROUP);
		}
		return (userLogin + " was successfully addded to group " + group.getName() + ".");
	}

	public String removeUserFromGroup(String userLogin, int groupId) {

		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}

		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}

		int userID = user.getId();
		List<UserGroup> userGroups;
		try {
			userGroups = vanillaSecurityManager.getUserGroups();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_GROUPS_NOT_FOUND);
		}
		List<UserGroup> fGroups = userGroups.stream().filter(g -> (g.getGroupId() == groupId && g.getUserId() == userID)).collect(Collectors.toList());
		// Vérification que le lien entre user et groupe existe
		if (fGroups.size() == 0) {
			throw new VanillaApiException(VanillaApiError.USER_NOT_IN_GROUP);
		}

		try {
			vanillaSecurityManager.delUserGroup(fGroups.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_REMOVE_USERGROUP);
		}

		return (userLogin + " was successfully removed from group " + group.getName() + ".");
	}

	public String addGroup(String groupName) {

		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupByName(groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (group != null) {
			throw new VanillaApiException(VanillaApiError.DUPLICATE_GROUP);
		}

		group = new Group();
		group.setName(groupName);

		try {
			vanillaSecurityManager.addGroup(group);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_ADD_GROUP);
		}

		// return group;
		return ("Group " + groupName + " was succressfully created.");
	}

	public String removeGroup(String groupID) {

		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(Integer.parseInt(groupID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}

		if (group.getName().toLowerCase().equals("system")) {
			throw new VanillaApiException(VanillaApiError.UNABLE_REMOVE_GROUP);
		}

		try {
			vanillaSecurityManager.delGroup(group);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_REMOVE_GROUP);
		}

		return ("Group " + group.getName() + " was succressfully deleted.");
	}
	
	public String addRoleToGroup(String roleName, int groupId) {
		
		Role role;
		try {
			role = vanillaSecurityManager.getRoleByName(roleName);
			if (role == null) {
				throw new VanillaApiException(VanillaApiError.ROLE_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ROLE_NOT_FOUND);
		}

		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupId);
			if (group == null) {
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}

		RoleGroup roleGroup = null;
		try {
			roleGroup = vanillaSecurityManager.getRoleGroups(role.getId(), groupId);
		} catch (Exception e) { }
		try {
			if (roleGroup == null) {
				roleGroup = new RoleGroup();
				roleGroup.setRoleId(role.getId());
				roleGroup.setGroupId(groupId);
	
				vanillaSecurityManager.addRoleGroup(roleGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_TO_ADD_ROLE);
		}

		return ("Role " + role.getName() + " was succressfully added to group " + group.getName() + ".");
	}

}
