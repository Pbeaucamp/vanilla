/**
 * 
 */
package bpm.freemetrics.api.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.group.GroupManager;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_GroupsManager;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;
import bpm.freemetrics.api.utils.IReturnCode;

/**
 * @author Belgarde
 *
 */
public class GroupImpl {

	public static List<Integer> getGroupIdForUser(int userId,boolean isSuperUser,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {
		return isSuperUser ? getGroupsIds(grpMgr) : usersgrpsMgr.getGroupIdForUserId(userId);
	}
	
	public static List<Integer> getGroupsIds(GroupManager grpMgr) {
		return grpMgr.getGroupIds(); 
	}
	
	public static List<Group> getGroupsForUser(int userId,boolean isSuperUser,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {

		List<Group> groups = new ArrayList<Group>();
		List<Integer> lstGroupId = usersgrpsMgr.getGroupIdForUserId(userId);

		for (Integer grId  : lstGroupId) {

			Group tmp = grpMgr.getGroupById(grId);
			if( tmp!= null){
				groups.add(tmp);
			}
		}
		return isSuperUser ? grpMgr.getGroups() : groups;
	}
	
	public static List<Group> getAllowedGroupsForUser(int userId,
			boolean isSuperAdmin,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {
		List<Integer> gpIds = null;
		if(isSuperAdmin) {
			gpIds = getGroupsIds(grpMgr);
		}
		else {
			gpIds = usersgrpsMgr.getGroupIdForUserId(userId);
		}
		List<Group> res = new ArrayList<Group>();
		for (Integer id : gpIds) {
			Group g = grpMgr.getGroupById(id);
			if(g != null){
				res.add(g);
			}
		}
		return res;
	}
	
	public static int addGroup(Group group,GroupManager grpMgr) {
		int ret = IReturnCode.GROUP_CREATION_FAILED;
		try {
			ret = grpMgr.addGroup(group);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return ret;
	}
	
	public static void updateGroup(Group group,GroupManager grpMgr) throws Exception {
		grpMgr.updateGroup(group);

	}
	
	public static boolean deleteGroupById(int id,GroupManager grpMgr) {		

		boolean res = false;
		Group d = grpMgr.getGroupById(id);
		if(d!= null){
			res =  grpMgr.deleteGroupById(id);


		}
		return res;
	}

	public static Group getGroupForApplication(int appId,
			FmApplication_GroupsManager appGrpMgr) {
		return appGrpMgr.getGroupForApplication(appId);
	}
}
