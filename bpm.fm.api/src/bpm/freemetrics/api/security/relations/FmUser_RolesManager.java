package bpm.freemetrics.api.security.relations;

import java.util.ArrayList;
import java.util.List;

public class FmUser_RolesManager {

	private FmUser_RolesDAO dao;

	public FmUser_RolesManager() {
		super();
	}

	public void setDao(FmUser_RolesDAO d) {
		this.dao = d;
	}

	public FmUser_RolesDAO getDao() {
		return dao;
	}

	public List<FmUser_Roles> getUser_Roles() {
		return dao.findAll();
	}

	public FmUser_Roles getById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public List<FmUser_Roles> getByUserId(int id) {
		return dao.getByUserId(id);
	}

	public List<FmUser_Roles> getByRoleId(int id) {
		return dao.getByRoleId(id);
	}

	public int addUsersRoles(FmUser_Roles d) throws Exception{

		if (dao.getByUserAndRoleId(d.getUserId(),d.getRoleId()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This relation user-Role already exist");
		}

	}

	public void delUsersRoles(FmUser_Roles d) {
		dao.delete(d);
	}

	public void updateUsersRoles(FmUser_Roles d) throws Exception {

		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
		}
		else{
			throw new Exception("This relation user-Role doesnt exists");
		}
	}



	public List<Integer> getRoleIdForUser(int userId){

		List<Integer> resultat = new ArrayList<Integer>();

		List<FmUser_Roles> usergrous = dao.getByUserId(userId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Roles fmUsersRoles : usergrous) {
				if (fmUsersRoles.getUserId() == userId) {
					resultat.add(fmUsersRoles.getRoleId());
				}
			}
		}
		return resultat;
	}

	public boolean deleteUserRoleByUserAndRoleIds(int userId, int RoleId) {
		FmUser_Roles usergrous = dao.getByUserAndRoleId(userId,RoleId);

		if(usergrous != null){
			return dao.delete(usergrous);
		}
		return false;
	}

	public void deleteUserRoleByRoleId(int RoleId) {
		List<FmUser_Roles> usergrous = dao.getByRoleId(RoleId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Roles fmUsersRoles : usergrous) {
				if (RoleId == fmUsersRoles.getRoleId()) {
					dao.delete(fmUsersRoles);
				}
			}
		}
	}

	public List<Integer> getUserIdsForRoleId(int roleId) {
		List<Integer> res = new ArrayList<Integer>();

		List<FmUser_Roles> usergrous = dao.getByRoleId(roleId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Roles ur : usergrous) {
				if(ur.getRoleId() == roleId && !res.contains(ur.getUserId()))
					res.add(ur.getUserId());
			}
		}
		return res; 

	}

}
