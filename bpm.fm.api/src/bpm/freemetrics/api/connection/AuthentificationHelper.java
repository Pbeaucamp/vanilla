package bpm.freemetrics.api.connection;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.security.FmRole;
import bpm.freemetrics.api.security.FmUser;

public class AuthentificationHelper {
	
	private static AuthentificationHelper instance;
	
	private boolean isAllowed = false;
	private List<Group> groups;
	FmUser user;
	
	private AuthentificationHelper() {
		super();
		
	}
	
	public static AuthentificationHelper getInstance() {
		if(instance == null) {
			instance = new AuthentificationHelper();
		}
		return instance;
	}
	
	/**
	 *
	 * @param username
	 * @param password
	 * @param manager
	 * @return
	 */
	@Deprecated
	public boolean authentify(String username, String password, IManager manager) {
		isAllowed = false;
		try {
			user = manager.getUserByNameAndPass(username, password);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		List<FmRole> roles = manager.getRolesForUserId(user.getId());
		
		List<Group> userGroups = new ArrayList<Group>();
		
		for(FmRole role : roles) {
			if(role != null && role.getGrants().contains("L")) {
				isAllowed = true;
				List<Group> grp = manager.getAllowedGroupsForUser(user.getId(), manager.isSuperAdmin(user.getId()));
				if(grp != null) {
					for(Group g : grp) {
						userGroups.add(g);
					}
				}
			}
		}
		return isAllowed;
	}
	
	public boolean authentify(String username, String password, boolean isEncrypted, IManager manager) {
		isAllowed = false;
		try {
			user = manager.getUserByNameAndPass(username, password, isEncrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		List<FmRole> roles = manager.getRolesForUserId(user.getId());
		
		List<Group> userGroups = new ArrayList<Group>();
		
		for(FmRole role : roles) {
			if(role != null && role.getGrants().contains("L")) {
				isAllowed = true;
				List<Group> grp = manager.getAllowedGroupsForUser(user.getId(), manager.isSuperAdmin(user.getId()));
				if(grp != null) {
					for(Group g : grp) {
						userGroups.add(g);
					}
				}
			}
		}
		return isAllowed;
	}
	
	@Deprecated
	public boolean authentifyWithRole(String username, String password, IManager manager, String fmRole) {
		isAllowed = false;
		try {
			user = manager.getUserByNameAndPass(username, password);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		List<FmRole> roles = manager.getRolesForUserId(user.getId());
		
		groups = new ArrayList<Group>();
		
		for(FmRole role : roles) {
			if(role != null && role.getGrants().contains(fmRole)) {
				isAllowed = true;
				List<Group> grp = manager.getAllowedGroupsForUser(user.getId(), manager.isSuperAdmin(user.getId()));
				if(grp != null) {
					for(Group g : grp) {
						boolean exist = false;
						for(Group groupAdded : groups) {
							if(groupAdded.getId() == g.getId()) {
								exist = true;
							}
						}
						if(!exist) {
							groups.add(g);
						}
					}
				}
			}
		}
		
		return isAllowed;
	}
	
	public boolean authentifyWithRole(String username, String password, IManager manager, String fmRole, boolean encrypted) {
		isAllowed = false;
		try {
			user = manager.getUserByNameAndPass(username, password, encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		List<FmRole> roles = manager.getRolesForUserId(user.getId());
		
		groups = new ArrayList<Group>();
		
		for(FmRole role : roles) {
			if(role != null && role.getGrants().contains(fmRole)) {
				isAllowed = true;
				List<Group> grp = manager.getAllowedGroupsForUser(user.getId(), manager.isSuperAdmin(user.getId()));
				if(grp != null) {
					for(Group g : grp) {
						boolean exist = false;
						for(Group groupAdded : groups) {
							if(groupAdded.getId() == g.getId()) {
								exist = true;
							}
						}
						if(!exist) {
							groups.add(g);
						}
					}
				}
			}
		}
		
		return isAllowed;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public FmUser getUser() {
		return user;
	}
	
}
