package bpm.vanilla.platform.core.runtime.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.AutoLogin;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.LimitNumberConnectionException;
import bpm.vanilla.platform.core.exceptions.PasswordException;
import bpm.vanilla.platform.core.exceptions.UserNotFoundException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.runtime.dao.ged.SecurityDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.UolapQueriesLoggerDao;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.FmdtUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.PublicUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.security.GroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.GroupProjectionDAO;
import bpm.vanilla.platform.core.runtime.dao.security.RoleDAO;
import bpm.vanilla.platform.core.runtime.dao.security.RoleGroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.SecurityLogDAO;
import bpm.vanilla.platform.core.runtime.dao.security.SettingsDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserGroupDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserRepDAO;
import bpm.vanilla.platform.core.runtime.dao.unitedolap.UnitedOlapPreloadDAO;
import bpm.vanilla.platform.core.runtime.tools.Connection;
import bpm.vanilla.platform.core.runtime.tools.ConnectionThreadManager;
import bpm.vanilla.platform.core.runtime.tools.Security;

public class VanillaSecurityManager extends AbstractVanillaManager implements IVanillaSecurityManager {

	private UserDAO userDao;
	private UserGroupDAO userGroupDao;
	private GroupDAO groupDao;
	private RoleDAO roleDao;
	private RoleGroupDAO roleGroupDao;
	private UserRepDAO userRepDao;
	private GroupProjectionDAO groupProjectionDAO;
	private FmdtUrlDAO fmdtUrlDao;
	private SecurityDAO gedSecurityDao;
	private PublicUrlDAO publicUrlDao;
	private UolapQueriesLoggerDao uolapQueryLogDao;
	private UnitedOlapPreloadDAO uolapQueryPreloadDao;
	private SettingsDAO settingsDao;
	private SecurityLogDAO securityLogDao;

	private boolean isLdapAuthentication;
	private boolean sortGroup;

	private List<Connection> connectionTries;

	@Override
	public List<User> getUsers() throws Exception {
		return userDao.findAll();
	}

	@Override
	public User getUserById(int id) throws Exception {
		return userDao.findByPrimaryKey(id);
	}

	@Override
	public int addUser(User d) throws Exception {
		Collection c = userDao.findAll();
		Iterator it = c.iterator();
		boolean exist = false;
		while(it.hasNext()) {
			if(((User) it.next()).getLogin().equals(d.getLogin())) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			return userDao.save(d);
		}
		else {
			throw new Exception("This username is already used");
		}

	}

	@Override
	public void delUser(User d) throws Exception {
		getLogger().info("Deleting User.id" + d.getId() + "...");
		for (UserGroup ug : userGroupDao.find4User(d.getId())) {
			userGroupDao.delete(ug);
		}
		getLogger().info("Deleted UserGroups references for user.id=" + d.getId());

		for (UserRep ur : userRepDao.findByUserId(d.getId())) {
			userRepDao.delete(ur);
		}
		getLogger().info("Deleted UserRep references for user.id=" + d.getId());
		userDao.delete(d);
		getLogger().info("Deleted User.id=" + d.getId());
	}

	@Override
	public void updateUser(User d) throws Exception {
		Collection<User> c = userDao.findAll();
		boolean exist = false;
		for(User u : c) {
			if(u.getLogin().equals(d.getLogin()) || ((d.getId() != null) && u.getId().equals(d.getId()))) {
				exist = true;
				break;
			}
		}
		if (exist) {
			userDao.update(d);
		}
		else {
			throw new Exception("This user doesnt exists");
		}

	}

	@Override
	public User authentify(String ip, String login, String password, boolean isTimedOut) throws UserNotFoundException, PasswordException, LimitNumberConnectionException, Exception {
		List<User> l = userDao.findForLogin(login);
		
		if(password.isEmpty()) {
			throw new PasswordException();
		}

		String messageFailedConnection = settingsDao.getMessageFailedConnection();

		if (l.isEmpty()) {
			if (messageFailedConnection != null && !messageFailedConnection.isEmpty()) {
				throw new Exception(messageFailedConnection);
			}
			throw new UserNotFoundException(login);
		}

		User u = l.get(0);
		
		Connection con = increaseTry(ip, u.getId(), login, messageFailedConnection);

		// try to authentify in LDAP
		if (isLdapAuthentication) {
			try {
				authentifyWithLdap(login, password, u);
				return u;
			} catch (Exception e) {
				getLogger().debug("The LDAP connection failed, try with vanilla.");
				e.printStackTrace();
			}
		}

		// Encode the password
		String md5password = password;
		
		//TODO: Big security flaw - we have to encode the password every time to not allow hash to login
		// We have to use something else than md5
		
		if (!password.matches("[0-9a-f]{32}")) {
			md5password = Security.encode(password);
		}
		
//		System.out.println(md5password);
//		System.out.println(u.getPassword());
		if (!u.getPassword().equalsIgnoreCase(md5password)) {
			con.increaseNbConnection();
			
			saveSecurityLog(TypeSecurityLog.WRONG_PASSWORD, u.getId(), ip);
			
			if (messageFailedConnection != null && !messageFailedConnection.isEmpty()) {
				throw new Exception(messageFailedConnection);
			}
			throw new PasswordException();
		}
		
		if (u.getDateStopActif() != null && u.getDateStopActif().before(new Date())) {
			if (messageFailedConnection != null && !messageFailedConnection.isEmpty()) {
				throw new Exception(messageFailedConnection);
			}
			throw new Exception("The user is inactive.");
		}

		saveSecurityLog(TypeSecurityLog.LOGIN, u.getId(), ip);
		
		return u;
	}

	private void saveSecurityLog(TypeSecurityLog type, int userId, String clientIp) {
		SecurityLog log = new SecurityLog(type, userId, clientIp);
		//securityLogDao.save(log);
	}

	private Connection increaseTry(String ip, int userId, String login, String messageFailedConnection) throws LimitNumberConnectionException, Exception {
		List<Connection> connections = getConnections();

//		getLogger().debug("Trying to connect with ip = " + ip);

		boolean found = false;
		for (Connection con : connections) {
			if (con.getIp().equals(ip) && con.getLogin().equals(login)) {
				found = true;

				if (con.isMaxTryReached()) {
					float nbMin = con.getInactivationDelay() / 1000 / 60;
					getLogger().debug("Max connection reached for ip " + ip);
					
					saveSecurityLog(TypeSecurityLog.TOO_MANY_TRIES, userId, ip);
					
					if (messageFailedConnection != null && !messageFailedConnection.isEmpty()) {
						throw new Exception(messageFailedConnection);
					}
					throw new LimitNumberConnectionException(nbMin);
				}

				return con;
			}
		}

		if (!found) {
			int maxNbConnection = settingsDao.getMaxNbConnection();
			int inactivationDelay = settingsDao.getInactivationDelay();
			
			Connection con = new Connection(ip, login, maxNbConnection, inactivationDelay);
			connections.add(con);
			return con;
		}

		return null;
	}

	private void authentifyWithLdap(String name, String password, User user) throws Exception {
		String login = name;
		Properties env = new Properties();
		env.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(DirContext.PROVIDER_URL, ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_URL));
		env.put(DirContext.SECURITY_AUTHENTICATION, ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_TYPE));
		if (ConfigurationManager.getProperty("bpm.ldap.authentication.domaine") != null && !ConfigurationManager.getProperty("bpm.ldap.authentication.domaine").isEmpty()) {
			login += "@" + ConfigurationManager.getProperty("bpm.ldap.authentication.domaine");
		}
		if (ConfigurationManager.getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_USER_CONTAINER) == null || ConfigurationManager.getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_USER_CONTAINER).isEmpty()) {
			env.put(DirContext.SECURITY_PRINCIPAL, user.getFunction());
		}
		else {
			env.put(DirContext.SECURITY_PRINCIPAL, "uid=" + login + "," + ConfigurationManager.getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_USER_CONTAINER));
		}
		env.put(DirContext.SECURITY_CREDENTIALS, password);

		try {
			DirContext dc = new InitialDirContext(env);
			getLogger().info("Connection with LDAP successful for user " + name);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public User getUserByName(String name) throws Exception {
		List<User> l = userDao.findForName(name);
		if (l == null || l.isEmpty() || l.size() > 1)
			return null;

		return l.get(0);
	}

	@Override
	public List<User> getUsers(int begin, int end) throws Exception {
		return userDao.getUsers(begin, end);
	}

	@Override
	public List<User> getUsersForGroup(Group group) throws Exception {
		return userDao.getUsersByGroup(group.getId());
//		List<User> res = new ArrayList<User>();
//		for (UserGroup ug : userGroupDao.find4Group(group.getId())) {
//			for (User u : getUsers()) {
//				if (u.getId().equals(ug.getUserId())) {
//					res.add(u);
//				}
//			}
//		}
//		return res;
	}

	@Override
	public int getUserNumbers() throws Exception {
		return userDao.getUsersNumber();
	}

	@Override
	public void addUserGroup(UserGroup d) {
		UserGroup usergroup = userGroupDao.findByUserGroup(d.getUserId(), d.getGroupId());
		if (usergroup == null)
			userGroupDao.save(d);
	}

	@Override
	public UserGroup getUserGroup(int userId, int groupId) {
		return userGroupDao.findByUserGroup(userId, groupId);
	}

	@Override
	public void delUserGroup(UserGroup d) {
		if (d.getId() == null) {
			d = userGroupDao.findByUserGroup(d.getUserId(), d.getGroupId());
		}
		userGroupDao.delete(d);
	}

	@Override
	public List<UserGroup> getUserGroups(User user) {
		return userGroupDao.find4User(user.getId());
	}

	@Override
	public List<UserGroup> getUserGroups(Group group) {
		return userGroupDao.find4Group(group.getId());

	}

	@Override
	public List<UserGroup> getUserGroups() {
		return userGroupDao.findAll();
	}

	@Override
	public List<Group> getGroups() {
		List<Group> groups = groupDao.findAll();
		if (sortGroup) {
			sortGroups(groups);
		}
		return groups;
	}

	@Override
	public int addGroup(Group d) {
		Group g = groupDao.findByName(d.getName());
		if (g != null) {
			return g.getId();
		}
		else {
			return groupDao.save(d);
		}
	}

	@Override
	public void delGroup(Group d) throws Exception {
		getLogger().info("Deleting Group.id=" + d.getId() + " ...");
		for (RoleGroup rg : roleGroupDao.getForGroupId(d.getId())) {
			roleGroupDao.delete(rg);
			getLogger().info("Deleted RoleGroup.id=" + rg.getId());
		}

		for (UserGroup ug : userGroupDao.find4Group(d.getId())) {
			userGroupDao.delete(ug);
			getLogger().info("Deleted UserGroup.id=" + ug.getId());
		}

		if (d.getName() != null) {
			List<FmdtUrl> urls = fmdtUrlDao.findByGroupName(d.getName());
			if (urls != null) {
				fmdtUrlDao.deleteAll(urls);
			}
		}

		List<bpm.vanilla.platform.core.beans.ged.Security> gedSecs = gedSecurityDao.findForGroup(d.getId());
		if (gedSecs != null) {
			gedSecurityDao.deleteAll(gedSecs);
		}
		
		List<PublicUrl> publicUrls = publicUrlDao.findByGroupdId(d.getId());
		if (publicUrls != null) {
			publicUrlDao.deleteAll(publicUrls);
		}
		
		List<UOlapQueryBean> queries = uolapQueryLogDao.listByGroupId(d.getId());
		if (queries != null) {
			uolapQueryLogDao.deleteAll(queries);
		}
		
		List<UOlapPreloadBean> preloads = uolapQueryPreloadDao.listByGroupId(d.getId());
		if (preloads != null) {
			uolapQueryPreloadDao.deleteAll(preloads);
		}

		try {
			manageRepositoryGroup(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		groupDao.delete(d);
		getLogger().info("Deleted Group.id=" + d.getId());
	}

	private void manageRepositoryGroup(Group group) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, login, password);

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		List<Repository> repositories = vanillaApi.getVanillaRepositoryManager().getRepositories();

		Group dummyGroup = new Group();
		dummyGroup.setId(-1);

		if (repositories != null) {
			for (Repository rep : repositories) {
				IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, dummyGroup, rep);
				IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
				repositoryApi.getAdminService().removeGroup(group);
			}
		}
	}

	@Override
	public void updateGroup(Group d) {
		groupDao.update(d);
	}

	@Override
	public Group getGroupById(int groupId) {
		List<Group> l = groupDao.findByPrimaryKey(groupId);
		if (l.isEmpty())
			return null;
		else
			return l.get(0);
	}

	@Override
	public Group getGroupByName(String groupName) {
		return groupDao.findByName(groupName);
	}

	@Override
	public List<Group> getGroups(int begin, int end) {
		List<Group> groups = groupDao.getGroups(begin, end);
		if (sortGroup) {
			sortGroups(groups);
		}
		return groups;
	}

	@Override
	public List<Group> getChilds(Group group) {
		List<Group> groups = groupDao.getGroupsChildOf(group);
		if (sortGroup) {
			sortGroups(groups);
		}
		return groups;
	}

	@Override
	public List<Group> getGroups(User user) {
		List<UserGroup> l = getUserGroups(user);
		List<Group> groups = new ArrayList<Group>();

		for (UserGroup ug : l) {
			Group g = getGroupById(ug.getGroupId());
			if (g != null) {
				groups.add(g);
			}
		}

		if (sortGroup) {
			sortGroups(groups);
		}

		return groups;
	}

	@Override
	public int getGroupsNumber() {

		return groupDao.getGroupsNumber();
	}

	@Override
	public List<Role> getRoles() {
		return (List) roleDao.findAll();
	}

	@Override
	public int addRole(Role d) {
		Role r = roleDao.findByName(d.getName());
		if (r != null) {
			return r.getId();
		}
		else {
			return roleDao.save(d);
		}
	}

	@Override
	public void delRole(Role d) {
		roleDao.delete(d);
	}

	@Override
	public void updateRole(Role d) {
		roleDao.update(d);
	}

	@Override
	public Role getRoleById(int id) {
		if (roleDao.findByPrimaryKey(id).isEmpty()) {
			return null;
		}
		return roleDao.findByPrimaryKey(id).get(0);
	}

	@Override
	public List<Role> getRolesForAppId(int id, String type) {
		return roleDao.getForAppById(id, type);
	}

	@Override
	public Role getRoleByName(String name) {
		return roleDao.findByName(name);
	}

	@Override
	public List<Role> getRolesForApp(String appName) {
		return roleDao.getForApp(appName);
	}

	@Override
	public List<Role> getRolesForGroup(Group gr) {
		List<Role> res = new ArrayList<Role>();
//		List<Role> roles = getRoles();
		for (RoleGroup rg : roleGroupDao.getForGroupId(gr.getId())) {
			Role role = getRoleById(rg.getRoleId());
			if (role != null) {
				res.add(role);
			}
		}
		return res;
	}

	@Override
	public void addRoleGroup(RoleGroup d) {
		//We check if the link with group and role already exist
		RoleGroup rg = getRoleGroups(d.getRoleId(), d.getGroupId());
		if (rg == null) {
			roleGroupDao.save(d);
		}
	}

	@Override
	public void delRoleGroup(RoleGroup d) {
		roleGroupDao.delete(d);
	}

	@Override
	public List<RoleGroup> getRoleGroups(Group g) {
		return roleGroupDao.getForGroupId(g.getId());
	}

	@Override
	public List<RoleGroup> getRoleGroups(Role g) {
		return roleGroupDao.getForRoleId(g.getId());
	}

	@Override
	public RoleGroup getRoleGroups(int roleId, int groupId) {
		return roleGroupDao.findByRoleGroup(roleId, groupId);
	}

	@Override
	public List<RoleGroup> getRoleGroups() {
		return (List) roleGroupDao.findAll();
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		this.groupDao = getDao().getGroupDao();
		if (this.groupDao == null) {
			throw new Exception("Missing GroupDao!");
		}
		this.roleDao = getDao().getRoleDao();
		if (this.roleDao == null) {
			throw new Exception("Missing RoleDao!");
		}
		this.roleGroupDao = getDao().getRoleGroupDao();
		if (this.roleGroupDao == null) {
			throw new Exception("Missing RoleGroupDao!");
		}
		this.userDao = getDao().getUserDao();
		if (this.userDao == null) {
			throw new Exception("Missing UserDao!");
		}
		this.userGroupDao = getDao().getUserGroupDao();
		if (this.userGroupDao == null) {
			throw new Exception("Missing UserGroupDao!");
		}
		this.userRepDao = getDao().getUserRepDao();
		if (this.userRepDao == null) {
			throw new Exception("Missing UserRepDao!");
		}
		this.groupProjectionDAO = getDao().getGroupProjectionDAO();
		if (this.groupProjectionDAO == null) {
			throw new Exception("Missing GroupProjectionDAO");
		}
		this.fmdtUrlDao = getDao().getFmdtUrlDao();
		if (this.fmdtUrlDao == null) {
			throw new Exception("Missing FmdtUrlDao");
		}
		this.gedSecurityDao = getDao().getSecurityDao();
		if (this.gedSecurityDao == null) {
			throw new Exception("Missing FmdtUrlDao");
		}
		this.publicUrlDao = getDao().getPublicUrlDao();
		if (this.publicUrlDao == null) {
			throw new Exception("Missing PublicUrlDao");
		}
		this.uolapQueryLogDao = getDao().getUolapQueryLogDao();
		if (this.uolapQueryLogDao == null) {
			throw new Exception("Missing UolapQueryLogDao");
		}
		this.uolapQueryPreloadDao = getDao().getUnitedOlapPreloadDao();
		if (this.uolapQueryPreloadDao == null) {
			throw new Exception("Missing UnitedOlapPreloadDao");
		}
		this.settingsDao = getDao().getSettingsDao();
		if (this.settingsDao == null) {
			throw new Exception("Missing SettingsDao");
		}

		this.securityLogDao = getDao().getSecurityLogDao();
		if (this.securityLogDao == null) {
			throw new Exception("Missing SecurityLogDao!");
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

		try {
			String authenticationLdap = config.getProperty(VanillaConfiguration.P_LDAP_AUTHENTICATION_ACTIVE);
			if (Boolean.parseBoolean(authenticationLdap)) {
				isLdapAuthentication = true;
			}
			else {
				isLdapAuthentication = false;
			}
		} catch (Exception e) {
			getLogger().warn("No property defined for LDAP authentication, assuming it's false.");
			isLdapAuthentication = false;
		}

//		try {
//			this.maxNbConnection = Integer.parseInt(config.getProperty(VanillaConfiguration.P_AUTHENTICATION_MAX_TRY));
//			this.blockTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_AUTHENTICATION_MAX_TRY_BLOCKTIME));
//		} catch (Exception e) {
//			getLogger().warn("No property defined for max connection try or block time, put default 5 and 1800000 ms.");
//			this.maxNbConnection = 5;
//			this.blockTime = 1800000;
//		}

		try {
			String sortGroup = config.getProperty(VanillaConfiguration.P_AUTHENTICATION_SORT_GROUP);
			if (Boolean.parseBoolean(sortGroup)) {
				this.sortGroup = true;
			}
			else {
				this.sortGroup = false;
			}
		} catch (Exception e) {
			getLogger().warn("No property defined for Sorting Groups, assuming it's false.");
			this.sortGroup = false;
		}

		ConnectionThreadManager connectionThread = new ConnectionThreadManager(this);
		connectionThread.start();

		getLogger().info("init done!");

	}

	@Override
	public int addGroupProjection(GroupProjection gp) throws Exception {
		return this.groupProjectionDAO.save(gp);
	}

	@Override
	public void deleteGroupProjection(GroupProjection gp) throws Exception {
		this.groupProjectionDAO.delete(gp);
	}

	@Override
	public List<GroupProjection> getGroupProjections() throws Exception {
		return this.groupProjectionDAO.findAll();
	}

	@Override
	public List<GroupProjection> getGroupProjectionsByFasdId(int fasdId) throws Exception {
		return this.groupProjectionDAO.findByFasdId(fasdId);
	}

	@Override
	public void addUserImage(int userId, DataHandler datas, String format) throws Exception {
		getLogger().info("Adding image to User.id=" + userId + "...");
		User user = userDao.findByPrimaryKey(userId);

		if (user == null) {
			throw new Exception("The user does not exist.");
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String imageFolder = config.getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);

		byte[] b = new byte[1024];
		int n = 0;

		String imageName = "UserImage_" + new Object().hashCode();
		File f = new File(imageFolder + File.separator + imageName + "." + format);
		try {
			FileOutputStream fos = new FileOutputStream(f);

			InputStream is = datas.getInputStream();

			while ((n = is.read(b, 0, 1024)) > 0) {
				fos.write(b, 0, n);
			}
			fos.close();
			is.close();
			getLogger().info("Stored image file in " + f.getAbsolutePath());

			user.setImage(imageName + "." + format);
			userDao.update(user);
			getLogger().info("User has been updated with the new image.");
		} catch (Exception e) {
			throw new Exception("The image was not changed due to an error:" + e.getMessage());
		}
	}

	@Override
	public byte[] getImageForUserId(int userId) throws Exception {
		getLogger().info("Getting image for User.id=" + userId + "...");
		User user = userDao.findByPrimaryKey(userId);

		if (user == null) {
			throw new Exception("The user does not exist.");
		}

		if (user.getImage() == null || user.getImage().isEmpty()) {
			throw new Exception("The user does not have an image set.");
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String imageFolder = config.getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);

		DataSource ds = new FileDataSource(imageFolder + File.separator + user.getImage());
		DataHandler imageData = new DataHandler(ds);

		byte[] currentXMLBytes = IOUtils.toByteArray(imageData.getInputStream());
		return Base64.encodeBase64(currentXMLBytes);
	}

	@Override
	public String addGroupImage(DataHandler data, String format) throws Exception {
		getLogger().info("Adding image to Groups ...");

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String imageFolder = config.getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);

		byte[] b = new byte[1024];
		int n = 0;

		String path = "";

		String imageName = "GroupImage_" + new Object().hashCode();
		File f = new File(imageFolder + File.separator + imageName + "." + format);
		try {
			FileOutputStream fos = new FileOutputStream(f);

			InputStream is = data.getInputStream();

			while ((n = is.read(b, 0, 1024)) > 0) {
				fos.write(b, 0, n);
			}
			fos.close();
			is.close();
			getLogger().info("Stored image file in " + f.getAbsolutePath());

			path = imageName + "." + format;
			getLogger().info("The Group Image has been add to Vanilla File folder.");
		} catch (Exception e) {
			throw new Exception("The image was not changed due to an error:" + e.getMessage());
		}

		return path;
	}

	@Override
	public byte[] getImageForGroupId(int groupId) throws Exception {
		getLogger().info("Getting image for Group.id=" + groupId + "...");
		try {
			List<Group> grs = groupDao.findByPrimaryKey(groupId);
			Group group = !grs.isEmpty() ? grs.get(0) : null;
	
			if (group == null) {
				throw new Exception("The user does not exist.");
			}
	
			if (group.getImage() == null || group.getImage().isEmpty()) {
				getLogger().info("The group does not have an image set.");
				return new byte[0];
			}
	
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String imageFolder = config.getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);
	
			DataSource ds = new FileDataSource(imageFolder + File.separator + group.getImage());
			DataHandler imageData = new DataHandler(ds);
	
			byte[] currentXMLBytes = IOUtils.toByteArray(imageData.getInputStream());
			return Base64.encodeBase64(currentXMLBytes);
		} catch (Exception e) {
			getLogger().info("Unable to find image for Group.id=" + groupId);
			return null;
		}
	}

	@Override
	public boolean canAccessApp(int groupId, String appName) throws Exception {
		if (groupId <= 0) {
			return true;
		}

		Group group = null;
		try {
			group = getGroupById(groupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to contact VanillaServer.\n" + ex.getMessage(), ex);
		}

		if (group == null) {
			throw new Exception("No group with id=" + groupId);
		}

		List<Role> roles = null;
		try {
			roles = getRolesForGroup(group);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to contact VanillaServer.\n" + ex.getMessage(), ex);
		}

		for (Role r : roles) {
			if (r.getType().equals(appName)) {
				if (r.getGrants().contains("A")) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized List<Connection> getConnections() {
		if (connectionTries == null) {
			connectionTries = new ArrayList<Connection>();
		}
		return connectionTries;
	}

	private void sortGroups(List<Group> groups) {
		if (groups != null && !groups.isEmpty()) {
			Collections.sort(groups, new GroupSort());
		}
	}

	private class GroupSort implements Comparator<Group> {

		@Override
		public int compare(Group o1, Group o2) {
			if (o1.getName() != null && o2.getName() != null) {
				return o1.getName().compareTo(o2.getName());
			}

			return 0;
		}

	}

	@Override
	public User getUserByLogin(String login) throws Exception {
		List<User> l = userDao.findForLogin(login);
		if(l == null || l.isEmpty() || l.size() > 1)
			return null;

		return l.get(0);
	}

	@Override
	public Settings getSettings() throws Exception {
		return settingsDao.getSettings();
	}

	@Override
	public void updateSettings(Settings settings) throws Exception {
		settingsDao.saveOrUpdate(settings);
	}

	@Override
	public boolean checkPasswordBackupHash(String hash) throws Exception {
		return userDao.checkPasswordBackupValidity(hash);
	}

	@Override
	public User getUserByPasswordBackupHash(String hash) throws Exception {
		return userDao.getUserByPasswordBackupHash(hash);
	}

	@Override
	public void managePasswordBackup(PasswordBackup backup) throws Exception {
		if (backup.getId() > 0) {
			userDao.update(backup);
		}
		else {
			userDao.save(backup);
		}
	}

	@Override
	public void removePasswordBackup(PasswordBackup backup) throws Exception {
		userDao.delete(backup);
	}

	@Override
	public List<PasswordBackup> getPendingPasswordChangeDemands() throws Exception {
		return userDao.getPendingPasswordChangeDemands();
	}

	@Override
	public User checkAutoLogin(String keyAutoLogin) throws Exception {
		return userDao.checkAutoLogin(keyAutoLogin);
	}

	@Override
	public String buildAutoLoginKey(User user, long expirationDelaiInMs) throws Exception {
		String keyAutoLogin = UUID.randomUUID().toString();
		int userId = user.getId();
		
		AutoLogin autoLogin = new AutoLogin(keyAutoLogin, userId, expirationDelaiInMs);
		userDao.save(autoLogin);
		return keyAutoLogin;
	}
	
	@Override
	public User checkKeycloak(String token) throws Exception {
		//TODO: Verif valid token from keycloak
//		String keycloakConfigUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_KEYCLOAK_CONFIG_URL);
//		
//		try (InputStream input = new URL(keycloakConfigUrl).openStream()) {
			AccessToken test = TokenVerifier.create(token, AccessToken.class).getToken();
			String username = test.getPreferredUsername();
			return getUserByLogin(username);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return null;
	}

}
