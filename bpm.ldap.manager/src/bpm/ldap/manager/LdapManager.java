package bpm.ldap.manager;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class LdapManager {
	
	private static final String TYPE = "type";
	private static final String URL = "url";
	private static final String AUTH = "auth";
	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String CLASS = "class";
	private static final String FILTER = "filter";
	private static final String GROUP = "group";

	private static final String AKLABOX_URL = "aklabox.url";
	private static final String AKLABOX_USER = "aklabox.user";
	private static final String AKLABOX_PASS = "aklabox.password";
	private static final String USER_LDAPID = "user.ldapid";
	private static final String USER_MAIL = "user.email";
	private static final String USER_NAME = "user.name";
	private static final String USER_FIRSTNAME = "user.firstname";
	private static final String USER_PHONE = "user.phone";
	private static final String USER_SKYPENAME = "user.skypename";
	private static final String GROUP_DEFAULT_ID = "group.default.id";
	private static final String REPOSITORY_DEFAULT_ID = "repository.default.id";

//	private static final String BASE = "base";

//	private static final String USER_ADRESS = "user.adress";
//	private static final String USER_FUNCTION = "user.function";
//	private static final String USER_COMPANY = "user.company";
//	private static final String USER_SERVICE = "user.service";

	private static HashMap<String, String> users = new HashMap<>();

	/**
	 * Args 1 needs to be the path to ldap.properties
	 * Args 2 is optionnal and correspond to a boolean. If true we update the password
	 * Args 3 is optionnal and correspond to a specific user to load (by the login)
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String propertyPath = args[0];
		boolean updatePassword = args[1] != null ? Boolean.parseBoolean(args[1]) : false;
		String specificLogin = args.length > 2 ? args[2] : null;
		
		Properties props = new Properties();
		props.load(new FileInputStream(propertyPath));
		for (Object key : props.keySet())
			System.out.println(key + " : '" + props.getProperty(key.toString()) + "'");
		if (props.getProperty(TYPE) != null && props.getProperty(TYPE).equals("Vanilla")) {
			RemoteVanillaPlatform remote = new RemoteVanillaPlatform(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS));
			List<User> existings = remote.getVanillaSecurityManager().getUsers();
			for (User u : existings)
				users.put(u.getFunction(), u.getLogin());
			int j = 1;
			while (true) {
				String base = props.getProperty("base_" + j);
				if (base != null && !base.isEmpty()) {
					readAndInsertVanillaUsers(props, j, specificLogin, updatePassword);
					j++;
				}
				break;
			}
			insertVanillaGroups(props, specificLogin);
			return;
		}
//		int i = 1;
//		while (true) {
//			String base = props.getProperty("base_" + i);
//			if (base != null && !base.isEmpty()) {
//				readAndInsertUsers(props, i);
//				i++;
//			}
//			break;
//		}
		// if (props.getProperty(GROUP) != null &&
		// !props.getProperty(GROUP).isEmpty()) {
		// Hashtable<String, String> env = new Hashtable<>();
		// env.put("java.naming.factory.initial",
		// "com.sun.jndi.ldap.LdapCtxFactory");
		// env.put("java.naming.provider.url", props.getProperty(URL));
		// env.put("java.naming.security.authentication",
		// props.getProperty(AUTH));
		// env.put("java.naming.security.principal", props.getProperty(USER));
		// env.put("java.naming.security.credentials",
		// props.getProperty(PASS));
		// env.put("java.naming.referral", "follow");
		// DirContext ctx = new InitialLdapContext(env, null);
		// SearchControls ctrl = new SearchControls();
		// ctrl.setSearchScope(2);
		// NamingEnumeration<SearchResult> res =
		// ctx.search(props.getProperty(GROUP), "(objectClass=group)", ctrl);
		// SearchResult r = null;
		// RemoteVdmManager vdmManager = new RemoteVdmManager(new
		// VdmContext(props.getProperty("aklabox.url"),
		// props.getProperty(AKLABOX_USER),
		// props.getProperty(AKLABOX_PASS), 0));
		// User userLog = new User();
		// userLog.setEmail(props.getProperty(AKLABOX_USER));
		// userLog.setPassword(props.getProperty(AKLABOX_PASS));
		// vdmManager.connect(userLog);
		// List<Group> groups = vdmManager.getGroups();
		// List<Group> ldapGroups = new ArrayList<>();
		// while (res.hasMore()) {
		// r = res.next();
		// String gname =
		// r.getAttributes().get("name").get().toString().replaceAll("'", "''");
		// Group g = findGroup(gname, groups);
		// System.out.println("Group name : " + gname);
		// System.out.println(g);
		// if (g == null) {
		// g = new Group();
		// g.setGroupName(gname);
		// g.setGroupMail(gname);
		// vdmManager.addGroup(g, new ArrayList());
		// g = vdmManager.getGroupByAdress(gname);
		// ldapGroups.add(g);
		// }
		// else {
		// ldapGroups.add(g);
		// }
		// try {
		// NamingEnumeration<?> members =
		// r.getAttributes().get("member").getAll();
		// while (members.hasMore()) {
		// Object o = members.next();
		// if (users.get(o) != null) {
		// String id = users.get(o);
		// User u = vdmManager.getUserInfo(id);
		// List<User> us = new ArrayList<>();
		// us.add(u);
		// vdmManager.saveUserGroup(us, g.getGroupId());
		// }
		// }
		// } catch (Exception exception) {
		// }
		// }
		// try {
		// for (Group g : groups) {
		// Group gg;
		// Iterator<Group> iterator = ldapGroups.iterator();
		// do {
		// if (!iterator.hasNext()) {
		// vdmManager.deleteGroup(g);
		// break;
		// }
		// gg = iterator.next();
		// } while (!g.getGroupName().equals(gg.getGroupName()));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	private static void insertVanillaGroups(Properties props, String specificLogin) throws Exception {
		if (props.getProperty(GROUP) != null && !props.getProperty(GROUP).isEmpty()) {
			Hashtable<String, String> env = new Hashtable<>();
			env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
			env.put("java.naming.provider.url", props.getProperty(URL));
			env.put("java.naming.security.authentication", props.getProperty(AUTH));
			env.put("java.naming.security.principal", props.getProperty(USER));
			env.put("java.naming.security.credentials", props.getProperty(PASS));
			env.put("java.naming.referral", "follow");
			DirContext ctx = new InitialLdapContext(env, null);
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(2);
			NamingEnumeration<SearchResult> res = ctx.search(props.getProperty(GROUP), "(cn=*)", ctrl);
			SearchResult r = null;
			RemoteVanillaPlatform vdmManager = new RemoteVanillaPlatform(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS));
			List<Group> groups = vdmManager.getVanillaSecurityManager().getGroups();
			List<Group> ldapGroups = new ArrayList<>();
			while (res.hasMore()) {
				r = res.next();
				String gname = r.getAttributes().get("cn").get().toString().replaceAll("'", "''");
				Group g = findGroup(gname, groups, 0);
				System.out.println("Group name : " + gname);
				System.out.println(g);
				if (g == null) {
					g = new Group();
					g.setName(gname);
					g.setId(Integer.valueOf(vdmManager.getVanillaSecurityManager().addGroup(g)));
					ldapGroups.add(g);
				}
				else {
					ldapGroups.add(g);
				}
				try {
					NamingEnumeration<?> members = r.getAttributes().get("uniqueMember").getAll();
					while (members.hasMore()) {
						Object o = members.next();
						if (LdapManager.users.get(o) != null) {
							String id = LdapManager.users.get(o);
							User u = vdmManager.getVanillaSecurityManager().getUserByLogin(id);
							UserGroup ug = new UserGroup();
							ug.setGroupId(g.getId());
							ug.setUserId(u.getId());
							vdmManager.getVanillaSecurityManager().addUserGroup(ug);
						}
					}
				} catch (Exception exception) {
				}
			}
		}
		else {
			if (props.getProperty(GROUP_DEFAULT_ID) != null && !props.getProperty(GROUP_DEFAULT_ID).isEmpty()) {
				int groupId = Integer.parseInt(props.getProperty(GROUP_DEFAULT_ID));
				RemoteVanillaPlatform vdmManager = new RemoteVanillaPlatform(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS));
				List<User> users = vdmManager.getVanillaSecurityManager().getUsers();
				for (User u : users) {
					if (specificLogin == null || specificLogin.equals(u.getLogin())) {
						UserGroup ug = new UserGroup();
						ug.setGroupId(Integer.valueOf(groupId));
						ug.setUserId(u.getId());
						try {
							vdmManager.getVanillaSecurityManager().addUserGroup(ug);
							System.out.println("User added " + u.getLogin() + " to default group " + groupId);
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println("Unable to add user " + u.getLogin() + " to default group " + groupId);
						}
					}
				}
			}
			if (props.getProperty(REPOSITORY_DEFAULT_ID) != null && !props.getProperty(REPOSITORY_DEFAULT_ID).isEmpty()) {
				int repId = Integer.parseInt(props.getProperty(REPOSITORY_DEFAULT_ID));
				RemoteVanillaPlatform vdmManager = new RemoteVanillaPlatform(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS));
				List<User> users = vdmManager.getVanillaSecurityManager().getUsers();
				for (User u : users) {
					if (specificLogin == null || specificLogin.equals(u.getLogin())) {
						UserRep ug = new UserRep();
						ug.setRepositoryId(repId);
						ug.setUserId(u.getId().intValue());
						try {
							vdmManager.getVanillaRepositoryManager().addUserRep(ug);
							System.out.println("User added " + u.getLogin() + " to default repository " + repId);
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println("Unable to add user " + u.getLogin() + " to default repository " + repId);
						}
					}
				}
			}
		}
	}

	private static Group findGroup(String gname, List<Group> groups, int i) {
		for (Group g : groups) {
			if (gname.equals(g.getName()))
				return g;
		}
		return null;
	}

	private static void readAndInsertVanillaUsers(Properties props, int baseIndex, String specificLogin, boolean updatePassword) throws Exception {
		Hashtable<String, String> env = new Hashtable<>();
		env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		env.put("java.naming.provider.url", props.getProperty(URL));
		env.put("java.naming.security.authentication", props.getProperty(AUTH));
		env.put("java.naming.security.principal", props.getProperty(USER));
		env.put("java.naming.security.credentials", props.getProperty(PASS));
		env.put("java.naming.referral", "follow");
		LdapContext ctx = new InitialLdapContext(env, null);
		ctx.setRequestControls(new Control[] { new PagedResultsControl(50, false) });
		byte[] cookie = null;
		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(2);
		String filter = "";
		if (props.getProperty(FILTER) != null) {
			filter = props.getProperty(FILTER);
		}
		else {
			filter = "(objectClass=" + props.getProperty(CLASS) + ")";
		}
		RemoteVanillaPlatform remote = new RemoteVanillaPlatform(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS));
		List<User> existings = remote.getVanillaSecurityManager().getUsers();
		List<User> toAdd = new ArrayList<>();
		List<User> toUpdate = new ArrayList<>();
//		List<User> ldapUsers = new ArrayList<>();
		do {
			NamingEnumeration<SearchResult> res = ctx.search(props.getProperty("base_" + baseIndex), filter, ctrl);
			SearchResult r = null;
			while (res.hasMore()) {
				r = res.next();
				User u = new User();
				if (props.getProperty(USER_LDAPID) != null && !props.getProperty(USER_LDAPID).isEmpty())
					try {
						String ldapId = r.getAttributes().get(props.getProperty(USER_LDAPID)).get().toString().replaceAll("'", "''");
						u.setFunction(ldapId);
						
						//Generate uuid to set as the password
						String uuid = UUID.randomUUID().toString().replace("-", "");
						u.setPassword(uuid);
						System.out.println("found user : " + ldapId);
					} catch (Exception exception) {
					}
				if (props.getProperty(USER_SKYPENAME) != null && !props.getProperty(USER_SKYPENAME).isEmpty())
					try {
						String ldapId = r.getAttributes().get(props.getProperty(USER_SKYPENAME)).get().toString().replaceAll("'", "''");
						u.setSkypeName(ldapId);
						System.out.println("found user skype name : " + ldapId);
					} catch (Exception e) {
						System.out.println("error while retriving skype name " + e.getMessage());
					}
				if (props.getProperty(USER_FIRSTNAME) != null && !props.getProperty(USER_FIRSTNAME).isEmpty())
					try {
						String fname = r.getAttributes().get(props.getProperty(USER_FIRSTNAME)).get().toString().replaceAll("'", "''");
						u.setName(fname);
					} catch (Exception exception) {
					}
				if (props.getProperty(USER_MAIL) != null && !props.getProperty(USER_MAIL).isEmpty())
					try {
						String mail = r.getAttributes().get(props.getProperty(USER_MAIL)).get().toString().replaceAll("'", "''");
						u.setBusinessMail(mail);
						users.put(r.getAttributes().get("distinguishedName").get().toString().replaceAll("'", "''"), mail);
					} catch (Exception exception) {
					}
				if (props.getProperty(USER_NAME) != null && !props.getProperty(USER_NAME).isEmpty())
					try {
						String lname = r.getAttributes().get(props.getProperty(USER_NAME)).get().toString().replaceAll("'", "''");
						if (lname == null || lname.isEmpty())
							continue;
						u.setLogin(lname);
					} catch (Exception exception) {
					}
				if (props.getProperty(USER_PHONE) != null && !props.getProperty(USER_PHONE).isEmpty())
					try {
						u.setTelephone(r.getAttributes().get(props.getProperty(USER_PHONE)).get().toString());
					} catch (Exception exception) {
					}
				System.out.println("User : " + u.getFunction());
				boolean exists = false;
				for (User user : existings) {
					if (user.getFunction() != null && user.getFunction().equals(u.getFunction())) {
						exists = true;
						
						//Settings new values
						user.setFunction(u.getFunction());
						user.setSkypeName(u.getSkypeName());
						user.setName(u.getName());
						user.setBusinessMail(u.getBusinessMail());
						user.setLogin(u.getLogin());
						user.setTelephone(u.getTelephone());
						
						if (updatePassword) {
							String uuid = UUID.randomUUID().toString().replace("-", "");
							user.setPassword(Security.encode(uuid));
						}
						
						u = user;
						break;
					}
				}
				if (!exists) {
					for (User user : existings) {
						if (user.getLogin() != null && user.getLogin().equals(u.getLogin())) {
							u.setId(user.getId());
							break;
						}
					}
					toAdd.add(u);
					System.out.println("Add User : " + u.getFunction());
				}
				else {
					toUpdate.add(u);
					System.out.println("Update User : " + u.getFunction());
				}
//				ldapUsers.add(u);
				users.put(u.getFunction(), u.getLogin());
			}
			Control[] controls = ctx.getResponseControls();
			if (controls != null) {
				for (int i = 0; i < controls.length; i++) {
					if (controls[i] instanceof PagedResultsResponseControl) {
						PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
						int total = prrc.getResultSize();
						if (total != 0) {
							System.out.println("***************** END-OF-PAGE (total : " + total + ") *****************\n");
						}
						else {
							System.out.println("***************** END-OF-PAGE (total: unknown) ***************\n");
						}
						cookie = prrc.getCookie();
					}
				}
			}
			else {
				System.out.println("No controls were sent from the server");
			}
			ctx.setRequestControls(new Control[] { new PagedResultsControl(50, cookie, true) });
		} while (cookie != null);
		for (User u : toAdd) {
			try {
				if (specificLogin == null || specificLogin.equals(u.getLogin())) {
					System.out.println("add " + u.getFunction());
					remote.getVanillaSecurityManager().addUser(u);
				}
			} catch (Exception e) {
				if (e.getMessage().contains("This username is already used")) {
					if (specificLogin == null || specificLogin.equals(u.getLogin())) {
						System.out.println("update " + u.getFunction());
						remote.getVanillaSecurityManager().updateUser(u);
					}
				}
				else {
					System.out.println("User : " + u.getFunction() + " not created");
					e.printStackTrace();
				}
			}
			System.out.println("User : " + u.getFunction() + " created");
		}
		
		for (User u : toUpdate) {
			try {
				if (specificLogin == null || specificLogin.equals(u.getLogin())) {
					System.out.println("update " + u.getFunction());
					remote.getVanillaSecurityManager().updateUser(u);
				}
			} catch (Exception e) {
				System.out.println("User : " + u.getFunction() + " not updated");
				e.printStackTrace();
			}
			System.out.println("User : " + u.getFunction() + " updated");
		}
	}

//	private static Group findGroup(String gname, List<Group> groups) {
//		for (Group g : groups) {
//			if (gname.equals(g.getGroupName()))
//				return g;
//		}
//		return null;
//	}
//
//	private static void readAndInsertUsers(Properties props, int baseIndex) throws Exception {
//		Hashtable<String, String> env = new Hashtable<>();
//		env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
//		env.put("java.naming.provider.url", props.getProperty(URL));
//		env.put("java.naming.security.authentication", props.getProperty(AUTH));
//		env.put("java.naming.security.principal", props.getProperty(USER));
//		env.put("java.naming.security.credentials", props.getProperty(PASS));
//		env.put("java.naming.referral", "follow");
//		DirContext ctx = new InitialLdapContext(env, null);
//		SearchControls ctrl = new SearchControls();
//		ctrl.setSearchScope(2);
//		String filter = "";
//		if (props.getProperty(FILTER) != null) {
//			filter = props.getProperty(FILTER);
//		}
//		else {
//			filter = "(objectClass=" + props.getProperty(CLASS) + ")";
//		}
//		RemoteVdmManager vdmManager = new RemoteVdmManager(new VdmContext(props.getProperty(AKLABOX_URL), props.getProperty(AKLABOX_USER), props.getProperty(AKLABOX_PASS), 0));
//		User userLog = new User();
//		userLog.setEmail(props.getProperty(AKLABOX_USER));
//		userLog.setPassword(props.getProperty(AKLABOX_PASS));
//		vdmManager.connect(userLog);
//		List<User> existing = vdmManager.getAllUsers();
//		List<User> toAdd = new ArrayList<>();
//		List<User> ldapUsers = new ArrayList<>();
//		NamingEnumeration<SearchResult> res = ctx.search(props.getProperty("base_" + baseIndex), filter, ctrl);
//		SearchResult r = null;
//		int i = 0;
//		while (res.hasMore()) {
//			r = res.next();
//			User u = new User();
//			if (props.getProperty(USER_LDAPID) != null && !props.getProperty(USER_LDAPID).isEmpty())
//				try {
//					String ldapId = r.getAttributes().get(props.getProperty(USER_LDAPID)).get().toString().replaceAll("'", "''");
//					u.setLdapId(ldapId);
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_FIRSTNAME) != null && !props.getProperty(USER_FIRSTNAME).isEmpty())
//				try {
//					String address = r.getAttributes().get(props.getProperty(USER_FIRSTNAME)).get().toString().replaceAll("'", "''");
//					u.setAddress(address);
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_FIRSTNAME) != null && !props.getProperty(USER_FIRSTNAME).isEmpty())
//				try {
//					String fname = r.getAttributes().get(props.getProperty(USER_FIRSTNAME)).get().toString().replaceAll("'", "''");
//					u.setFirstName(fname);
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_MAIL) != null && !props.getProperty(USER_MAIL).isEmpty())
//				try {
//					String mail = r.getAttributes().get(props.getProperty(USER_MAIL)).get().toString().replaceAll("'", "''");
//					u.setEmail(mail);
//					users.put(r.getAttributes().get("distinguishedName").get().toString().replaceAll("'", "''"), mail);
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_NAME) != null && !props.getProperty(USER_NAME).isEmpty())
//				try {
//					String lname = r.getAttributes().get(props.getProperty(USER_NAME)).get().toString().replaceAll("'", "''");
//					u.setLastName(lname);
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_PHONE) != null && !props.getProperty(USER_PHONE).isEmpty())
//				try {
//					u.setPhone(r.getAttributes().get(props.getProperty(USER_PHONE)).get().toString());
//				} catch (Exception exception) {
//				}
//			if (props.getProperty(USER_COMPANY) != null && !props.getProperty(USER_COMPANY).isEmpty())
//				try {
//					u.setCompany(r.getAttributes().get(props.getProperty(USER_COMPANY)).get().toString());
//				} catch (Exception exception) {
//				}
//			System.out.println("User : " + u.getLdapId());
//			boolean exists = false;
//			for (User user : existing) {
//				if (user.getLdapId() != null && user.getLdapId().equals(u.getLdapId())) {
//					exists = true;
//					u = user;
//					break;
//				}
//			}
//			if (!exists) {
//				toAdd.add(u);
//				System.out.println("Add User : " + u.getLdapId());
//			}
//			ldapUsers.add(u);
//		}
//		for (User u : toAdd) {
//			u.setUserType("Author");
//			u.setUserValidator(true);
//			u.setEncryptPassword("none");
//			u.setWorkSize(Integer.valueOf(2000000));
//			boolean created = vdmManager.createUser(u, "", false, "", new UserRole(0, "Author"), new UserConnectionProperty(true, true, true, true, 0)).booleanValue();
//			System.out.println("User : " + u.getLdapId() + " created = " + created);
//			u = vdmManager.getUserInfo(u.getEmail());
//			DocumentMemoryUsage memory = new DocumentMemoryUsage();
//			memory.setUser(u.getEmail());
//			memory.setOfficeMemory(Integer.valueOf(0));
//			memory.setAudioMemory(Integer.valueOf(0));
//			memory.setImageMemory(Integer.valueOf(0));
//			memory.setVideoMemory(Integer.valueOf(0));
//			memory.setOtherMemory(Integer.valueOf(0));
//			memory.setXaklMemory(Integer.valueOf(0));
//			memory.setOfficeMemory(Integer.valueOf(0));
//			memory.setTextMemory(Integer.valueOf(0));
//			memory.setZipMemory(Integer.valueOf(0));
//			memory.setTotalMemory(u.getWorkSize());
//			memory.setUsedMemory(Integer.valueOf(0));
//			memory.setAlertLevelOne(true);
//			memory.setAlertLevelTwo(true);
//			memory.setAlertLevelThree(true);
//			memory.setAlertLevelFour(true);
//			vdmManager.saveDefaultMemory(memory);
//		}
//		try {
//			for (User u : existing) {
//				User us;
//				Iterator<User> iterator = ldapUsers.iterator();
//				do {
//					if (!iterator.hasNext()) {
//						vdmManager.deleteUser(u);
//						break;
//					}
//					us = iterator.next();
//				} while (!u.getLdapId().equals(us.getLdapId()));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
