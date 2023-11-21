package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

public class SecurityTool {
	
	private RepositoryRuntimeComponent component;
	private List<Group> loadedGroups = new ArrayList<Group>();
	
	public SecurityTool(RepositoryRuntimeComponent component) {
		this.component = component;
	}
	
		
	public User getUserFromLogin(String login) throws Exception{
		try{
    		User user = component.getVanillaRootApi().getVanillaSecurityManager().getUserByLogin(login);
    		return user;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		throw new Exception("Unable to get User from VanillaServer.\n" + ex.getMessage(), ex);
    	}
	}
	
	
	
	private boolean hasGrant(User user, int groupId, int objectType, String grant) throws Exception{
		Group group = null;
		try{
			group = component.getVanillaRootApi().getVanillaSecurityManager().getGroupById(groupId);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to contact VanillaServer.\n" + ex.getMessage(), ex);
		}
		
		if (group == null){
			throw new Exception("No group with id=" + groupId);
		}
		
		List<Role> roles = null;
		
		try{
			roles = component.getVanillaRootApi().getVanillaSecurityManager().getRolesForGroup(group);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to contact VanillaServer.\n" + ex.getMessage(), ex);
		}
		
		for(Role r : roles){
			if (r.getType().equals(IRepositoryApi.TYPES_NAMES[objectType])){
				if (r.getGrants().contains(grant)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean canCreateItem(User user, int groupId, int objectType) throws Exception{
		if (groupId <= 0){
			return true;
		}
		return hasGrant(user, groupId, objectType, "C");
		
	}
	
	
	public boolean canDeleteItem(User user, int objectType, int objectId, int repositoryId) throws Exception{
		
		List<Group> groups = null;
		groups = component.getVanillaRootApi().getVanillaSecurityManager().getGroups(user);
		
		for(Group g : groups){
			 if (component.getRepositoryDao(repositoryId).getSecuredObjectDao().getForItemAndGroup(objectId, g.getId()) != null){
				 for(Role r : component.getVanillaRootApi().getVanillaSecurityManager().getRolesForGroup(g)){
					 if (r.getType().equals(IRepositoryApi.TYPES_NAMES[objectType]) && r.getGrants().contains("D")){
						 return true;
					 }
				 }
			 }
		}
		
		return false;
		
	}
	
	
	public boolean isDirectoryAccessibleToGroup(int directoryId, int groupId, int repositoryId) throws Exception{
		List<SecuredDirectory> l = null;
		
		if (groupId <= 0){
			return true;
		}
		
		l = component.getRepositoryDao(repositoryId).getSecuredDirectoryDao().getSecuredDirectory4UserDirectory(directoryId, groupId);

		return !(l == null || l.isEmpty());
	}
	
	public boolean isUserInGroup(User user, int groupId) throws Exception{
		List<Group> l = null;
		
		try{
			l = component.getVanillaRootApi().getVanillaSecurityManager().getGroups(user); 
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to contact Vanilla Server\n" + ex.getMessage(), ex);
		}
		
		for(Group g : l){
			if (groupId == g.getId()){
				return true;
			}
		}
		
		return false;
	}


	public boolean canUpdateItem(User user, int groupId, int objectType) throws Exception{
		return hasGrant(user, groupId, objectType, "U");
	}


	public String getGroupName(int groupId) {
		for(Group g : loadedGroups){
			if (g.getId().intValue() == groupId){
				return g.getName();
			}
		}
		
		synchronized (loadedGroups) {
			loadedGroups.clear();
			try{
				loadedGroups.addAll(component.getVanillaRootApi().getVanillaSecurityManager().getGroups());
				for(Group g : loadedGroups){
					if (g.getId().intValue() == groupId){
						return g.getName();
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		return "";
	}
}
