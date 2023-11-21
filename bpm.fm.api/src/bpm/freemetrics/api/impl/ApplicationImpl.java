/**
 * 
 */
package bpm.freemetrics.api.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.application.ApplicationManager;
import bpm.freemetrics.api.organisation.group.GroupManager;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_GroupsManager;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_MetricManager;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_Terr;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_TerrManager;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;

/**
 * @author Belgarde
 *
 */
public class ApplicationImpl {

	public static Application getApplicationById(int appliId,ApplicationManager appMgr) {
		return appMgr.getApplicationById(appliId);
	}

	public static List<Application> getApplications(ApplicationManager appMgr) {
		return appMgr.getApplications();
	}

	public static List<Application> getApplicationsForMetricId(int id,ApplicationManager appMgr,Assoc_Application_MetricManager aTMMgr) {

		List<Application> apps = new ArrayList<Application>();

		List<Integer> appId = aTMMgr.getApplicationIdsForMetricId(id);
		for (Integer integer : appId) {

			Application app = appMgr.getApplicationById(integer);

			if( app != null){
				apps.add(app);
			}
		}
		return apps;
	}

	public static List<Application> getApplicationForTypeTerrId(int typTerId,ApplicationManager appMgr) {
		List<Application> res = new ArrayList<Application>();

		List<Application> tmp = appMgr.getApplicationByTypeTerrId(typTerId);
		if(tmp != null && !tmp.isEmpty()){
			for(Application app : tmp){
				if(app!= null){
					res.add(app);
				}
			}		
		}
		return res;
	}

	public static int addApplication(Application app,ApplicationManager appMgr) {

		try {
			return appMgr.addApplication(app);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean deleteApplicationById(int id,ApplicationManager appMgr) {
		Application app = appMgr.getApplicationById(id);
		appMgr.delApplication(app);
		return appMgr.getApplicationById(id) == null;
	}

	public static Application getApplicationByName(String text,ApplicationManager appMgr) {
		return appMgr.getApplicationByName(text);
	}

	public static boolean updateApplication(Application application,ApplicationManager appMgr) {

		try {
			return appMgr.updateApplication(application);
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}
	}

	public static List<Application> getApplicationsForGroup(int gpId,FmApplication_GroupsManager appGrpMgr) {
		
		return appGrpMgr.getApplicationForGroup(gpId);
	}

	public static List<Integer> getApplicationIdsForGroup(int groupId, FmApplication_GroupsManager appGrpMgr) {
		return appGrpMgr.getApplicationIdsForGroup(groupId);
	}

	public static List<Application> getApplicationForTypeDecomTerrId(int typDecId,ApplicationManager appMgr, Assoc_Terr_Type_Dec_TerrManager aTTDTMgr) {
		List<Application> res = new ArrayList<Application>();

		List<Assoc_Terr_Type_Dec_Terr> tmp = aTTDTMgr.getByChildTypeDecomTerrId(typDecId);

		if(tmp != null && !tmp.isEmpty()){
			for(Assoc_Terr_Type_Dec_Terr assoc : tmp ){

				if(assoc != null){
					res.add(appMgr.getApplicationById(assoc.getParent_App_ID()));
				}
			}		
		}
		return res;
	}

	public static List<Application> getAllowedApplicationForUser(int userId,ApplicationManager appMgr, FmApplication_GroupsManager appGrpMgr, boolean isSuperUser,GroupManager grpMgr, FmUser_GroupsManager usersgrpsMgr) {

		List<Application> res = new ArrayList<Application>();

		List<Integer> allowedAppIds = new ArrayList<Integer>();

		List<Integer> tmp = GroupImpl.getGroupIdForUser(userId, isSuperUser, grpMgr, usersgrpsMgr);
		for (Integer gpId : tmp) {

			List<Integer> tmp2 = getApplicationIdsForGroup(gpId, appGrpMgr);
			for (Integer appId : tmp2) {
				if(!allowedAppIds.contains(appId)){
					allowedAppIds.add(appId);
				}
			}
		}

		for (Integer id : allowedAppIds) {
			Application a = appMgr.getApplicationById(id);
			if(a != null)
				res.add(a);
		}
		return res;
	}

}
