package bpm.freemetrics.api.organisation.relations.user_group;

import java.util.ArrayList;
import java.util.List;

public class FmUser_GroupsManager {

	private FmUser_GroupsDAO dao;

	public FmUser_GroupsManager() {
		super();
	}

	public void setDao(FmUser_GroupsDAO d) {
		this.dao = d;
	}

	public FmUser_GroupsDAO getDao() {
		return dao;
	}

	public List<FmUser_Groups> getUserGroups() {
		return dao.findAll();
	}

	public FmUser_Groups getById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public List<FmUser_Groups> getUserGroupByUserId(int id) {
		return dao.getByUserId(id);
	}

	public List<FmUser_Groups> getByGroupId(int id) {

		return dao.getByGroupId(id);
	}

	public void addUsersGroups(FmUser_Groups d) throws Exception{

		if (dao.getByUserAndGroupId(d.getUserId(), d.getGroupId()) == null){
			dao.save(d);
		}
		else{
			throw new Exception("This relation user-group already exist");
		}

	}

	public void delUsersGroups(FmUser_Groups d) {
		dao.delete(d);
	}

	public void updateUsersGroups(FmUser_Groups d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}
		else{
			throw new Exception("This relation user-group doesnt exists");
		}
	}

	public List<Integer> getUserIdsForGroupId(int groupId){

		List<Integer> resultat = new ArrayList<Integer>();

		List<FmUser_Groups> usergrous = dao.getByGroupId(groupId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Groups fmUsersGroups : usergrous) {
				if (fmUsersGroups.getGroupId().intValue() == groupId) {
					resultat.add(fmUsersGroups.getUserId());
				}
			}
		}
		return resultat;

	}

	public List<Integer> getGroupIdForUserId(int userId){

		List<Integer> resultat = new ArrayList<Integer>();

		List<FmUser_Groups> usergrous = dao.getByUserId(userId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Groups fmUsersGroups : usergrous) {
				if (fmUsersGroups.getUserId() == userId) {
					resultat.add(fmUsersGroups.getGroupId());
				}
			}
		}
		return resultat;
	}

	public boolean deleteUserGroupByUserAndGroupIds(int userId, int groupId) {

		FmUser_Groups usergrous = dao.getByUserAndGroupId(userId, groupId);

		if(usergrous != null){
			dao.delete(usergrous);
		}


		return dao.getByUserAndGroupId(userId, groupId) == null;
	}

	public boolean deleteUserGroupByGroupId(int groupId) {
		List<FmUser_Groups> usergrous = dao.getByGroupId(groupId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Groups fmUsersGroups : usergrous) {
				if (groupId == fmUsersGroups.getGroupId()) {
					dao.delete(fmUsersGroups);
				}
			}
		}

		return getByGroupId(groupId).isEmpty();
	}

	public void deleteUserGroupByUserId(int userId) {
		List<FmUser_Groups> usergrous = dao.getByUserId(userId);

		if(usergrous != null && !usergrous.isEmpty()){
			for (FmUser_Groups fmUsersGroups : usergrous) {
				if (userId == fmUsersGroups.getUserId()) {
					dao.delete(fmUsersGroups);
				}
			}
		}
	}
}
