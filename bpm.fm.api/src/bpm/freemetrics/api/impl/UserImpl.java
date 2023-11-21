/**
 * 
 */
package bpm.freemetrics.api.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.group.GroupManager;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.security.FmUserManager;
import bpm.freemetrics.api.security.relations.FmUser_RolesManager;
import bpm.freemetrics.api.utils.IReturnCode;

/**
 * @author Belgarde
 *
 */
public class UserImpl {


	public static int addUser(FmUser user,FmUserManager userMgr){
		int id = IReturnCode.USER_CREATION_FAILED;
		try {
			id = userMgr.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}		

//		if(id > 0){
//		User u = new User();
//		u.setBusinessMail(user.getBusinessMail());
//		u.setCellular(user.getCellular());
//		u.setCreation(new Date());
//		u.setFmUserId(id);
//		u.setFunction(user.getFunction());
//		u.setId(id);
//		u.setImage(user.getImage());
//		u.setLogin(user.getName());
//		u.setName(user.getName());
//		u.setPassword(user.getPassword());
//		u.setPrivateMail(user.getPrivateMail());
//		u.setSkypeName(user.getSkypeName());
//		u.setSkypeNumber(user.getSkypeNumber());
//		u.setSuperUser(false);
//		u.setTelephone(user.getTelephone());		
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").addUser(u);
//		}	

		return id;
	}

	public static void updateUser(FmUser user,FmUserManager userMgr) throws Exception {
		userMgr.updateUser(user);

//		User u = new User();

//		u.setBusinessMail(user.getBusinessMail());
//		u.setCellular(user.getCellular());
//		u.setCreation(new Date());
//		u.setFmUserId(user.getId());
//		u.setFunction(user.getFunction());
//		u.setId(user.getId());
//		u.setImage(user.getImage());
//		u.setLogin(user.getName());
//		u.setName(user.getName());
//		u.setPassword(user.getPassword());
//		u.setPrivateMail(user.getPrivateMail());
//		u.setSkypeName(user.getSkypeName());
//		u.setSkypeNumber(user.getSkypeNumber());
//		u.setSuperUser(false);
//		u.setTelephone(user.getTelephone());	

//		try {
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").modifyUser(u);
//		} catch (FactoryManagerException e) {
//		e.printStackTrace();
//		} catch (Exception e) {
//		e.printStackTrace();
//		}
	}

	public static boolean deleteUserById(int id,FmUserManager userMgr, FmUser_GroupsManager usersgrpsMgr){

		FmUser usr = null;
		try {
			usr = userMgr.getUserById(id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}		


		usersgrpsMgr.deleteUserGroupByUserId(usr.getId());
		return userMgr.delUser(usr);

	}

	public static List<FmUser> getAllowedUserForUser(int userId,boolean isSuperUser,FmUserManager userMgr,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {
		List<FmUser> res = new ArrayList<FmUser>();

		List<Integer> alReadyDoneId = new ArrayList<Integer>();

		if(isSuperUser){
			return userMgr.getUsers();
		}else{
			List<Integer> tmp  = GroupImpl.getGroupIdForUser(userId, isSuperUser,grpMgr, usersgrpsMgr);
			for (Integer groupId : tmp){

				List<Integer> tmp2  = usersgrpsMgr.getUserIdsForGroupId(groupId);
				for (Integer usId : tmp2){

					if(!alReadyDoneId.contains(usId)){
						FmUser u = userMgr.getUserById(usId);
						if(u!= null && !res.contains(u))
							res.add(u);

						alReadyDoneId.add(usId);
					}
				}
			}
		}
		return res;
	}
	
	public static List<Integer> getUserIdsForGroup(int groupId, int userId,boolean isSuperUser,FmUserManager userMgr,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {
		List<Integer> res = new ArrayList<Integer>();

		List<Integer> allowedUserIds = new ArrayList<Integer>();

		List<FmUser> allowedUser = getAllowedUserForUser(userId, isSuperUser, userMgr, grpMgr, usersgrpsMgr);

		for (FmUser fmUser : allowedUser){
			allowedUserIds.add(fmUser.getId());
		}		

		List<Integer> tmp = usersgrpsMgr.getUserIdsForGroupId(groupId);

		for(Integer rawUserId : tmp){

			if(allowedUserIds.contains(rawUserId))
				res.add(rawUserId);
		}
		return res;
	}
	
	public static List<FmUser> getUserForRoleId(int roleId,FmUserManager userMgr, FmUser_RolesManager usrRolMgr) {

		List<FmUser> res = new ArrayList<FmUser>();

		List<Integer> tmp = usrRolMgr.getUserIdsForRoleId(roleId);

		for(Integer id : tmp){			
			res.add(userMgr.getUserById(id));
		}		
		return res;

	}
	
	public static int updateUser(FmUser user, boolean isSuperAdmin,FmUserManager userMgr) {
		int retCode = 0;
		try {
			retCode = userMgr.updateUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			retCode = IReturnCode.USER_NOT_EXIST;
		}

//		User u = bpm.birep.admin.manager.FactoryManager.getManager("GL").getUserForFmUserId(user.getId());

//		if(u == null){

//		u = new User();
//		u.setFmUserId(user.getId());

//		u.setBusinessMail(user.getBusinessMail());
//		u.setCellular(user.getCellular());
//		u.setCreation(new Date());
//		u.setFunction(user.getFunction());
////		u.setId(user.getId());
//		u.setImage(user.getImage());
//		u.setLogin(user.getName());
//		u.setName(user.getName());
//		u.setPassword(user.getPassword());
//		u.setPrivateMail(user.getPrivateMail());
//		u.setSkypeName(user.getSkypeName());
//		u.setSkypeNumber(user.getSkypeNumber());
//		u.setSuperUser(isSuperAdmin);
//		u.setTelephone(user.getTelephone());	

//		try {
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").addUser(u);
//		} catch (FactoryManagerException e) {
//		e.printStackTrace();
//		retCode = IReturnCode.REPOSITORY_SYNCHRONIZATION_FAILED;
//		} catch (Exception e) {
//		e.printStackTrace();
//		retCode = IReturnCode.USER_REPOSITORY_SYNCHRONIZATION_FAILED;
//		}

//		}else{

//		u.setBusinessMail(user.getBusinessMail());
//		u.setCellular(user.getCellular());
//		u.setCreation(new Date());
//		u.setFunction(user.getFunction());
////		u.setId(user.getId());
//		u.setImage(user.getImage());
//		u.setLogin(user.getName());
//		u.setName(user.getName());
//		u.setPassword(user.getPassword());
//		u.setPrivateMail(user.getPrivateMail());
//		u.setSkypeName(user.getSkypeName());
//		u.setSkypeNumber(user.getSkypeNumber());
//		u.setSuperUser(isSuperAdmin);
//		u.setTelephone(user.getTelephone());	

//		try {
//		bpm.birep.admin.manager.FactoryManager.getManager("GL").modifyUser(u);

//		} catch (FactoryManagerException e) {
//		e.printStackTrace();
//		retCode = IReturnCode.REPOSITORY_SYNCHRONIZATION_FAILED;
//		} catch (Exception e) {
//		e.printStackTrace();
//		retCode = IReturnCode.USER_REPOSITORY_SYNCHRONIZATION_FAILED;
//		}
//		}
		return retCode;
	}
}
