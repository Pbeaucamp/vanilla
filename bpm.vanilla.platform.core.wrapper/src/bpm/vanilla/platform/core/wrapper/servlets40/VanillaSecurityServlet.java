package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.VanillaSecurityManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaSecurityServlet extends AbstractComponentServlet {

	public VanillaSecurityServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger) {
		this.logger = logger;
		this.component = componentProvider;
	}

	@Override
	public void init() throws ServletException {
		logger.info("Initializing ExternalAccessibilityServlet...");
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IVanillaSecurityManager.ActionType)) {
				throw new Exception("ActionType not a IRepositoryManager");
			}

			IVanillaSecurityManager.ActionType type = (IVanillaSecurityManager.ActionType) action.getActionType();
			if(!(type == IVanillaSecurityManager.ActionType.FIND_GROUP)) {
				log(type, ((VanillaSecurityManager)component.getSecurityManager()).getComponentName(), req);
			}
			
			try {
				switch (type) {
				case ADD_GROUP:
					actionResult = addGroup(args);
					break;
				case ADD_ROLE:
					actionResult = addRole(args);
					break;
				case ADD_ROLE_GROUP:
					addRoleGroup(args);
					break;
				case ADD_USER:
					addUser(args);
					break;
				case ADD_USER_GROUP:
					addUserGroup(args);
					break;
				case ADD_USER_IMG:
					addUserImg(args);
					break;
				case ADD_GROUP_IMG:
					actionResult = addGroupImg(args);
					break;
				case AUTHENTIFY:
					String ipAddress = (String) args.getArguments().get(0);
			        if (ipAddress == null || ipAddress.isEmpty()) {
				        ipAddress = req.getHeader("x-forwarded-for");
				        if (ipAddress == null) {
				            ipAddress = req.getHeader("X_FORWARDED_FOR");
				            if (ipAddress == null){
				                ipAddress = req.getRemoteAddr();
				            }
				        }
			        }
					actionResult =  authentify(ipAddress, args);
					break;
				case DEL_GROUP:
					delGroup(args);
					break;
				case DEL_ROLE:
					delRole(args);
					break;
				case DEL_ROLE_GROUP:
					delRoleGroup(args);
					break;
				case DEL_USER:
					delUser(args);
					break;
				case DEL_USER_GROUPS:
					delUserGroup(args);
					break;
				case FIND_GROUP:
					actionResult = findGroup(args);
					break;
				case FIND_GROUP_BY_NAME:
					actionResult = findGroupByName(args);
					break;
				case FIND_ROLE:
					actionResult = findRole(args);
					break;
				case FIND_ROLE_BY_NAME:
					actionResult = findRoleByName(args);
					break;
				case FIND_ROLE_GROUP:
					actionResult = findRoleGroup(args);
					break;
				case FIND_USER:
					actionResult = findUser(args);
					break;
				case FIND_USER_BY_NAME:
					actionResult = findUserByName(args);
					break;
				case GET_USER_BY_LOGIN:
					actionResult = findUserByLogin(args);
					break;
				case FIND_USER_GROUP:
					actionResult = findUserGroup(args);
					break;
				case FIND_IMG_4USER:
					actionResult = findImgByUserId(args);
					break;
				case FIND_IMG_4GROUP:
					actionResult = findImgByGroupId(args);
					break;
				case GET_CHILDS_GROUPS:
					actionResult = getGroupChilds(args);
					break;
				case LIST_GROUP:
					actionResult = listGroups(args);
					break;
				case LIST_GROUP_4USER:
					actionResult = listGroups4User(args);
					break;
				case LIST_GROUP_SZ:
					actionResult = listGroupSz(args);
					break;
				case LIST_ROLE_GROUPS:
					actionResult = listRoleGroups(args);
					break;
				case LIST_ROLE_GROUPS_4GROUP:
					actionResult = listRoleGroups4Group(args);
					break;
				case LIST_ROLE_GROUPS_4ROLE:
					actionResult = listRoleGroups4Role(args);
					break;
				case LIST_ROLES:
					actionResult = listRoles(args);
					break;
				case LIST_ROLES_4APP:
					actionResult = listRoles4App(args);
					break;
				case LIST_ROLES_4APP_ID:
					actionResult = listRoles4AppId(args);
					break;
				case LIST_ROLES_4GROUP:
					actionResult = listRoles4Group(args);
					break;
				case CAN_ACCESS_APP:
					actionResult = canAccessApp(args);
					break;
			
				case LIST_USER_GROUPS4USER:
					actionResult = listUserGroups4User(args);
					break;
				case LIST_USER_GROUPS:
					actionResult = listUserGroups(args);
					break;
				case LIST_USER_GROUPS4GROUP:
					actionResult = listUserGroups4Group(args);
					break;
				case LIST_USERS:
					actionResult = listUsers(args);
					break;
				case LIST_USERS_4GROUP:
					actionResult = listUsers4Group(args);
					break;
				case LIST_USERS_SZ:
					actionResult = listUsersSz(args);
					break;
				case NUMBER_GROUP:
					actionResult = getGroupNumber(args);
					break;
				case NUMBER_USERS:
					actionResult = getUserNumber(args);
					break;
				case UPDATE_GROUP:
					updateGroup(args);
					break;
				case UPDATE_ROLE:
					updateRole(args);
					break;
				case UPDATE_USER:
					updateUser(args);
					break;
				case FIND_GROUPPROJECTION:
					actionResult = findGroupProjection(args);
					break;
				case ADD_GROUPPROJECTION:
					actionResult = addGroupProjection(args);
					break;
				case DELETE_GROUPPROJECTION:
					deleteGroupProjection(args);
					break;
				case GET_SETTINGS:
					actionResult = getSettings(args);
					break;
				case UPDATE_SETTINGS:
					updateSettings(args);
					break;
				case CHECK_PASSWORD_BACKUP_HASH:
					actionResult = checkPasswordBackupHash(args);
					break;
				case GET_USER_BY_PASSWORD_BACKUP_HASH:
					actionResult = getUserByPasswordBackupHash(args);
					break;
				case MANAGE_BACKUP_PASSWORD:
					managePasswordBackup(args);
					break;
				case REMOVE_BACKUP_PASSWORD:
					removePasswordBackup(args);
					break;
				case GET_PENDING_PASSWORD_CHANGE:
					actionResult = getPendingPasswordChangeDemands(args);
					break;
				case CHECK_AUTO_LOGIN:
					actionResult = checkAutoLogin(args);
					break;
				case BUILD_AUTO_LOGIN:
					actionResult = buildAutoLoginKey(args);
					break;
				case CHECK_KEYCLOAK:
					actionResult = checkKeycloak(args);
					break;
				}
			} catch (Exception ex) {
				logger.error(ex);
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception ex) {
			if(!ex.getMessage().contains("FIND_IMG_4USER")) {
				logger.error(ex.getMessage(), ex);

				resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
				resp.getWriter().close();
			}

		}
	}

	private Object findUserByLogin(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserByLogin", String.class), args);
		return component.getSecurityManager().getUserByLogin((String)args.getArguments().get(0));
	}

	private Object canAccessApp(XmlArgumentsHolder args) throws Exception {
		return component.getSecurityManager().canAccessApp((Integer)args.getArguments().get(0), (String)args.getArguments().get(1));
	}

	private void deleteGroupProjection(XmlArgumentsHolder args) throws Exception {
		component.getSecurityManager().deleteGroupProjection((GroupProjection) args.getArguments().get(0));
	}

	private int addGroupProjection(XmlArgumentsHolder args) throws Exception {
		return component.getSecurityManager().addGroupProjection((GroupProjection) args.getArguments().get(0));
	}

	private Object findGroupProjection(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments().size() > 0) {
			return component.getSecurityManager().getGroupProjectionsByFasdId((Integer) args.getArguments().get(0));
		}
		else {
			return component.getSecurityManager().getGroupProjections();
		}
	}

	private void updateUser(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("updateUser", User.class), args);
		component.getSecurityManager().updateUser((User)args.getArguments().get(0));

	}

	private void updateRole(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("updateRole", Role.class), args);
		component.getSecurityManager().updateRole((Role)args.getArguments().get(0));
	}

	private void updateGroup(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("updateGroup", Group.class), args);
		component.getSecurityManager().updateGroup((Group)args.getArguments().get(0));
	}

	private Object getUserNumber(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getUserNumbers();
	}

	private Object getGroupNumber(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getGroupsNumber();
	}

	private Object listUsersSz(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUsers", int.class, int.class), args);
		return component.getSecurityManager().getUsers((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));

	}

	private Object listUsers4Group(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUsersForGroup", Group.class), args);
		return component.getSecurityManager().getUsersForGroup((Group)args.getArguments().get(0));

	}

	private Object listUsers(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getUsers();
	}

	private Object listUserGroups4Group(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserGroups", Group.class), args);
		return component.getSecurityManager().getUserGroups((Group)args.getArguments().get(0));

	}
	private Object listUserGroups4User(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserGroups", User.class), args);
		return component.getSecurityManager().getUserGroups((User)args.getArguments().get(0));

	}


	private Object listUserGroups(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getUserGroups();

	}

//	private Object listRoles4User(XmlArgumentsHolder args)  throws Exception{
//		argChecker.checkArguments(IRepositoryManager.class.getMethod("getUserGroups", Group.class), args);
//		return component.getSecurityManager().getRoles((Group)args.getArguments().get(0));
//
//	}

	private Object listRoles4Group(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRolesForGroup", Group.class), args);
		return component.getSecurityManager().getRolesForGroup((Group)args.getArguments().get(0));

	}

	private Object listRoles4AppId(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRolesForAppId", int.class, String.class), args);
		return component.getSecurityManager().getRolesForAppId((Integer)args.getArguments().get(0), (String)args.getArguments().get(1));

	}

	private Object listRoles4App(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRolesForApp", String.class), args);
		return component.getSecurityManager().getRolesForApp((String)args.getArguments().get(0));

	}

	private Object listRoles(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getRoles();

	}

	private Object listRoleGroups4Role(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRoleGroups", Role.class), args);
		return component.getSecurityManager().getRoleGroups((Role)args.getArguments().get(0));

	}

	private Object listRoleGroups4Group(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRoleGroups", Group.class), args);
		return component.getSecurityManager().getRoleGroups((Group)args.getArguments().get(0));
	}

	private Object listRoleGroups(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getRoleGroups();

	}

	private Object listGroupSz(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getGroups", int.class, int.class), args);
		return component.getSecurityManager().getGroups((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));

	}

	private Object listGroups4User(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getGroups", User.class), args);
		return component.getSecurityManager().getGroups((User)args.getArguments().get(0));

	}

	private Object listGroups(XmlArgumentsHolder args)  throws Exception{
		return component.getSecurityManager().getGroups();
	}

	private Object getGroupChilds(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getChilds", Group.class), args);
		return component.getSecurityManager().getChilds((Group)args.getArguments().get(0));

	}

	private Object findUserGroup(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserGroup", int.class, int.class), args);
		return component.getSecurityManager().getUserGroup((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
	}

	private Object findUserByName(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserByName", String.class), args);
		return component.getSecurityManager().getUserByName((String)args.getArguments().get(0));
	}

	private Object findUser(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserById", int.class), args);
		return component.getSecurityManager().getUserById((Integer)args.getArguments().get(0));

	}

	private Object findRoleGroup(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRoleGroups", int.class, int.class), args);
		return component.getSecurityManager().getRoleGroups((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));

	}

	private Object findRoleByName(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRoleByName", String.class), args);
		return component.getSecurityManager().getRoleByName((String)args.getArguments().get(0));

	}

	private Object findRole(XmlArgumentsHolder args)  throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getRoleById", int.class), args);
		return component.getSecurityManager().getRoleById((Integer)args.getArguments().get(0));

	}

	private Object findGroupByName(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getGroupByName", String.class), args);
		return component.getSecurityManager().getGroupByName((String)args.getArguments().get(0));

	}

	private Object findGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getGroupById", int.class), args);
		return component.getSecurityManager().getGroupById((Integer)args.getArguments().get(0));

	}

	private void delUserGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("delUserGroup", UserGroup.class), args);
		component.getSecurityManager().delUserGroup((UserGroup)args.getArguments().get(0));

	}

	private void delUser(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("delUser", User.class), args);
		component.getSecurityManager().delUser((User)args.getArguments().get(0));
	}

	private void delRoleGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("delRoleGroup", RoleGroup.class), args);
		component.getSecurityManager().delRoleGroup((RoleGroup)args.getArguments().get(0));
	}

	private void delRole(XmlArgumentsHolder args)throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("delRole", Role.class), args);
		component.getSecurityManager().delRole((Role)args.getArguments().get(0));
	}

	private void delGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("delGroup", Group.class), args);
		component.getSecurityManager().delGroup((Group)args.getArguments().get(0));
	}

	private Object authentify(String ip, XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("authentify", String.class, String.class, String.class, boolean.class), args);
		return component.getSecurityManager().authentify(ip, (String)args.getArguments().get(1), (String)args.getArguments().get(2), false);

	}

	private void addUserGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("addUserGroup", UserGroup.class), args);
		component.getSecurityManager().addUserGroup((UserGroup)args.getArguments().get(0));

	}

	private void addUser(XmlArgumentsHolder args)throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("addUser", User.class), args);
		component.getSecurityManager().addUser((User)args.getArguments().get(0));

	}

	private void addRoleGroup(XmlArgumentsHolder args)throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("addRoleGroup", RoleGroup.class), args);
		component.getSecurityManager().addRoleGroup((RoleGroup)args.getArguments().get(0));

	}

	private Object addRole(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("addRole", Role.class), args);
		return component.getSecurityManager().addRole((Role)args.getArguments().get(0));

	}

	private Object addGroup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("addGroup", Group.class), args);
		return component.getSecurityManager().addGroup((Group)args.getArguments().get(0));
	}

	private void addUserImg(XmlArgumentsHolder args) throws Exception {
		DataHandler h = null;
		try{
			byte[] raw = (byte[])args.getArguments().get(1);
			
			raw = Base64.decodeBase64(raw);
			ByteArrayInputStream bis = new ByteArrayInputStream(raw);
			String mimeType = URLConnection.guessContentTypeFromStream(bis);
			h = new DataHandler(new PreferencesServlet._ByteArrayDataSource(mimeType, bis, "imageUser.png"));
			
		}catch(Exception ex){
			String m = "Failed to read incoming picture raw bytes ";
			Logger.getLogger(getClass()).error(m + ex.getMessage(), ex);
			throw new Exception(m + ex.getMessage(), ex);
		}
		component.getSecurityManager().addUserImage((Integer)args.getArguments().get(0), h, (String)args.getArguments().get(2));
	}

	private Object findImgByUserId(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getImageForUserId", int.class), args);
		return component.getSecurityManager().getImageForUserId((Integer)args.getArguments().get(0));
	}
	
	private Object addGroupImg(XmlArgumentsHolder args) throws Exception {
		DataHandler h = null;
		try{
			byte[] raw = (byte[])args.getArguments().get(0);
			
			raw = Base64.decodeBase64(raw);
			ByteArrayInputStream bis = new ByteArrayInputStream(raw);
			String mimeType = URLConnection.guessContentTypeFromStream(bis);
			h = new DataHandler(new PreferencesServlet._ByteArrayDataSource(mimeType, bis, "imageGroup.png"));
			
		}catch(Exception ex){
			String m = "Failed to read incoming picture raw bytes ";
			Logger.getLogger(getClass()).error(m + ex.getMessage(), ex);
			throw new Exception(m + ex.getMessage(), ex);
		}
		return component.getSecurityManager().addGroupImage(h, (String)args.getArguments().get(1));
	}

	private Object findImgByGroupId(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getImageForGroupId", int.class), args);
		return component.getSecurityManager().getImageForGroupId((Integer)args.getArguments().get(0));
	}

	private void updateSettings(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("updateSettings", Settings.class), args);
		component.getSecurityManager().updateSettings((Settings) args.getArguments().get(0));
	}

	private Object getSettings(XmlArgumentsHolder args) throws Exception {
		return component.getSecurityManager().getSettings();
	}

	private Object getUserByPasswordBackupHash(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("getUserByPasswordBackupHash", String.class), args);
		return component.getSecurityManager().getUserByPasswordBackupHash((String) args.getArguments().get(0));
	}

	private Object checkPasswordBackupHash(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("checkPasswordBackupHash", String.class), args);
		return component.getSecurityManager().checkPasswordBackupHash((String) args.getArguments().get(0));
	}

	private void managePasswordBackup(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("managePasswordBackup", PasswordBackup.class), args);
		component.getSecurityManager().managePasswordBackup((PasswordBackup) args.getArguments().get(0));
	}

	private void removePasswordBackup(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("removePasswordBackup", PasswordBackup.class), args);
		component.getSecurityManager().removePasswordBackup((PasswordBackup) args.getArguments().get(0));
	}

	private Object getPendingPasswordChangeDemands(XmlArgumentsHolder args) throws Exception {
		return component.getSecurityManager().getPendingPasswordChangeDemands();
	}

	private User checkAutoLogin(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("checkAutoLogin", String.class), args);
		return component.getSecurityManager().checkAutoLogin((String) args.getArguments().get(0));
	}

	private String buildAutoLoginKey(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("buildAutoLoginKey", User.class, Long.class), args);
		return component.getSecurityManager().buildAutoLoginKey((User) args.getArguments().get(0), (Long) args.getArguments().get(1));
	}

	private User checkKeycloak(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSecurityManager.class.getMethod("checkKeycloak", String.class), args);
		return component.getSecurityManager().checkKeycloak((String) args.getArguments().get(0));
	}
}
