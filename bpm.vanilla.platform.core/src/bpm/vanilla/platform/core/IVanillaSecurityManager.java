package bpm.vanilla.platform.core;

import java.util.List;

import javax.activation.DataHandler;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IVanillaSecurityManager {

	public static enum ActionType implements IXmlActionType{
		ADD_GROUP(Level.INFO), ADD_ROLE(Level.INFO), ADD_ROLE_GROUP(Level.INFO),ADD_USER(Level.INFO),ADD_USER_GROUP(Level.INFO), ADD_USER_IMG(Level.INFO), ADD_GROUP_IMG(Level.INFO),
		AUTHENTIFY(Level.INFO),
		DEL_GROUP(Level.INFO),DEL_ROLE(Level.INFO),DEL_ROLE_GROUP(Level.INFO), DEL_USER(Level.INFO),DEL_USER_GROUPS(Level.INFO),
		GET_CHILDS_GROUPS(Level.DEBUG), FIND_GROUP(Level.DEBUG), FIND_GROUP_BY_NAME(Level.DEBUG), LIST_GROUP(Level.DEBUG), LIST_GROUP_SZ(Level.DEBUG), LIST_GROUP_4USER(Level.DEBUG),
		NUMBER_GROUP(Level.DEBUG),
		FIND_ROLE(Level.DEBUG),FIND_ROLE_BY_NAME(Level.DEBUG),LIST_ROLE_GROUPS(Level.DEBUG), LIST_ROLE_GROUPS_4GROUP(Level.DEBUG),FIND_ROLE_GROUP(Level.DEBUG), LIST_ROLE_GROUPS_4ROLE(Level.DEBUG), 
		LIST_ROLES(Level.DEBUG), LIST_ROLES_4APP(Level.DEBUG),LIST_ROLES_4APP_ID(Level.DEBUG),LIST_ROLES_4GROUP(Level.DEBUG),
		FIND_USER(Level.DEBUG), FIND_USER_BY_NAME(Level.DEBUG),FIND_USER_GROUP(Level.DEBUG),LIST_USER_GROUPS(Level.DEBUG),LIST_USER_GROUPS4GROUP(Level.DEBUG),
		NUMBER_USERS(Level.DEBUG),LIST_USERS(Level.DEBUG), LIST_USERS_SZ(Level.DEBUG),LIST_USERS_4GROUP(Level.DEBUG),
		UPDATE_GROUP(Level.INFO),UPDATE_ROLE(Level.INFO), UPDATE_USER(Level.INFO), LIST_USER_GROUPS4USER(Level.DEBUG), FIND_IMG_4USER(Level.DEBUG), FIND_IMG_4GROUP(Level.DEBUG),
		FIND_GROUPPROJECTION(Level.DEBUG), ADD_GROUPPROJECTION(Level.INFO), DELETE_GROUPPROJECTION(Level.INFO), CAN_ACCESS_APP(Level.DEBUG), GET_USER_BY_LOGIN(Level.DEBUG), 
		GET_SETTINGS(Level.DEBUG), UPDATE_SETTINGS(Level.DEBUG), CHECK_PASSWORD_BACKUP_HASH(Level.DEBUG), GET_USER_BY_PASSWORD_BACKUP_HASH(Level.DEBUG), MANAGE_BACKUP_PASSWORD(Level.DEBUG), 
		REMOVE_BACKUP_PASSWORD(Level.DEBUG), GET_PENDING_PASSWORD_CHANGE(Level.DEBUG), CHECK_AUTO_LOGIN(Level.DEBUG), BUILD_AUTO_LOGIN(Level.DEBUG), CHECK_KEYCLOAK(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public int addGroup(Group group) throws Exception;
	
	/**
	 * delete a Group and all its references witin Vanilla
	 * (UserGroup, RoleGroups,AdressableSecurity)
	 * @param group
	 * @throws Exception
	 */
	public void delGroup(Group group) throws Exception;
	public void updateGroup(Group group) throws Exception;
	public Group getGroupById(int groupId) throws Exception;
	public Group getGroupByName(String name) throws Exception;
	public List<Group> getGroups() throws Exception;
	public List<Group> getGroups(int start, int end) throws Exception;
	public List<Group> getGroups(User user) throws Exception;
	public List<Group> getChilds(Group parent) throws Exception;
	public int getGroupsNumber() throws Exception;
	
	public void addRoleGroup(RoleGroup rg) throws Exception;
	public void delRoleGroup(RoleGroup rg) throws Exception;
	public List<RoleGroup> getRoleGroups() throws Exception;
	public RoleGroup getRoleGroups(int roleId, int groupId) throws Exception;
	public List<RoleGroup> getRoleGroups(Group group) throws Exception;
	public List<RoleGroup> getRoleGroups(Role role) throws Exception;
	
	public void addUserGroup(UserGroup ug) throws Exception;
	/**
	 * delete a User and all its references within Vanilla
	 * (UserGroup, UserRep)
	 * @param ug
	 * @throws Exception
	 */
	public void delUserGroup(UserGroup ug) throws Exception;
	public List<UserGroup> getUserGroups() throws Exception;
	public List<UserGroup> getUserGroups(Group group) throws Exception;
	public List<UserGroup> getUserGroups(User user) throws Exception;
	public UserGroup getUserGroup(int userId, int groupId) throws Exception;
	
	public int addUser(User user) throws Exception;
	public void delUser(User user) throws Exception;
	public void updateUser(User user) throws Exception;
	public User authentify(String ip, String login, String password, boolean isTimedOut) throws Exception;
	public User getUserById(int userId) throws Exception;
	public boolean checkPasswordBackupHash(String hash) throws Exception;
	public User getUserByPasswordBackupHash(String hash) throws Exception;
	
	/**
	 * @deprecated use getLogin(String login) instead. You can use it to get the user by name but why in the hell would you do that ???
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public User getUserByName(String name) throws Exception;
	public List<User> getUsers() throws Exception;
	public List<User> getUsers(int start, int end) throws Exception;
	public List<User> getUsersForGroup(Group group) throws Exception;
	public int getUserNumbers() throws Exception;
	public void addUserImage(int userId, DataHandler data, String format) throws Exception;
	public byte[] getImageForUserId(int userId) throws Exception;
	public String addGroupImage(DataHandler data, String format) throws Exception;
	public byte[] getImageForGroupId(int groupId) throws Exception;
	
	public int addRole(Role role) throws Exception;
	public void delRole(Role role) throws Exception;
	public List<Role> getRolesForAppId(int i , String type) throws Exception;
	public Role getRoleByName(String roleName) throws Exception;
	public Role getRoleById(int roleId) throws Exception;
	public List<Role> getRolesForGroup(Group group) throws Exception;
	public List<Role> getRoles() throws Exception;
	public List<Role> getRolesForApp(String appName) throws Exception;
	public void updateRole(Role role) throws Exception;
	
	public List<GroupProjection> getGroupProjections() throws Exception;
	public List<GroupProjection> getGroupProjectionsByFasdId(int fasdId) throws Exception;
	public int addGroupProjection(GroupProjection gp) throws Exception;
	public void deleteGroupProjection(GroupProjection gp) throws Exception;

	/**
	 * Check if the group can access the specified application.
	 * Groups with id < 0 can access without roles to the application
	 * 
	 * @param groupId
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public boolean canAccessApp(int groupId, String appName) throws Exception;
	
	public User getUserByLogin(String login) throws Exception;
	
	public Settings getSettings() throws Exception;
	public void updateSettings(Settings settings) throws Exception;

	public void managePasswordBackup(PasswordBackup backup) throws Exception;
	public void removePasswordBackup(PasswordBackup backup) throws Exception;
	public List<PasswordBackup> getPendingPasswordChangeDemands() throws Exception;

	public User checkAutoLogin(String keyAutoLogin) throws Exception;
	public String buildAutoLoginKey(User user, long expirationDelaiInMs) throws Exception;
	
	public User checkKeycloak(String token) throws Exception;
}
