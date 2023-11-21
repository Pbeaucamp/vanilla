package bpm.freemetrics.api.organisation.relations.appl_group;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.utils.IReturnCode;

public class FmApplication_GroupsManager {

	private FmApplication_GroupsDAO dao;

	public FmApplication_GroupsManager() {
		super();
	}

	public void setDao(FmApplication_GroupsDAO d) {
		this.dao = d;
	}

	public FmApplication_GroupsDAO getDao() {
		return dao;
	}

	public List<FmApplication_Groups> getApplicationGroups() {
		return dao.findAll();
	}

	public FmApplication_Groups getById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public FmApplication_Groups getByApplicationId(int id) {

		return dao.getByApplicationId(id);
	}

	public List<FmApplication_Groups> getByGroupId(int id) {
		return dao.getByGroupId(id);
	}

	public int addApplicationsGroups(FmApplication_Groups d){

		int returnCode = IReturnCode.OPERATION_DONE_SUCCESFULLY;

		FmApplication_Groups c = dao.getByApplicationId(d.getApplicationId());

		boolean exist = false;

		if(c != null){

			returnCode = IReturnCode.ASSOCIATION_APPLICATION_GROUP_ALREADY_EXIST;
			exist = true;

		}else if ( c != null && c.getGroupId().equals(d.getGroupId())){

			returnCode = IReturnCode.ASSOCIATION_APPLICATION_GROUP_ALREADY_EXIST;

			exist = true;
		}

		if (!exist){
			dao.save(d);
		}

		return returnCode;
	}

	public void delApplicationsGroups(FmApplication_Groups d) {
		dao.delete(d);
	}

	public void updateApplicationsGroups(FmApplication_Groups d) throws Exception {

		if ( dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This relation Application-group doesnt exists");
		}
	}

	public List<Integer> getApplicationIdsForGroup(int groupId){

		List<Integer> resultat = new ArrayList<Integer>();

		List<FmApplication_Groups> applicationGroups = getByGroupId(groupId);

		if(applicationGroups != null && !applicationGroups.isEmpty()){
			for (Object element : applicationGroups) {
				FmApplication_Groups fmApplication_Groups = (FmApplication_Groups) element;
				if (fmApplication_Groups.getGroupId() == groupId) {
					resultat.add(fmApplication_Groups.getApplicationId());
				}
			}
		}
		return resultat;

	}

	public int getGroupIdForApplication(int applicationId){
		int resultat = IReturnCode.NO_GROUP_FOR_APPLICATION;

		FmApplication_Groups applicationGroups =getByApplicationId(applicationId);

		if(applicationGroups != null){
			resultat = applicationGroups.getGroupId();

		}
		return resultat;
	}

	public void deleteApplicationGroupByApplicationAndGroupIds(int applicationId, int groupId) {
		List<FmApplication_Groups> applicationGroups = dao.getByGroupId(groupId);

		if(applicationGroups != null && !applicationGroups.isEmpty()){
			for (FmApplication_Groups fmApplication_Groups : applicationGroups) {
				if (fmApplication_Groups.getApplicationId() == applicationId && groupId == fmApplication_Groups.getGroupId()) {
					dao.delete(fmApplication_Groups);
				}
			}
		}
	}

	public boolean deleteApplicationGroupByGroupId(int groupId) {
		List<FmApplication_Groups> applicationGroups = getByGroupId(groupId);

		if(applicationGroups != null && !applicationGroups.isEmpty()){
			for (Object element : applicationGroups) {
				FmApplication_Groups fmApplication_Groups = (FmApplication_Groups) element;
				if (groupId == fmApplication_Groups.getGroupId()) {
					dao.delete(fmApplication_Groups);
				}
			}
		}

		return getByGroupId(groupId).isEmpty();
	}

	public void deleteApplicationGroupByApplicId(int appid) {
		FmApplication_Groups applicationGroups = getByApplicationId(appid);

		if(applicationGroups != null){
			dao.delete(applicationGroups);
		}
	}

	public List<Application> getApplicationForGroup(int gpId) {
		return dao.getApplicationForGroup(gpId);
	}

	public Group getGroupForApplication(int appId) {
		return dao.getGroupForApplication(appId);
	}
}
