package groupviewer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adminbirep.Activator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;


public class GroupDataLoader {
	
	class Loader extends Thread{
		@Override
		public void run() {
			try {
				// Get all group
				groupList = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
				// Get all Users
				userList = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers();	
				// Get group's parent 
				for (Group grp : getGroupList()){
					if (Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(grp) != null)
						groupUsersList.put(grp, Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(grp));
				}
				// Set the Last update date to now
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	private static List<Group> groupList = new ArrayList<Group>();
	private static List<User> userList = new ArrayList<User>();
	private static Map<Group, List<User>> groupUsersList = new HashMap<Group, List<User>>();
	private boolean isSet = false;
	private Date updateDate;
	     
    public GroupDataLoader() {
    	Loader l = new Loader();
    	l.start();
    	try {
			l.join();
			updateDate = new Date();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
	public List<Group> getGroupList() {	
		return groupList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public Date getLastUpdateDate(){
		return this.updateDate;
	}
	
	public List<User> getUsersInGroup(Group group){
		return (groupUsersList.containsKey(group))? groupUsersList.get(group): null;
	}

	public boolean isSet() {
		return isSet;
	}
}
