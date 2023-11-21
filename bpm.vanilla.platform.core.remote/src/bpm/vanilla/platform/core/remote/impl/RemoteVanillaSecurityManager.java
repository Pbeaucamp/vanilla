package bpm.vanilla.platform.core.remote.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaSecurityManager implements IVanillaSecurityManager{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	public RemoteVanillaSecurityManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public int addGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.ADD_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public int addRole(Role role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVanillaSecurityManager.ActionType.ADD_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public void addRoleGroup(RoleGroup rg) throws Exception {
		XmlAction op = new XmlAction(createArguments(rg), IVanillaSecurityManager.ActionType.ADD_ROLE_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public int addUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVanillaSecurityManager.ActionType.ADD_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
		User u = getUserByLogin(user.getLogin());
		if (u == null){
			throw new Exception("Failed to create User");
		}
		return u.getId();
		
	}

	@Override
	public void addUserGroup(UserGroup ug) throws Exception {
		XmlAction op = new XmlAction(createArguments(ug), IVanillaSecurityManager.ActionType.ADD_USER_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public User authentify(String ip, String login, String password, boolean isTimedOut) throws Exception {
		if(ip != null && !ip.isEmpty()) {
			httpCommunicator.setIp(ip);
		}
		
		String pass = password;
		//It's done server side now (to connect with ldap)
//		if (!password.matches("[0-9a-f]{32}")){
//			pass = HttpCommunicator.md5encode(password);
//		}
		
		XmlAction op = new XmlAction(createArguments(ip, login, pass, false), IVanillaSecurityManager.ActionType.AUTHENTIFY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false, isTimedOut);
		return (User)xstream.fromXML(xml);
	}

	@Override
	public void delGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.DEL_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void delRole(Role role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVanillaSecurityManager.ActionType.DEL_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void delRoleGroup(RoleGroup rg) throws Exception {
		XmlAction op = new XmlAction(createArguments(rg), IVanillaSecurityManager.ActionType.DEL_ROLE_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void delUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVanillaSecurityManager.ActionType.DEL_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void delUserGroup(UserGroup ug) throws Exception {
		XmlAction op = new XmlAction(createArguments(ug), IVanillaSecurityManager.ActionType.DEL_USER_GROUPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public List<Group> getChilds(Group parent) throws Exception {
		XmlAction op = new XmlAction(createArguments(parent), IVanillaSecurityManager.ActionType.GET_CHILDS_GROUPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public Group getGroupById(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IVanillaSecurityManager.ActionType.FIND_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Group)xstream.fromXML(xml);
	}

	@Override
	public Group getGroupByName(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), IVanillaSecurityManager.ActionType.FIND_GROUP_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if(xml != null && !xml.isEmpty()) {
			return (Group)xstream.fromXML(xml);
		}
		return null;
	}

	@Override
	public List<Group> getGroups() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.LIST_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Group> getGroups(int start, int end) throws Exception {
		XmlAction op = new XmlAction(createArguments(start, end), IVanillaSecurityManager.ActionType.LIST_GROUP_SZ);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Group> getGroups(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVanillaSecurityManager.ActionType.LIST_GROUP_4USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public int getGroupsNumber() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.NUMBER_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public Role getRoleById(int roleId) throws Exception {
		XmlAction op = new XmlAction(createArguments(roleId), IVanillaSecurityManager.ActionType.FIND_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Role)xstream.fromXML(xml);
	}

	@Override
	public Role getRoleByName(String roleName) throws Exception {
		XmlAction op = new XmlAction(createArguments(roleName), IVanillaSecurityManager.ActionType.FIND_ROLE_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Role)xstream.fromXML(xml);
	}

	@Override
	public List<RoleGroup> getRoleGroups() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.LIST_ROLE_GROUPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public RoleGroup getRoleGroups(int roleId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(roleId, groupId), IVanillaSecurityManager.ActionType.FIND_ROLE_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (RoleGroup)xstream.fromXML(xml);
	}

	@Override
	public List<RoleGroup> getRoleGroups(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.LIST_ROLE_GROUPS_4GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<RoleGroup> getRoleGroups(Role role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVanillaSecurityManager.ActionType.LIST_ROLE_GROUPS_4ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Role> getRoles() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.LIST_ROLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Role> getRolesForApp(String appName) throws Exception {
		XmlAction op = new XmlAction(createArguments(appName), IVanillaSecurityManager.ActionType.LIST_ROLES_4APP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Role> getRolesForAppId(int i, String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(i, type), IVanillaSecurityManager.ActionType.LIST_ROLES_4APP_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Role> getRolesForGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.LIST_ROLES_4GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public User getUserById(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaSecurityManager.ActionType.FIND_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (xml != null && !xml.isEmpty()) {
			return (User)xstream.fromXML(xml);
		}
		return null;
	}

	@Override
	public User getUserByName(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), IVanillaSecurityManager.ActionType.FIND_USER_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (User)xstream.fromXML(xml);
	}

	@Override
	public UserGroup getUserGroup(int userId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, groupId), IVanillaSecurityManager.ActionType.FIND_USER_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (UserGroup)xstream.fromXML(xml);
	}

	@Override
	public List<UserGroup> getUserGroups() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.LIST_USER_GROUPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<UserGroup> getUserGroups(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.LIST_USERS_4GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<UserGroup> getUserGroups(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVanillaSecurityManager.ActionType.LIST_USER_GROUPS4USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public int getUserNumbers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.NUMBER_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public List<User> getUsers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.LIST_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<User> getUsers(int start, int end) throws Exception {
		XmlAction op = new XmlAction(createArguments(start, end), IVanillaSecurityManager.ActionType.LIST_USERS_SZ);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<User> getUsersForGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.LIST_USERS_4GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public void updateGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVanillaSecurityManager.ActionType.UPDATE_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void updateRole(Role role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVanillaSecurityManager.ActionType.UPDATE_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void updateUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVanillaSecurityManager.ActionType.UPDATE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public int addGroupProjection(GroupProjection gp) throws Exception {
		XmlAction op = new XmlAction(createArguments(gp), IVanillaSecurityManager.ActionType.ADD_GROUPPROJECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void deleteGroupProjection(GroupProjection gp) throws Exception {
		XmlAction op = new XmlAction(createArguments(gp), IVanillaSecurityManager.ActionType.DELETE_GROUPPROJECTION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<GroupProjection> getGroupProjections() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.FIND_GROUPPROJECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GroupProjection>) xstream.fromXML(xml);
	}

	@Override
	public List<GroupProjection> getGroupProjectionsByFasdId(int fasdId) throws Exception {
		XmlAction op = new XmlAction(createArguments(fasdId), IVanillaSecurityManager.ActionType.FIND_GROUPPROJECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GroupProjection>) xstream.fromXML(xml);
	}

	@Override
	public void addUserImage(int userId, DataHandler datas, String format) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		byte[] raws = null;
		
		if (datas != null){
			try{
				IOWriter.write(datas.getInputStream(), stream, true, true);
				raws = stream.toByteArray();
				raws = Base64.encodeBase64(raws);
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Unable to convert picture DataHandler into base64 raw byte array", ex);
			}	
		}
		
		XmlAction op = new XmlAction(createArguments(userId, raws, format), IVanillaSecurityManager.ActionType.ADD_USER_IMG);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public byte[] getImageForUserId(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaSecurityManager.ActionType.FIND_IMG_4USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (byte[])xstream.fromXML(xml);
	}



	@Override
	public String addGroupImage(DataHandler data, String format) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		byte[] raws = null;
		
		if (data != null){
			try{
				IOWriter.write(data.getInputStream(), stream, true, true);
				raws = stream.toByteArray();
				raws = Base64.encodeBase64(raws);
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Unable to convert picture DataHandler into base64 raw byte array", ex);
			}	
		}
		
		XmlAction op = new XmlAction(createArguments(raws, format), IVanillaSecurityManager.ActionType.ADD_GROUP_IMG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}



	@Override
	public byte[] getImageForGroupId(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IVanillaSecurityManager.ActionType.FIND_IMG_4GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (byte[])xstream.fromXML(xml);
	}



	@Override
	public boolean canAccessApp(int groupId, String appName) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, appName), IVanillaSecurityManager.ActionType.CAN_ACCESS_APP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}



	@Override
	public User getUserByLogin(String login) throws Exception {
		XmlAction op = new XmlAction(createArguments(login), IVanillaSecurityManager.ActionType.GET_USER_BY_LOGIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (User)xstream.fromXML(xml);
	}

	@Override
	public Settings getSettings() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.GET_SETTINGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Settings) xstream.fromXML(xml);
	}

	@Override
	public void updateSettings(Settings settings) throws Exception {
		XmlAction op = new XmlAction(createArguments(settings), IVanillaSecurityManager.ActionType.UPDATE_SETTINGS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public boolean checkPasswordBackupHash(String hash) throws Exception {
		XmlAction op = new XmlAction(createArguments(hash), IVanillaSecurityManager.ActionType.CHECK_PASSWORD_BACKUP_HASH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public User getUserByPasswordBackupHash(String hash) throws Exception {
		XmlAction op = new XmlAction(createArguments(hash), IVanillaSecurityManager.ActionType.GET_USER_BY_PASSWORD_BACKUP_HASH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (User) xstream.fromXML(xml);
	}

	@Override
	public void managePasswordBackup(PasswordBackup backup) throws Exception {
		XmlAction op = new XmlAction(createArguments(backup), IVanillaSecurityManager.ActionType.MANAGE_BACKUP_PASSWORD);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void removePasswordBackup(PasswordBackup backup) throws Exception {
		XmlAction op = new XmlAction(createArguments(backup), IVanillaSecurityManager.ActionType.REMOVE_BACKUP_PASSWORD);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PasswordBackup> getPendingPasswordChangeDemands() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSecurityManager.ActionType.GET_PENDING_PASSWORD_CHANGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<PasswordBackup>) xstream.fromXML(xml);
	}



	@Override
	public User checkAutoLogin(String keyAutoLogin) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyAutoLogin), IVanillaSecurityManager.ActionType.CHECK_AUTO_LOGIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (User) xstream.fromXML(xml);
	}



	@Override
	public String buildAutoLoginKey(User user, long expirationDelaiInMs) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, expirationDelaiInMs), IVanillaSecurityManager.ActionType.BUILD_AUTO_LOGIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) xstream.fromXML(xml);
	}



	@Override
	public User checkKeycloak(String token) throws Exception {
		XmlAction op = new XmlAction(createArguments(token), IVanillaSecurityManager.ActionType.CHECK_KEYCLOAK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (User) xstream.fromXML(xml);
	}
}
