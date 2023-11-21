/**
 * 
 */
package bpm.freemetrics.api.impl;

import bpm.freemetrics.api.organisation.relations.user_group.FmUser_Groups;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;

/**
 * @author Belgarde
 *
 */
public class UserGroupImpl {

	public static void addUserGroup(FmUser_Groups ug, FmUser_GroupsManager usersgrpsMgr) throws Exception {
		usersgrpsMgr.addUsersGroups(ug);

//		bpm.birep.admin.datas.Group group= null;
//		User user = null;

//		List<bpm.birep.admin.datas.Group> gprs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getGroups();

//		for (int i = 0; i < gprs.size(); i++) {
//		bpm.birep.admin.datas.Group t = gprs.get(i);
//		if( t != null && t.getId().intValue() == ug.getGroupId().intValue()){
//		group = t;
//		}
//		}		

//		if(group != null){
//		List<User> usrs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getUsers();

//		for (int i = 0; i < usrs.size(); i++) {
//		if(usrs.get(i) != null && usrs.get(i).getFmUserId() != null && usrs.get(i).getFmUserId().intValue() == ug.getUserId().intValue()){
//		user = usrs.get(i);
//		}
//		}

//		if(user != null)
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").addToGroup(user, group);

//		}
	}

	public static void deleteUserGroup(FmUser_Groups ug, FmUser_GroupsManager usersgrpsMgr) throws Exception {
		usersgrpsMgr.delUsersGroups(ug);

//		bpm.birep.admin.datas.Group group= null;
//		User user = null;

//		List<bpm.birep.admin.datas.Group> gprs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getGroups();

//		for (int i = 0; i < gprs.size(); i++) {
//		if(gprs.get(i) != null && gprs.get(i).getId() == ug.getGroupId()){
//		group = gprs.get(i);
//		}
//		}

//		if(group != null){
//		List<User> usrs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getUsers(group);

//		for (int i = 0; i < usrs.size(); i++) {
//		if(usrs.get(i) != null && usrs.get(i).getId() == ug.getUserId()){
//		user = usrs.get(i);
//		}
//		}

//		if(user != null)
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").removeUserFromGroup(user, group);

//		}
	}

	public static boolean deleteUserGroupByGroupId(int id, FmUser_GroupsManager usersgrpsMgr) {
//		List<FmUser_Groups> ugs =  getUserGroups();
//		for(int ind = 0; ind < ugs.size();ind++){

//		if(ugs.get(ind)!= null && ugs.get(ind).getGroupId() == id){
//		FmUser_Groups ug = ugs.get(ind);

//		bpm.birep.admin.datas.Group group= null;
//		User user = null;

//		try {
//		List<bpm.birep.admin.datas.Group> gprs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getGroups();

//		for (int i = 0; i < gprs.size(); i++) {
//		if(gprs.get(i) != null && gprs.get(i).getId() == ug.getGroupId()){
//		group = gprs.get(i);
//		}
//		}

//		if(group != null){
//		List<User> usrs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getUsers(group);

//		for (int i = 0; i < usrs.size(); i++) {
//		if(usrs.get(i) != null && usrs.get(i).getId() == ug.getUserId()){
//		user = usrs.get(i);
//		}
//		}
//		if(user != null)
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").removeUserFromGroup(user, group);

//		}

//		} catch (bpm.birep.admin.manager.FactoryManagerException e) {
//		e.printStackTrace();
//		}
//		}
//		}
		return usersgrpsMgr.deleteUserGroupByGroupId(id);

	}

	public static boolean deleteUserGroupByUserAndGroupIds(int userId, int groupId, FmUser_GroupsManager usersgrpsMgr) {
		return usersgrpsMgr.deleteUserGroupByUserAndGroupIds(userId, groupId);

//		bpm.birep.admin.datas.Group group= null;
//		User user = null;

//		try{
//		List<bpm.birep.admin.datas.Group> gprs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getGroups();

//		for (int i = 0; i < gprs.size(); i++) {
//		if(gprs.get(i) != null && gprs.get(i).getId() == groupId){
//		group = gprs.get(i);
//		}
//		}

//		if(group != null){
//		List<User> usrs = bpm.birep.admin.manager.FactoryManager.getManager("GL").getUsers(group);

//		for (int i = 0; i < usrs.size(); i++) {
//		if(usrs.get(i) != null && usrs.get(i).getId() != null && usrs.get(i).getFmUserId() == userId){
//		user = usrs.get(i);
//		}
//		}

//		if(user != null)
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").removeUserFromGroup(user, group);

//		}
//		} catch (bpm.birep.admin.manager.FactoryManagerException e) {
//		e.printStackTrace();
//		}
	}

	
}
