package bpm.freemetrics.api.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.freemetrics.api.connection.AuthentificationHelper;
import bpm.freemetrics.api.exception.ThemeObservatoireException;
import bpm.freemetrics.api.exception.UserObservatoireException;
import bpm.freemetrics.api.features.actions.Action;
import bpm.freemetrics.api.features.actions.ActionManager;
import bpm.freemetrics.api.features.alerts.Alert;
import bpm.freemetrics.api.features.alerts.AlertCondition;
import bpm.freemetrics.api.features.alerts.AlertConditionManager;
import bpm.freemetrics.api.features.alerts.AlertManager;
import bpm.freemetrics.api.features.alerts.MetricWithAlert;
import bpm.freemetrics.api.features.alerts.MetricWithAlertManager;
import bpm.freemetrics.api.features.favorites.UserAction;
import bpm.freemetrics.api.features.favorites.UserActionManager;
import bpm.freemetrics.api.features.favorites.UserAlertePreference;
import bpm.freemetrics.api.features.favorites.UserAlertePreferenceManager;
import bpm.freemetrics.api.features.favorites.UserPreference;
import bpm.freemetrics.api.features.favorites.UserPreferenceManager;
import bpm.freemetrics.api.features.favorites.WatchList;
import bpm.freemetrics.api.features.favorites.WatchListManager;
import bpm.freemetrics.api.features.forum.Forum;
import bpm.freemetrics.api.features.forum.ForumManager;
import bpm.freemetrics.api.features.infos.FMDatasource;
import bpm.freemetrics.api.features.infos.FMDatasourceManager;
import bpm.freemetrics.api.features.infos.Organisation;
import bpm.freemetrics.api.features.infos.OrganisationManager;
import bpm.freemetrics.api.features.infos.PropertiesManager;
import bpm.freemetrics.api.features.infos.TypeMetric;
import bpm.freemetrics.api.features.infos.TypeMetricManager;
import bpm.freemetrics.api.features.infos.TypeOrganisation;
import bpm.freemetrics.api.features.infos.TypeOrganisationManager;
import bpm.freemetrics.api.features.infos.Unit;
import bpm.freemetrics.api.features.infos.UnitManager;
import bpm.freemetrics.api.impl.ApplicationImpl;
import bpm.freemetrics.api.impl.GroupImpl;
import bpm.freemetrics.api.impl.MetricImpl;
import bpm.freemetrics.api.impl.UserGroupImpl;
import bpm.freemetrics.api.impl.UserImpl;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.application.ApplicationDAO;
import bpm.freemetrics.api.organisation.application.ApplicationHierarchy;
import bpm.freemetrics.api.organisation.application.ApplicationHierarchyManager;
import bpm.freemetrics.api.organisation.application.ApplicationManager;
import bpm.freemetrics.api.organisation.application.AssocApplicationAssocAppMetric;
import bpm.freemetrics.api.organisation.application.AssocApplicationAssocAppMetricManager;
import bpm.freemetrics.api.organisation.application.Decompo_Territoire;
import bpm.freemetrics.api.organisation.application.Decompo_TerritoireManager;
import bpm.freemetrics.api.organisation.application.TypeDecompTerritoire;
import bpm.freemetrics.api.organisation.application.TypeDecompTerritoireManager;
import bpm.freemetrics.api.organisation.application.TypeTerritoire;
import bpm.freemetrics.api.organisation.application.TypeTerritoireManager;
import bpm.freemetrics.api.organisation.dashOrTheme.DashboardRelation;
import bpm.freemetrics.api.organisation.dashOrTheme.DashboardRelationManager;
import bpm.freemetrics.api.organisation.dashOrTheme.SubTheme;
import bpm.freemetrics.api.organisation.dashOrTheme.SubThemeManager;
import bpm.freemetrics.api.organisation.dashOrTheme.Theme;
import bpm.freemetrics.api.organisation.dashOrTheme.ThemeManager;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.group.GroupManager;
import bpm.freemetrics.api.organisation.group.TypeCollectivite;
import bpm.freemetrics.api.organisation.group.TypeCollectiviteManager;
import bpm.freemetrics.api.organisation.metrics.AssocCompteurIndicateur;
import bpm.freemetrics.api.organisation.metrics.Assoc_Compteur_IndicateurManager;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricInteraction;
import bpm.freemetrics.api.organisation.metrics.MetricInteractionManager;
import bpm.freemetrics.api.organisation.metrics.MetricManager;
import bpm.freemetrics.api.organisation.metrics.MetricValueListManager;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.metrics.MetricValuesManager;
import bpm.freemetrics.api.organisation.observatoire.Observatoire;
import bpm.freemetrics.api.organisation.observatoire.ObservatoireManager;
import bpm.freemetrics.api.organisation.observatoire.ObservatoiresThemes;
import bpm.freemetrics.api.organisation.observatoire.ObservatoiresUsers;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_Groups;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_GroupsManager;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_MetricManager;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_Terr;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_TerrManager;
import bpm.freemetrics.api.organisation.relations.dash_metric.FmDashboard_MetricManager;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_Groups;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_GroupsManager;
import bpm.freemetrics.api.security.FmRole;
import bpm.freemetrics.api.security.FmRoleManager;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.security.FmUserManager;
import bpm.freemetrics.api.security.relations.FmUser_Roles;
import bpm.freemetrics.api.security.relations.FmUser_RolesManager;
import bpm.freemetrics.api.treebeans.TreeAppsThm;
import bpm.freemetrics.api.treebeans.TreeGroupsAppsThm;
import bpm.freemetrics.api.treebeans.TreeThm;
import bpm.freemetrics.api.utils.IConstants;
import bpm.freemetrics.api.utils.Tools;

public class SQLManager implements IManager{

	private FmUserManager userMgr;
	private GroupManager grpMgr;
	private FmRoleManager rolMgr;
	private ApplicationManager appMgr;
	private MetricManager metricsMgr;

	private Assoc_Application_MetricManager aTMMgr;
	private FmApplication_GroupsManager appGrpMgr;
	private FmUser_RolesManager usrRolMgr;
	private FmUser_GroupsManager usersgrpsMgr;

	@SuppressWarnings("unused")
	private FmDashboard_MetricManager dashMetrMgr;

	private MetricValuesManager mValsMgr;
	private MetricInteractionManager mInteractManager;
	private MetricWithAlertManager mWAlertMgr;
	private AlertConditionManager alConMgr;
	private ActionManager actMgr;
	private AlertManager alMgr;

	@SuppressWarnings("unused")
	private PropertiesManager proMgr;

	private ForumManager forMgr;
	private ThemeManager thmMgr;
	private SubThemeManager sthmMgr;
	private WatchListManager wlstMgr;
	private UserPreferenceManager usrPrefMgr;
	private UserAlertePreferenceManager usrAlPrefMgr;
	private UserActionManager usrActMgr;

	@SuppressWarnings("unused")
	private MetricValueListManager mLstValMgr;

	private UnitManager uMgr;
	private TypeCollectiviteManager tcolMgr;
	private TypeOrganisationManager toMgr;
	private OrganisationManager oMgr;
	private Assoc_Terr_Type_Dec_TerrManager aTTDTMgr;
	private TypeTerritoireManager tterMgr;
	private TypeDecompTerritoireManager tdtMgr;
	private Decompo_TerritoireManager decTerMgr;
	private TypeMetricManager tmMgr;
	private DashboardRelationManager drMgr;
	private Assoc_Compteur_IndicateurManager aCIMgr;
	private FMDatasourceManager dsMgr;
	
	private ObservatoireManager obsMgr;
	
	private ApplicationHierarchyManager appHieraMgr;
	private AssocApplicationAssocAppMetricManager assoAppAssocMgr;

	protected SQLManager( GroupManager grpMgr,FmUserManager userMgr, FmUser_GroupsManager usersgrpsMgr
			, Assoc_Application_MetricManager aTMMgr,FmUser_RolesManager usrRolMgr
			, FmApplication_GroupsManager appGrpMgr, ApplicationManager appMgr
			, MetricManager metrMgr, FmRoleManager rolMgr
			, FmDashboard_MetricManager dashMetrMgr, MetricValuesManager mValsMgr
			, MetricInteractionManager mInteractManager, MetricWithAlertManager mWAlertMgr
			, AlertConditionManager alConMgr, ActionManager actMgr, AlertManager alMgr
			, PropertiesManager proMgr, ForumManager forMgr,ThemeManager thmMgr
			, SubThemeManager sthmMgr, WatchListManager wlstMgr
			, UserPreferenceManager usrPrefMgr, UserAlertePreferenceManager usrAlPrefMgr
			, UserActionManager usrActMgr,MetricValueListManager mLstValMgr,UnitManager uMgr
			, TypeCollectiviteManager tcolMgr, OrganisationManager oMgr, TypeOrganisationManager toMgr
			, Assoc_Terr_Type_Dec_TerrManager aTTDTMgr, TypeTerritoireManager tterMgr
			, TypeDecompTerritoireManager tdtMgr, Decompo_TerritoireManager decTerMgr
			, TypeMetricManager tmMgr, DashboardRelationManager drMgr, Assoc_Compteur_IndicateurManager aCIMgr
			, FMDatasourceManager dsMgr
			, ObservatoireManager obsMgr, ApplicationHierarchyManager appHieraMgr, AssocApplicationAssocAppMetricManager assoAppAssocMgr) {

		this.appMgr = appMgr;
		this.metricsMgr = metrMgr;
		this.userMgr = userMgr;
		this.grpMgr = grpMgr;
		this.usersgrpsMgr = usersgrpsMgr;
		this.aTMMgr = aTMMgr;
		this.appGrpMgr = appGrpMgr;
		this.usrRolMgr = usrRolMgr;
		this.rolMgr= rolMgr;
		this.dashMetrMgr = dashMetrMgr;
		this.mValsMgr = mValsMgr;
		this.mInteractManager = mInteractManager;
		this.mWAlertMgr = mWAlertMgr;
		this.alConMgr = alConMgr;
		this.actMgr = actMgr;
		this.alMgr = alMgr;
		this.proMgr = proMgr;
		this.forMgr = forMgr;
		this.thmMgr = thmMgr;
		this.sthmMgr = sthmMgr;
		this.wlstMgr = wlstMgr;
		this.usrPrefMgr= usrPrefMgr;
		this.usrAlPrefMgr = usrAlPrefMgr;
		this.usrActMgr = usrActMgr;
		this.mLstValMgr = mLstValMgr;
		this.uMgr = uMgr;
		this.tcolMgr = tcolMgr;
		this.toMgr = toMgr;
		this.oMgr = oMgr;

		this.aTTDTMgr = aTTDTMgr;
		this.tterMgr = tterMgr;
		this.tdtMgr = tdtMgr;
		this.decTerMgr = decTerMgr;

		this.tmMgr = tmMgr;
		this.drMgr = drMgr;

		this.aCIMgr = aCIMgr;	

		this.dsMgr = dsMgr;
		
		this.obsMgr = obsMgr;

		this.appHieraMgr = appHieraMgr;
		this.assoAppAssocMgr = assoAppAssocMgr;
	}

	public List<FmUser> getUsers() {
		return userMgr.getUsers();
	}

	public int addUser(FmUser user){
		return UserImpl.addUser(user, userMgr);
	}

	public FmUser getUserById(int id) {
		return userMgr.getUserById(id);
	}

	public FmUser getUserByNameAndPass(String name, String password) throws Exception {
		return userMgr.getUserByNameAndPass(name, password);
	}

	public int addGroup(Group group) {
		return GroupImpl.addGroup(group, grpMgr);
	}

	public int addAssoc_Territoire_Metric(Assoc_Application_Metric sthm) throws Exception {
		int assoId = aTMMgr.addAssoc_Territoire_Metric(sthm);
		Metric current = metricsMgr.getMetricById(sthm.getMetr_ID());
		if (!current.getMdGlIsCompteur()) {
			MetricValues v = new MetricValues();
			v.setMvGlAssoc_ID(assoId);
			
			if(current.getTarget() != null) {
				v.setMvGlObjectif(Float.parseFloat(current.getTarget()));
			}
			if (current.getMinValue() != null) {
				v.setMvMinValue(new Float(current.getMinValue()));
			}
			if (current.getMinSeuil() != null) {
				v.setMvGlValeurSeuilMini(current.getMinSeuil());
			}
			if (current.getMaxValue() != null) {
				v.setMvMaxValue(new Float(current.getMaxValue()));
			}
			if(current.getMaxSeuil() != null) {
				v.setMvGlValeurSeuilMaxi(current.getMaxSeuil());
			}
			v.setMvCreationDate(Calendar.getInstance().getTime());
			v.setMvPeriodDate(v.getMvCreationDate());
			
			mValsMgr.addMetricValues(v);
		}
		
		return assoId;
	}

	public Assoc_Application_Metric getAssoc_Territoire_MetricById(int id) throws Exception {
		Assoc_Application_Metric asso = aTMMgr.getAssoc_Territoire_MetricById(id);
		asso.setApplications(findApplicationsByAssoId(id));
		return asso;
	}

	public Assoc_Application_Metric getAssoc_Territoire_MetricByName(String name) throws Exception {
		return aTMMgr.getAssoc_Territoire_MetricByName(name);
	}

	public List<Assoc_Application_Metric> getAssoc_Territoire_Metrics() throws Exception {
		return aTMMgr.getAssoc_Territoire_Metrics();
	}

	public Assoc_Application_Metric getAssoc_Territoire_Metric_ById(int assocId) throws Exception {
		return aTMMgr.getAssocForId(assocId);
	}
	
	public void addUserGroup(FmUser_Groups ug) throws Exception {
		UserGroupImpl.addUserGroup(ug, usersgrpsMgr);
	}

	public void deleteUserGroup(FmUser_Groups ug) throws Exception {
		UserGroupImpl.deleteUserGroup(ug, usersgrpsMgr);
	}

	public boolean deleteUserGroupByGroupId(int id) {
		return UserGroupImpl.deleteUserGroupByGroupId(id, usersgrpsMgr);

	}

	public boolean deleteUserGroupByUserAndGroupIds(int userId, int groupId) {
		return UserGroupImpl.deleteUserGroupByUserAndGroupIds(userId, groupId, usersgrpsMgr);
	}

	public Group getGroupById(int id) {
		return grpMgr.getGroupById(id);
	}

	public FmUser_Groups getUserGroupById(int id){
		return usersgrpsMgr.getById(id);
	}
	
	public List<FmUser_Groups> getUserGroupByUserId(int id) {
		return usersgrpsMgr.getUserGroupByUserId(id);
	}

	public List<FmUser_Groups> getUserGroups()  {
		return usersgrpsMgr.getUserGroups()!= null ? usersgrpsMgr.getUserGroups() : new ArrayList<FmUser_Groups>();
	}

	public List<Integer> getUserIdsForGroup(int groupId) {
		return usersgrpsMgr.getUserIdsForGroupId(groupId);
	}

	public void updateGroup(Group group) throws Exception {
		GroupImpl.updateGroup(group, grpMgr);
	}

	public void updateUserGroup(FmUser_Groups ug) throws Exception {
		usersgrpsMgr.updateUsersGroups(ug);
	}

	public List<Integer> getGroupIdForUser(int userId) {
		boolean isSuperUser = isSuperAdmin(userId);
		return isSuperUser ? getGroupsIds() : usersgrpsMgr.getGroupIdForUserId(userId);
	}

	public int addApplicationGroup(FmApplication_Groups ag) {
		return appGrpMgr.addApplicationsGroups(ag);
	}

	public void deleteApplicationGroupByApplicationAndGroupIds(int applicationId,
			int groupId) {
		appGrpMgr.deleteApplicationGroupByApplicationAndGroupIds(applicationId, groupId);
	}

	public boolean deleteApplicationGroupByGroupId(int groupId) {
		return appGrpMgr.deleteApplicationGroupByGroupId(groupId);
	}

	public List<Integer> getApplicationIdsForGroup(int groupId) {
		return ApplicationImpl.getApplicationIdsForGroup(groupId, appGrpMgr);
	}

	public int getGroupIdForApplication(int applicationId) {
		return appGrpMgr.getGroupIdForApplication(applicationId);
	}

	public List<Group> getGroups() {
		return grpMgr.getGroups();
	}

	public List<Integer> getGroupsIds() {
		return GroupImpl.getGroupsIds(grpMgr);
	}

	public List<Application> getApplications() {
		return ApplicationImpl.getApplications(appMgr);
	}


	public List<Application> getApplicationsForMetricId(int id) {
		return ApplicationImpl.getApplicationsForMetricId(id, appMgr, aTMMgr);
	}

	public List<Metric> getMetricsForGroupId(int grpid) {
		return metricsMgr.getMetricsForGroupId(grpid);
	}

	public List<FmRole> getRolesForUserId(int userId) {

		List<FmRole> roles = new ArrayList<FmRole>();

		for (FmUser_Roles fmRole : usrRolMgr.getByUserId(userId)) {

			if(fmRole != null){
				FmRole ro = rolMgr.getRoleById(fmRole.getRoleId());

				if(ro != null){
					roles.add(ro);
				}
			}
		}
		return roles;
	}

	public List<Metric> getMetricsForOwnerId(int userid) {
		return metricsMgr.getMetricsForOwnerId(userid);
	}

	public List<Metric> getMetricsForTypeId(int typeId) {
		return metricsMgr.getMetricsForTypeId(typeId);
	}

	public List<Metric> getMetrics() {		
		return metricsMgr.getMetrics();
	}

	public FmUser getUserByName(String name) {
		return userMgr.getUserByLogin(name);
	}

	public Metric getMetricById(int id) {
		return metricsMgr.getMetricById(id);
	}

	public List<MetricValues> getValuesForMetricAppBeforeDate(int appId, int metrId,
			Date date) {

		return MetricImpl.getValuesForMetricAppBeforeDate(appId, metrId, date, mValsMgr, aTMMgr);
	}

	public List<Group> getGroupsForUser(int userId) {

		boolean isSuperUser = isSuperAdmin(userId);
		return GroupImpl.getGroupsForUser(userId, isSuperUser, grpMgr, usersgrpsMgr);
	}

	public List<Metric> getMetricsForApplicationId(int id, int filter) {
		return MetricImpl.getMetricsForApplicationId(id, filter, metricsMgr, aTMMgr);
	}

	public boolean isSuperAdmin(int userId) {
		boolean resultat = false;

		for (FmUser_Roles ur : usrRolMgr.getByUserId(userId)) {

			FmRole role = rolMgr.getRoleById(ur.getRoleId());
			if(role != null && role.getGrants().contains("A")
					&& role.getGrants().contains("D")
					&& role.getGrants().contains("W")						
					&& role.getGrants().contains("U")
					&& role.getGrants().contains("L")){
				resultat = true;
				break;
			}
		}
		return resultat;
	}

	public Integer[] getMetricsDownIDs(int appId, int metricID) {
		return MetricImpl.getMetricsDownIDs(appId, metricID, mInteractManager);
	}

	public Integer[] getMetricsTopIDs(int appID, int metricID) {
		return MetricImpl.getMetricsTopIDs(appID, metricID, mInteractManager);
	}

	public String[] getMetricsDownNames(int appID, int metricID) {
		return MetricImpl.getMetricsDownNames(appID, metricID, mInteractManager, metricsMgr);
	}

	public String[] getMetricsTopNames(int appID, int metricID) {
		return MetricImpl.getMetricsTopNames(appID, metricID, mInteractManager, metricsMgr);
	}


	public float[] getMetricsTopTargets(int appID, int metricID) {
		return MetricImpl.getMetricsTopTargets(appID, metricID, mInteractManager, mValsMgr, aTMMgr, aCIMgr, metricsMgr);
	}

	public float[] getMetricsDownTargets(int appID, int metricID) {
		return MetricImpl.getMetricsDownTargets(appID, metricID, mInteractManager, mValsMgr, aTMMgr, aCIMgr, metricsMgr);
	}

	public float[] getMetricsTopMinimum(int appID, int metricID) {
		return MetricImpl.getMetricsTopMinimum(appID, metricID, mInteractManager, mValsMgr, aTMMgr, aCIMgr, metricsMgr);
	}

	public float[] getMetricsDownMinimum(int appId, int metricID) {
		return MetricImpl.getMetricsDownMinimum(appId, metricID, mInteractManager, mValsMgr, aTMMgr, aCIMgr, metricsMgr);
	}

	public MetricWithAlert getMetricWithAlertById(int appID, int metricID,
			int alertID) {
		return mWAlertMgr.getMetricWithAlertById(appID, metricID,alertID);
	}

	public int addMetrWithAlert(MetricWithAlert alert) throws Exception {

		int mwalId = mWAlertMgr.addMetricWithAlert(alert);

		AlertCondition cond = new AlertCondition();

		cond.setAcCreationDate(new Date());
		cond.setAcMetricswithalertId(mwalId);
		cond.setAcStartDate(alert.getMaBeginDate());
		cond.setAcEndDate(alert.getMaEndDate());
		cond.setAcDefinition(alert.getMaTest());

		alConMgr.addAlertCondition(cond);

		return mwalId;
	}

	public void deleteMetrWithAlert(MetricWithAlert alert) throws Exception {
		mWAlertMgr.delMetricWithAlert(alert);
	}

	public List<MetricWithAlert> getAlertsForMetricId(int metricID) {
		return mWAlertMgr.getAlertsForMetricId(metricID);
	}

	public boolean updateMetrWithAlert(MetricWithAlert alert) throws Exception {
		return mWAlertMgr.updateMetricWithAlert(alert);
	}

	public List<MetricWithAlert> getMetricWithAlerts() {
		return mWAlertMgr.getMetricWithAlerts();
	}

	public List<Action> getActions() {
		return actMgr.getActions();
	}

	public List<FmRole> getRoles() {
		return rolMgr.getRoles();
	}

	public List<Integer> getUserIdsForRoleId(int roleId, int userId) {

		List<Integer> res = new ArrayList<Integer>();

		List<Integer> allowedUserIds = new ArrayList<Integer>();

		List<FmUser> allowedUser = getAllowedUserForUser(userId);

		for (FmUser fmUser : allowedUser){
			allowedUserIds.add(fmUser.getId());
		}		

		List<Integer> tmp = usrRolMgr.getUserIdsForRoleId(roleId);
		for(Integer rawUserId : tmp){

			if(allowedUserIds.contains(rawUserId))
				res.add(rawUserId);
		}

		return res;
	}

	public int addAlert(Alert alert) throws Exception {
		return alMgr.addAlert(alert);
	}

	public void deleteMetrWithAlert(Alert alert) throws Exception {
		alMgr.delAlert(alert);
	}

	public boolean updateAlert(Alert alert) throws Exception {
		alMgr.updateAlert(alert);
		return true;
	}

	public Application getApplicationById(int appliId) {
		Application app = ApplicationImpl.getApplicationById(appliId, appMgr); 
		if(app != null) {
			List<ApplicationHierarchy> hierarchies = appHieraMgr.findAll();
			
			//find child applications
			for(ApplicationHierarchy hiera : hierarchies) {
				if(hiera.getParentId().intValue() == app.getId()) {
					app.addChild(getApplicationById(hiera.getChildId().intValue()));
				}
				if(hiera.getChildId().intValue() == app.getId()) {
					app.setParent(ApplicationImpl.getApplicationById(hiera.getParentId(), appMgr));
				}
			}
		}
		return app;
	}

	public int addAction(Action action) throws Exception {
		return actMgr.addAction(action);
	}

	public Action getActionById(int actId) {
		return actMgr.getActionById(actId);
	}

	public void updateAction(Action action) throws Exception {
		actMgr.updateAction(action);
	}

	public AlertCondition getAlertConditionForMWAId(int mwalId) {
		return alConMgr.getAlertConditionForMWAId(mwalId);
	}

	public Alert getAlertById(Integer maAlertId) {
		return alMgr.getAlertById(maAlertId);
	}

	public int addForum(Forum forum) throws Exception {
		return forMgr.addForum(forum);
	}

	public boolean delete(Forum forum) {
		forMgr.delForum(forum);

		return forMgr.getForumById(forum.getId()) == null;
	}

	public Forum getForumById(int forumId) {
		return forMgr.getForumById(forumId);
	}

	public List<Forum> getForumForAppAndMetrId(int appId, int metrId) {
		return  forMgr.getForumForAppAndMetrId(appId, metrId);
	}

	public void updateForum(Forum forum) throws Exception {
		forMgr.updateForum(forum);
	}

	public List<Forum> getForumForAppMetrAndMetrValId(int appId, int metrId,
			int metrValId) {
		return forMgr.getForumForAppMetrAndMetrValId(appId, metrId,
				metrValId);
	}

	public MetricValues getMetricValueById(int metrValId) {

		return mValsMgr.getMetricValuesById(metrValId);
	}

	public List<Alert> getAlertForUserId(int userId) {
		return alMgr.getAlertForUserId(userId);
	}

	public MetricWithAlert getMetricWithAlertById(int ma_id) {
		return mWAlertMgr.getMetricWithAlertById(ma_id);
	}

	public List<MetricValues> getValuesForAppIdAndMetrId(int appId, int metrId) {
		List<MetricValues> values = new ArrayList<MetricValues>();

		Assoc_Application_Metric assoc = aTMMgr.getAssocForMetrIdAndAppId(metrId, appId);

		if(assoc != null){
			List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

			if(tmp != null && !tmp.isEmpty()){
				for (MetricValues metricValues : tmp) {
					values.add(metricValues);
				}
			}
		}
		return values;
	}

	public List<SubTheme> getSubThemeForThemeId(int themeId) {
		return sthmMgr.getSubThemeForThemeId(themeId);
	}

	public List<Theme> getThemeForApplId(int appId,int filter) {

		List<Theme> them = new ArrayList<Theme>();
		List<Metric> metrs = getMetricsForApplicationId(appId,filter);
		List<Integer> lstThmId = new ArrayList<Integer>();
		
		Map<Integer, Theme> thms = new HashMap<Integer, Theme>();
		List<Theme> lstTmp = thmMgr.getThemes();
		for (Theme theme : lstTmp) {
			thms.put(theme.getId(), theme);
		}
		
		for (Metric m : metrs) {
			if(m!= null && !lstThmId.contains(m.getMdGlThemeId())){

				Theme thm = thms.get(m.getMdGlThemeId());

				if(thm != null){
					them.add(thm);
				}
				lstThmId.add(m.getMdGlThemeId());
			}
		}
		return them;
	}
	
	private List<Theme> getThemeForApplId(List<Metric> metrs, Map<Integer, Theme> thms) {

		List<Theme> them = new ArrayList<Theme>();
//		List<Metric> metrs = getMetricsForApplicationId(appId,filter);
		List<Integer> lstThmId = new ArrayList<Integer>();
		
		for (Metric m : metrs) {
			if(m!= null && !lstThmId.contains(m.getMdGlThemeId())){

				Theme thm = thms.get(m.getMdGlThemeId());

				if(thm != null){
					them.add(thm);
				}
				lstThmId.add(m.getMdGlThemeId());
			}
		}
		return them;
	}

	public List<SubTheme> getSubThemeForApplIdAndThemeId(int appId, int thmId,int filter) {

//		List<SubTheme> sthem = new ArrayList<SubTheme>();
		List<Metric> metrs = getMetricsForApplicationId(appId, filter);
//		List<Integer> lstsThmId = new ArrayList<Integer>();

		Map<Integer, SubTheme> sthms = new HashMap<Integer, SubTheme>();
		List<SubTheme> lstSthm = sthmMgr.getSubThemeForThemeId(thmId);
		for (SubTheme subTheme : lstSthm) {
			sthms.put(subTheme.getId(), subTheme);
		}
		
		Map<Integer, Theme> thms = new HashMap<Integer, Theme>();
		List<Theme> lstTmp = thmMgr.getThemes();
		for (Theme theme : lstTmp) {
			thms.put(theme.getId(), theme);
		}

//		for (Metric m : metrs) {
//			if(m!= null && m.getMdGlThemeId() != null && m.getMdGlThemeId() == thmId 
//					&& m.getMdGlSubThemeId() != null && !lstsThmId.contains(m.getMdGlSubThemeId())){
//
//				SubTheme sthm = sthms.get(m.getMdGlSubThemeId());
//
//				if(sthm != null){
//					sthem.add(sthm);
//				}
//
//				lstsThmId.add(m.getMdGlSubThemeId());
//			}
//		}

		return getSubThemeForApplIdAndThemeId(metrs, thmId, sthms, thms);
	}

	private List<SubTheme> getSubThemeForApplIdAndThemeId(List<Metric> metrs, int thmId, Map<Integer, SubTheme> sthms,Map<Integer, Theme> thms) {

		List<SubTheme> sthem = new ArrayList<SubTheme>();
		
		List<Integer> lstsThmId = new ArrayList<Integer>();

		for (Metric m : metrs) {
			if(m!= null && m.getMdGlThemeId() != null && m.getMdGlThemeId() == thmId 
					&& m.getMdGlSubThemeId() != null && !lstsThmId.contains(m.getMdGlSubThemeId())){

				SubTheme sthm = sthms.get(m.getMdGlSubThemeId());

				if(sthm != null){
					sthem.add(sthm);
				}

				lstsThmId.add(m.getMdGlSubThemeId());
			}
		}

		return sthem;
	}

	public List<Metric> getMetricsForAppAndTheme(int appId, int themeId) {
		return MetricImpl.getMetricsForAppAndTheme(appId, themeId, metricsMgr, aTMMgr);
	}

	public List<Metric> getMetricsForAppThemeAndSubTheme(int appId, int thmId,
			int sthmId) {
		return MetricImpl.getMetricsForAppThemeAndSubTheme(appId, thmId, sthmId, metricsMgr, aTMMgr);
	}

	public List<WatchList> getWatchlistByUserId(int userId) {
		return wlstMgr.getWatchlistByUserId(userId);
	}

	public int addUserWatchList(WatchList userWatchList) throws Exception {
		return wlstMgr.addWatchList(userWatchList);
	}

	public boolean deleteUserWatchList(WatchList del) {
		return wlstMgr.delWatchList(del);
	}

	public WatchList getWatchlistById(int mw_id) {
		return wlstMgr.getWatchListById(mw_id);
	}

	public int addUserPreference(UserPreference pref) throws Exception {
		return usrPrefMgr.addUserPreference(pref);
	}

	public boolean deleteUserPreference(UserPreference pref) {
		usrPrefMgr.delUserPreference(pref);
		return usrPrefMgr.getUserPreferenceById(pref.getId()) == null;
	}

	public UserPreference getUserPreferenceById(int mp_id) {
		return usrPrefMgr.getUserPreferenceById(mp_id);
	}

	public int addUserAlertePreference(UserAlertePreference usrAlPref) throws Exception {
		return usrAlPrefMgr.addUserAlertePreference(usrAlPref);
	}

	public Theme getThemeById(int thmId) {
		return thmMgr.getThemeById(thmId);
	}

	public List<UserPreference> getUserPrefForUserId(int userPrefId) {
		return usrPrefMgr.getUserPreferenceByUserId(userPrefId);
	}

	public List<WatchList> getWatchlistForUserIdAppIdAndMetrId(int userId,
			int appId, int metrId) {
		return wlstMgr.getWatchlistForUserIdAppIdAndMetrId(userId,appId, metrId);
	}

	public int addMetricValue(MetricValues val) throws Exception {
		return MetricImpl.addMetricValue(val, mValsMgr, metricsMgr, aTMMgr, aCIMgr);
	}


	public boolean deleteValueWithId(int valueID) {
		return mValsMgr.deleteById(valueID);
	}
	//FIXME
	public int getAssociationIdForMetrIdAndAppId(int metricID, int applicationID) {		
		
		
		
		
		
		
		return aTMMgr.getAssocIdForMetrIdAndAppId(metricID,applicationID);
	}

	public void updateMetricValue(MetricValues val) throws Exception {
		//mValsMgr.updateMetricValues(val);
		MetricImpl.updateMetricValues(val, mValsMgr, metricsMgr, aTMMgr, aCIMgr);
	}

	public List<Application> getAllowedApplicationForUser(int userId) {
		return ApplicationImpl.getAllowedApplicationForUser(userId, appMgr, appGrpMgr, isSuperAdmin(userId), grpMgr, usersgrpsMgr);
	}

	public List<Metric> getAllowedMetricsForUser(int userId,int filter) {
		return MetricImpl.getAllowedMetricsForUser(userId, filter, metricsMgr);
	}

	public List<Theme> getAllowedThemeForUser(int userId) {
		List<Theme> res = new ArrayList<Theme>();

		if(isSuperAdmin(userId)){
			res = getThemes();
		}else{

			List<Integer> thmIds = new ArrayList<Integer>();
			List<Metric> tmp  = getAllowedMetricsForUser(userId,IConstants.METRIC_FILTER_ANY);

			for (Metric met : tmp) {
				if(!thmIds.contains(met.getMdGlThemeId())){
					thmIds.add(met.getMdGlThemeId());
				}
			}

			for (Integer id : thmIds) {
				Theme t = thmMgr.getThemeById(id);
				if(t != null)
					res.add(t);
			}
		}
		return res;
	}

	public int addUserAction(UserAction op) throws Exception {		
		return usrActMgr.addUserAction(op);
	}

	public List<UserAction> getUserActionsForUser(int userId) {		
		return usrActMgr.getUserActionByUserId(userId);
	}

	public List<Alert> getAlerts() {
		return alMgr.getAlerts();
	}

	public List<MetricWithAlert> getAlertsForMetricAndAppId(int metricId,
			int appId) {
		return mWAlertMgr.getAlertsForMetricAndAppId( metricId,appId);
	}

	public boolean deleteUserAction(UserAction del) {
		return usrActMgr.delUserAction(del);
	}

	public boolean deleteUserAlertePreference(UserAlertePreference del) {
		return usrAlPrefMgr.delUserAlertePreference(del);
	}

	public UserAction getUserActionById(int id) {
		return usrActMgr.getUserActionById(id);
	}

	public UserAlertePreference getUserAlertePreferenceById(int id) {
		return usrAlPrefMgr.getUserAlertePreferenceById(id);
	}

	public MetricValues getLastValueForAppIdAndMetrId(int applid, int metrId) {
		int assocId = aTMMgr.getAssocIdForMetrIdAndAppId(metrId, applid);
		return mValsMgr.getLastValueForAssocId(assocId);
	}

	public Unit getUnitById(int unitId) {

		return uMgr.getUnitById(unitId);
	}

	public boolean deleteUnit(Unit unit) {

		return uMgr.delUnit(unit);
	}

	public boolean updateUnit(Unit unit) {

		try {
			return uMgr.updateUnit(unit);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addRole(FmRole role) {

		try {
			return rolMgr.addRole(role);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public FmRole getRoleById(int roleid) {

		return rolMgr.getRoleById(roleid);
	}

	public List<Unit> getUnits() {
		return uMgr.getUnits();
	}

	public int addUnit(Unit u) {

		try {
			return uMgr.addUnit(u);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<Theme> getThemes() {

		return thmMgr.getThemes();
	}

	public boolean updateRole(FmRole role) {

		try {
			return rolMgr.updateRole(role);
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}
	}

	public int addUserRole(FmUser_Roles userRole) {

		try {
			return usrRolMgr.addUsersRoles(userRole);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean deleteUserRoleByUsrIdAndRoleId(int userId, int roleId) {

		return usrRolMgr.deleteUserRoleByUserAndRoleIds(userId, roleId);
	}

	public boolean deleteGroupById(int id) {		
		return GroupImpl.deleteGroupById(id, grpMgr);
	}
	
	public boolean deleteGroup(Group group, boolean cascade) {
		if (!cascade) {
			grpMgr.deleteGroup(group);
			List<FmUser_Groups> lug = usersgrpsMgr.getByGroupId(group.getId());
			for (FmUser_Groups ug : lug) {
				usersgrpsMgr.delUsersGroups(ug);
			}
			
			List<FmApplication_Groups> lag = appGrpMgr.getByGroupId(group.getId());
			for (FmApplication_Groups ag : lag) {
				appGrpMgr.delApplicationsGroups(ag);
			}
		}
		else {
			List<FmUser_Groups> lug = usersgrpsMgr.getByGroupId(group.getId());
			for (FmUser_Groups ug : lug) {
				userMgr.deleteUserById(ug.getUserId());
			}
			for (FmUser_Groups ug : lug) {
				usersgrpsMgr.delUsersGroups(ug);
			}
			
			List<FmApplication_Groups> lag = appGrpMgr.getByGroupId(group.getId());
			for (FmApplication_Groups ag : lag) {
				Application app = appMgr.getApplicationById(ag.getApplicationId());
				
				if (app != null) {
					aTMMgr.deleteAssocForAppId(app.getId(), mValsMgr);
					
					appMgr.delApplication(app);
				}
			}
			for (FmApplication_Groups ag : lag) {
				appGrpMgr.delApplicationsGroups(ag);
				
			}
			grpMgr.deleteGroup(group);
		}
		
		return true;
	}

	public boolean deleteTheme(Theme thm) {
		return thmMgr.delTheme(thm);
	}

	public boolean deleteUserById(int id) throws Exception{
		List<Observatoire> obs = obsMgr.findObservatoireByUserId(id);
		if  (obs != null && !obs.isEmpty()) {
			throw new Exception("Impossible de supprimer l'utilisateur il est rattach� � un observatoire");
		}
		
		List<FmUser_Roles> ur = usrRolMgr.getByUserId(id);
		
		for (FmUser_Roles fur : ur) {
			usrRolMgr.delUsersRoles(fur);
		}
		
		return UserImpl.deleteUserById(id, userMgr, usersgrpsMgr);
	}

	public boolean updateTheme(Theme thm) {
		try {
			return thmMgr.updateTheme(thm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<FmUser> getAllowedUserForUser(int userId) {
		return UserImpl.getAllowedUserForUser(userId, isSuperAdmin(userId), userMgr, grpMgr, usersgrpsMgr);
	}

	public int addTypeCollectivite(TypeCollectivite sthm) {
		try {
			return tcolMgr.addTypeCollectivite(sthm);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	public TypeCollectivite getTypeCollectiviteById(int id){

		return tcolMgr.getTypeCollectiviteById(id);
	}

	public TypeCollectivite getTypeCollectiviteByName(String name) {

		return tcolMgr.getTypeCollectiviteByName(name);
	}


	public List<TypeCollectivite> getTypeCollectivites() {

		return tcolMgr.getTypeCollectivites();
	}

	public boolean deleteTypeCollectivite(TypeCollectivite col) {
		return tcolMgr.delTypeCollectivite(col);

	}


	public boolean updateTypeCollectivite(TypeCollectivite col){
		try {
			return tcolMgr.updateTypeCollectivite(col);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public Unit getUnitByName(String unitName) {

		return uMgr.getUnitByName(unitName);
	}

	public List<Integer> getUserIdsForGroup(int groupId, int userId) {

		return UserImpl.getUserIdsForGroup(groupId, userId, isSuperAdmin(userId), userMgr, grpMgr, usersgrpsMgr);
	}

	public int addTypeOrganisation(TypeOrganisation d){
		try {
			return toMgr.addTypeOrganisation(d);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public TypeOrganisation getTypeOrganisationById(int id) throws Exception {
		return toMgr.getTypeOrganisationById(id);
	}

	public TypeOrganisation getTypeOrganisationByName(String name)
	throws Exception {
		return toMgr.getTypeOrganisationByName(name);
	}

	public List<TypeOrganisation> getTypeOrganisations() throws Exception {
		return toMgr.getTypeOrganisations();
	}

	public boolean deleteTypeOrganisation(TypeOrganisation to) {
		return toMgr.delTypeOrganisation(to);
	}

	public boolean updateTypeOrganisation(TypeOrganisation to){
		try {
			return toMgr.updateTypeOrganisation(to);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteOrganisation(Organisation o) {
		return oMgr.delOrganisation(o);
	}

	public boolean updateOrganisation(Organisation o){
		try {
			return oMgr.updateOrganisation(o);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addOrganisation(Organisation org){
		try {
			return oMgr.addOrganisation(org);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<Organisation> getOrganisations() throws Exception {
		return oMgr.getOrganisations();
	}

	public Organisation getOrganisationById(int id) throws Exception {
		return  oMgr.getOrganisationById(id);
	}

	public Organisation getOrganisationByName(String name) throws Exception {
		return oMgr.getOrganisationByName(name);
	}

	public int addTheme(Theme thm) throws Exception {		
		return thmMgr.addTheme(thm);
	}

	public int addTypeDecompTerritoire(TypeDecompTerritoire sthm){
		try {
			return tdtMgr.addTypeDecompTerritoire(sthm);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int addTypeTerritoire(TypeTerritoire sthm){
		try {
			return tterMgr.addTypeTerritoire(sthm);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean deleteTypeTerritoire(TypeTerritoire d) {
		return tterMgr.delTypeTerritoire(d);
	}

	public TypeDecompTerritoire getTypeDecompTerritoireById(int id)
	throws Exception {
		return tdtMgr.getTypeDecompTerritoireById(id);
	}

	public TypeDecompTerritoire getTypeDecompTerritoireByName(String name) {
		return tdtMgr.getTypeDecompTerritoireByName(name);
	}

	public List<TypeDecompTerritoire> getTypeDecompTerritoires()
	throws Exception {
		return tdtMgr.getTypeDecompTerritoires();
	}

	public TypeTerritoire getTypeTerritoireById(int id) throws Exception {
		return tterMgr.getTypeTerritoireById(id);
	}

	public TypeTerritoire getTypeTerritoireByName(String name) {
		return tterMgr.getTypeTerritoireByName(name);
	}

	public List<TypeTerritoire> getTypeTerritoires() throws Exception {
		return tterMgr.getTypeTerritoires();
	}

	public boolean deleteTypeDecompTerritoire(TypeDecompTerritoire d) {
		return tdtMgr.delTypeDecompTerritoire(d);
	}

	public boolean updateTypeDecompTerritoire(TypeDecompTerritoire d){
		try {
			return tdtMgr.updateTypeDecompTerritoire(d);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateTypeTerritoire(TypeTerritoire d){
		try {
			return tterMgr.updateTypeTerritoire(d);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addDecompoTerritoire(Decompo_Territoire dec){
		try {
			return decTerMgr.addDecompo_Territoire(dec);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean deleteDecompForParentAndChildId(int parentId, int childId) {
		return decTerMgr.delDecompo_Territoire(parentId, childId );
	}

	public List<Integer> getDecompoTerritoireForAssoc_Terr_TyyDecTerId( int parentAssocTerrTypDecTerrId){
		return decTerMgr.getChildsIdForAssoc_Terr_TypDecTerId(parentAssocTerrTypDecTerrId);
	}

	public boolean deleteApplinksForAppId(int applId) {
//		aTMMgr.deleteAssocForAppId(applId, mValsMgr);

		appGrpMgr.deleteApplicationGroupByApplicId(applId);

		List<Decompo_Territoire> tmp = decTerMgr.getByChild_App_ID(applId);
		if(tmp != null && !tmp.isEmpty()){
			for(Decompo_Territoire dec : tmp){
				if(dec != null){
					decTerMgr.delDecompo_Territoire(dec);
				}
			}
		}

		List<Assoc_Terr_Type_Dec_Terr> tmp2 = aTTDTMgr.getByParent_App_ID(applId);

		if(tmp2 != null && !tmp2.isEmpty()){
			for(Assoc_Terr_Type_Dec_Terr assoc : tmp2){
				if(assoc != null){

					Decompo_Territoire dec = decTerMgr.getDecompo_TerritoireForParentAndChildId(assoc.getId(), applId);

					if(dec != null){
						decTerMgr.delDecompo_Territoire(dec);
					}
					aTTDTMgr.delAssoc_Terr_Type_Dec_TerrById(assoc.getId());
				}
			}
		}

		return true;
	}

	public int addAssoc_Terr_TypeDecom(Assoc_Terr_Type_Dec_Terr dec){
		try {
			return aTTDTMgr.addAssoc_Terr_TypeDecom(dec);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean deleteAssoc_Terr_TypeDecom(int parentId, int childId) {
		return aTTDTMgr.delAssoc_Terr_Type_Dec_Terr(parentId, childId);
	}

	public List<Assoc_Terr_Type_Dec_Terr> getAssoc_Terr_Type_Dec_Terrs() {
		return aTTDTMgr.getAssoc_Terr_Type_Dec_Terrs();
	}

	public List<Integer> getTypeDecTerrIDsForAppId(int appliId) {		
		return aTTDTMgr.getChildsIdForAppId(appliId);
	}

	public List<Integer> getDecompoTerritoireForAppIdAndTypeDecTerrId(
			int appliId, int typeDecTerrId) {
		List<Integer> res = new ArrayList<Integer>();
		List<Assoc_Terr_Type_Dec_Terr> lstTmp = aTTDTMgr.getByParent_App_ID(appliId);
		for (Assoc_Terr_Type_Dec_Terr ass : lstTmp) {			
			if(ass.getChild_TypeDecTerr_ID() == typeDecTerrId){
				res = decTerMgr.getChildsIdForAssoc_Terr_TypDecTerId(ass.getId());
				break;
			}
		}
		return res;
	}

	public List<Application> getApplicationForTypeDecomTerrId(int typDecId) {
		return ApplicationImpl.getApplicationForTypeDecomTerrId(typDecId, appMgr, aTTDTMgr);
	}

	public List<Application> getApplicationForTypeTerrId(int typTerId) {
		return ApplicationImpl.getApplicationForTypeTerrId(typTerId, appMgr);
	}

	public Theme getThemeByName(String name) {
		return thmMgr.getThemeByName(name);
	}

	public boolean deleteRole(FmRole role) {
		return rolMgr.delRole(role);
	}

	public List<FmUser> getUserForRoleId(int roleId) {
		return UserImpl.getUserForRoleId(roleId, userMgr, usrRolMgr);
	}

	public boolean isRoleSuperAdmin(int roleId) {
		return rolMgr.isRoleSuperAdmin(roleId);
	}

	public TypeMetric getTypeMetricByName(String name) {		
		return tmMgr.getTypeMetricByName(name);
	}

	public boolean deleteTypeMetric(TypeMetric col) {
		return tmMgr.delTypeMetric(col);
	}

	public boolean updateTypeMetric(TypeMetric col) {

		try {
			return tmMgr.updateTypeMetric(col);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addTypeMetric(TypeMetric u) {
		try {
			return tmMgr.addTypeMetric(u);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public TypeMetric getTypeMetricById(int id) {
		return tmMgr.getTypeMetricById(id);
	}

	public List<TypeMetric> getTypeMetrics() {

		return tmMgr.getTypeMetrics();
	}

	public FmUser getUserByLogin(String login) {
		return userMgr.getUserByLogin(login);
	}

	public boolean deleteAssoc_Application_Metric(Assoc_Application_Metric assoc) {

		List<AssocApplicationAssocAppMetric> assoss = assoAppAssocMgr.findByAssoId(assoc.getId());
		for(AssocApplicationAssocAppMetric as : assoss) {
			assoAppAssocMgr.delete(as);
		}
		
		return aTMMgr.delAssocTerritoireMetric(assoc, mValsMgr);
	}

	public List<Assoc_Application_Metric> getAssoc_Application_MetricByMetricId(int metricId) {
		List<Assoc_Application_Metric> result = aTMMgr.getAssoc_Application_MetricByMetrId(metricId);
		for(Assoc_Application_Metric assoc : result) {
			assoc.setApplications(findApplicationsByAssoId(assoc.getId()));
		}
		return result;
	}

	public List<Assoc_Application_Metric> getAssoc_Application_MetricsByAppId(int appId) {
		
		Application app = getApplicationById(appId);
		List<Assoc_Application_Metric> assoc = new ArrayList<Assoc_Application_Metric>();
		
		if(app.getChildren() != null && app.getChildren().size() > 0) {
			for(Application child : app.getChildren()) {
				List<Assoc_Application_Metric> childsAssoc = getAssoc_Application_MetricsByAppId(child.getId());
				if(childsAssoc != null && childsAssoc.size() > 0) {
					assoc.addAll(childsAssoc);
				}
			}
		}
		
		else {
			List<AssocApplicationAssocAppMetric> assos = assoAppAssocMgr.getByApplicationId(appId);
			for(AssocApplicationAssocAppMetric asso : assos) {
				Assoc_Application_Metric a = aTMMgr.getAssocForId(asso.getAssocId());
				assoc.add(a);
			}
		}
		
		return assoc;
//		return aTMMgr.getAssoc_Application_MetricByAppId(appId);
	}

	public boolean deleteActionById(int acId) {
		return actMgr.deleteActionById(acId);
	}

	public int addApplication(Application app) {
		int appId = ApplicationImpl.addApplication(app, appMgr);
		if(app.getParent() != null) {
			ApplicationHierarchy hiera = new ApplicationHierarchy();
			hiera.setChildId(appId);
			hiera.setParentId(app.getParent().getId());
			appHieraMgr.save(hiera);
		}
		return appId;
	}

	public int addMetric(Metric met) {
		try {
			return metricsMgr.addMetric(met);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean deleteApplicationById(int id) {
		
		Application app = getApplicationById(id);
//		if(app.getChildren() != null && app.getChildren().size() > 0) {
//			for(Application child : app.getChildren()) {
//				deleteApplicationById(child.getId());
//			}
//			appHieraMgr.deleteByParentId(app.getId());
//		}
//		
//		appHieraMgr.deleteByChildId(app.getId());
//		
//		assoAppAssocMgr.deleteForAppId(app.getId());
//		
//		appGrpMgr.deleteApplicationGroupByApplicId(app.getId());
		
		return ApplicationImpl.deleteApplicationById(id, appMgr);
	}


	public boolean deleteMetrLinksForMetrId(int metrId) {

		aTMMgr.deleteAssocForMetrId(metrId, mValsMgr);

		mInteractManager.deleteMetricInteractionForTopId(metrId);
		mInteractManager.deleteMetricInteractionForDownId(metrId);

		return true;
	}

	public boolean deleteMetricById(int metid) {
		return metricsMgr.deleteMetricById(metid);
	}

	public Application getApplicationByName(String text) {
		return ApplicationImpl.getApplicationByName(text, appMgr);
	}

	public Metric getMetricByName(String text) {
		return metricsMgr.getMetricByName( text);
	}

	public List<Theme> getParentThemeForThemeId(int id) {

		List<Theme> res  = new ArrayList<Theme>();

		List<Integer> dashParentIds  = new ArrayList<Integer>();

		for (DashboardRelation dr : drMgr.getDashboardRelations()) {			
			if(dr.getChild_Dash_Id() == id && !dashParentIds.contains(dr.getParent_Dash_Id())){
				dashParentIds.add(dr.getParent_Dash_Id());
			}
		}

		for (Integer dashParentId : dashParentIds) {
			Theme t = getThemeById(dashParentId);
			if(t != null)
				res.add(t);
		}
		return res;
	}

	public List<Theme> getThemesForParentThemeId(int id) {
		List<Theme> res  = new ArrayList<Theme>();
		List<Integer> dashChildIds  = new ArrayList<Integer>();

		for (DashboardRelation dr : drMgr.getDashboardRelations()) {			
			if(dr.getParent_Dash_Id() == id && !dashChildIds.contains(dr.getChild_Dash_Id())){
				dashChildIds.add(dr.getChild_Dash_Id());
			}
		}

		for (Integer dashParentId : dashChildIds) {
			Theme t = getThemeById(dashParentId);
			if(t != null)
				res.add(t);
		}
		return res;
	}

	public boolean updateApplication(Application application) {
		return ApplicationImpl.updateApplication(application, appMgr);
	}

	public boolean updateMetric(Metric metricCopy) {
		try {
			return metricsMgr.updateMetric(metricCopy);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addDashboardRelation(DashboardRelation d) {

		try {
			return drMgr.addDashboardRelation(d);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean deleteDashboardRelation(int childDashId, int parentDashId) {		

		for (DashboardRelation dr : drMgr.getDashboardRelations()) {			
			if(dr.getParent_Dash_Id() == parentDashId && dr.getChild_Dash_Id()== childDashId){
				drMgr.delDashboardRelation(dr);
			}
		}

		return true;
	}

	public boolean delete(Alert al) {

		return alMgr.delAlert(al);
	}

	public List<MetricValues> getAllowedValuesForUser(int userId, List<String> applications, List<String> themes) {
		List<MetricValues> values = new ArrayList<MetricValues>();

		List<Application> tmp = new ArrayList<Application>();
		for (String a : applications) {
			tmp.add(getApplicationByName(a));
		}
		
		List<Metric> tmp2 = new ArrayList<Metric>();
		for (String t : themes) {
			tmp2.addAll(metricsMgr.getMetricsByTheme(t));
		}
		
		for (Application app : tmp) {
			for (Metric metric : tmp2 ) {				
				values.addAll(getValuesForAppIdAndMetrId(app.getId(), metric.getId()));
			}
		}

		return values;
	}

	public SubTheme getSubThemeById(Integer mdGlSubThemeId) {		
		return sthmMgr.getSubThemeById(mdGlSubThemeId);
	}

	public List<Alert> getAllowedAlertsForUser(int userId) {

		List<Alert> res = new ArrayList<Alert>();

		List<Integer> allowedAlertIds = new ArrayList<Integer>();

		List<Integer> tmp = getGroupIdForUser(userId);
		for (Integer gpId : tmp) {

			List<Integer> tmp2 = getAlertIdsForGroup(gpId);
			for (Integer alId : tmp2) {
				if(!allowedAlertIds.contains(alId)){
					allowedAlertIds.add(alId);
				}
			}
		}

		for (Integer id : allowedAlertIds) {
			Alert a = alMgr.getAlertById(id);
			if(a != null){
				if(isSuperAdmin(userId)|| a.getAlIsPublic()){
					res.add(a);
				}else if(!a.getAlIsPublic() && a.getAlUserId()!= null && a.getAlUserId() == userId ){
					res.add(a);
				}
			}

		}
		return res;
	}

	private List<Integer> getAlertIdsForGroup(int gpId) {

		List<Integer> res = new ArrayList<Integer>();

		List<Alert> tmp = alMgr.getAlertForGroupId(gpId);

		if(tmp != null && !tmp.isEmpty()){
			for (Alert al : tmp) {

				if(al != null )
					res.add(al.getId());

			}
		}

		return res;
	}

	@Deprecated
	public List<Alert> getAlertsForMetric(int metrId) {
		List<Alert> res = new ArrayList<Alert>();

		List<MetricWithAlert> mwals =  mWAlertMgr.getAlertsForMetricId(metrId);

		List<Integer> alIds = new ArrayList<Integer>();
		for (MetricWithAlert mwal : mwals) {

			if(!alIds.contains(mwal.getMaAlertId())){
				alIds.add(mwal.getMaAlertId());
			}
		}

		for (Integer alId : alIds) {

			Alert a = alMgr.getAlertById(alId);

			if(a != null){
//				if(isSuperAdmin(FactoryManager.getUserId())|| a.getAlIsPublic()){
//					res.add(a);
//				}else 
				if(!a.getAlIsPublic() && a.getAlUserId()!= null /*&& a.getAlUserId() == FactoryManager.getUserId()*/ ){
					res.add(a);
				}
			}
		}
		return res;
	}

	public Action getActionByName(String name) {
		return actMgr.getActionByName(name);
	}

	public List<Alert> getAlertsForActionId(int actionId) {
		return alMgr.getAlertsForActionId(actionId);
	}

	public List<Application> getApplicationsForGroup(int gpId) {
		return ApplicationImpl.getApplicationsForGroup(gpId, appGrpMgr);
	}

	public List<MetricWithAlert> getMetricWithAlertForAlertId(int alId) {
		return mWAlertMgr.getMetricWithAlertForAlertId(alId);
	}

	public boolean updateAlertCondition(AlertCondition ac) {

		try {
			return alConMgr.updateAlertCondition(ac);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<MetricValues> getObjectifsForValueWithSamePeriod(MetricValues value) {
		return MetricImpl.getObjectifsForValueWithSamePeriod(value, aTMMgr, mValsMgr, aCIMgr, metricsMgr);
	}

	public MetricValues getPreviousMetricValue(MetricValues value,String periodicity) {
		return MetricImpl.getPreviousMetricValue(value, periodicity, mValsMgr);
	}

	public List<MetricValues> getValuesForMetricAppPeridDate(int appId,int metrId, Date dper) {
		
		List<Assoc_Application_Metric> assos = getAssoMetricAppByMetricAndAppIds(appId, metrId);
		
		return MetricImpl.getValuesForMetricAppPeridDate(assos, dper, aTMMgr, mValsMgr, metricsMgr);
	}

	public MetricValues getMetricValueAtPeriod(MetricValues values, Date period) {
		return MetricImpl.getMetricValueAtPeriod(values, period, mValsMgr, aTMMgr, metricsMgr);
	}

	public int addAssoc(AssocCompteurIndicateur assoc, boolean allowMulti) {
		try {
			int id =  aCIMgr.addAssoc_Compteur_Indicateur(assoc, allowMulti);
			if(id > 0){

				MetricInteraction d = new MetricInteraction();
				d.setMiCreationDate(new Date());
				d.setMiMetricTopId(assoc.getCompt_ID());
				d.setMiMetricDownId(assoc.getIndic_ID());

				mInteractManager.addMetricInteraction(d );
			}

			return id;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	public AssocCompteurIndicateur getAssoc_Compteur_IndicateurForComptId(int comptID) {
		return aCIMgr.getAssoc_Compteur_IndicateurByComptId(comptID);
	}


	public boolean update(AssocCompteurIndicateur assoc) {
		try {

			if(aCIMgr.updateAssoc_Compteur_Indicateur(assoc)){
				MetricInteraction d = mInteractManager.getMetricInteractionForTopId(assoc.getCompt_ID());

				if(d != null){
					d.setMiMetricDownId(assoc.getIndic_ID());

					mInteractManager.updateMetricInteraction(d);
				}else{
					d = new MetricInteraction();
					d.setMiCreationDate(new Date());
					d.setMiMetricTopId(assoc.getCompt_ID());
					d.setMiMetricDownId(assoc.getIndic_ID());

					mInteractManager.addMetricInteraction(d );
				}

				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addSubTheme(SubTheme sthm) {
		try {
			return sthmMgr.addSubTheme(sthm);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean deleteSubTheme(SubTheme d) {
		return sthmMgr.delSubTheme(d);
	}

	public List<FmUser> getAllowedUsersForUserId(int userId,boolean isSuperAdmin) {
		return UserImpl.getAllowedUserForUser(userId, isSuperAdmin, userMgr, grpMgr, usersgrpsMgr);
	}


	public List<Group> getAllowedGroupsForUser(int userId,
			boolean isSuperAdmin) {
		return GroupImpl.getAllowedGroupsForUser(userId, isSuperAdmin, grpMgr, usersgrpsMgr);
	}

	public Group getGroupByName(String name) {
		return grpMgr.getGroupByName(name);
	}

	public FmRole getRoleByName(String name) {
		return rolMgr.getRoleByName(name);
	}

	public SubTheme getSubThemeByName(String name) {
		return sthmMgr.getSubThemeByName(name);
	}

	public List<SubTheme> getSubThemes() {
		return sthmMgr.getSubThemes();
	}

	public boolean isAdmin(int userId) {

		List<FmUser_Roles> tmp = usrRolMgr.getByUserId(userId);
		for (FmUser_Roles ur : tmp) {

			FmRole role = rolMgr.getRoleById(ur.getRoleId());
			if(role != null && role.getGrants().contains("A")){
				return true;
			}
		}
		return false;
	}

	public boolean updateSubTheme(SubTheme d) {
		try {
			return sthmMgr.updateSubTheme(d);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int updateUser(FmUser user, boolean isSuperAdmin) {
		return UserImpl.updateUser(user, isSuperAdmin, userMgr);
	}

	public boolean isMetricHasValues(int metricId) {
		return MetricImpl.isMetricHasValues(metricId, mValsMgr, aTMMgr);
	}

	public List<Metric> getCompteurs() {
		return metricsMgr.getCompteurs();
	}

	public int addMetricInteraction(MetricInteraction d) throws Exception {
		return mInteractManager.addMetricInteraction(d);
	}

	public List<Metric> getIndicateurs() {
		return metricsMgr.getIndicateurs();
	}

	public int addDatasource(FMDatasource d) throws Exception {
		return dsMgr.addFMDatasource(d);
	}

	public boolean deleteDatasource(FMDatasource ds) {

		return dsMgr.delFMDatasource(ds);
	}

	public FMDatasource getDatasourceById(int id) {
		return dsMgr.getFMDatasourceById(id);
	}

	public FMDatasource getDatasourceByName(String name) {
		return dsMgr.getFMDatasourceByName(name);
	}

	public List<FMDatasource> getDatasources() {
		return dsMgr.getFMDatasources();
	}

	public boolean updateDatasource(FMDatasource ds) {
		try {
			return dsMgr.updateFMDatasource(ds);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public MetricValues getObjectifForMetricForPeriode(int appId, int comptId,
			Date date) {
		return MetricImpl.getObjectifForMetricForPeriode(appId, comptId, date, mValsMgr, aTMMgr, aCIMgr, metricsMgr);
	}

	public MetricValues getValuesForAssocIdPeridDate(int mvGlAssoc_ID,
			Date mvPeriodDate) {
		return MetricImpl.getValuesForAssocIdPeridDate(mvGlAssoc_ID, mvPeriodDate, aTMMgr, mValsMgr, metricsMgr);
	}

	public MetricValues getObjectifsForValueAndPeriod(MetricValues val,
			Date mvPeriodDate) {

		return MetricImpl.getObjectifsForValueWithSamePeriod(val,mvPeriodDate, aTMMgr, mValsMgr, aCIMgr, metricsMgr);
	}

	public List<Metric> getMetricsForType(String calculationType) {
		return metricsMgr.getMetricsForType(calculationType);
	}

	public List<MetricValues> getValuesForAssocId(int assocId) {
		List<MetricValues> values = new ArrayList<MetricValues>();

		Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(assocId);

		if(assoc != null){
			List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

			if(tmp != null && !tmp.isEmpty()){
				for (MetricValues metricValues : tmp) {
					values.add(metricValues);
				}
			}
		}
		return values;
	}

	public List<TreeGroupsAppsThm> getTreeGroupsAppsThmForUser(int userId) {

		List<TreeGroupsAppsThm> res = new ArrayList<TreeGroupsAppsThm>();

		List<Group> tmp = getGroupsForUser(userId);

		Map<Integer, Theme> thms = new HashMap<Integer, Theme>();
		List<Theme> lstTmp = thmMgr.getThemes();
		for (Theme theme : lstTmp) {
			thms.put(theme.getId(), theme);
		}
		
		Map<Integer, SubTheme> sthms = new HashMap<Integer, SubTheme>();
		List<SubTheme> lstSthm = sthmMgr.getSubThemes();
		for (SubTheme subTheme : lstSthm) {
			sthms.put(subTheme.getId(), subTheme);
		}
		
		Map<Integer, List<Metric>> metrs = new HashMap<Integer, List<Metric>>();
		
		for (Group g : tmp) {

			TreeGroupsAppsThm tga = new TreeGroupsAppsThm();
			tga.setGroup(g);

			List<Application> tmp2 = getApplicationsForGroup(g.getId());

			for (Application tmpApp : tmp2) {

				TreeAppsThm ta = new TreeAppsThm();				
				ta.setApp(tmpApp);

				if(metrs.get(tmpApp.getId()) == null){
					metrs.put(tmpApp.getId(), getMetricsForApplicationId(tmpApp.getId(), IConstants.METRIC_FILTER_COMPTEUR));
				}
				
				List<Theme> lstThms = getThemeForApplId(metrs.get(tmpApp.getId()), thms);
				
				for (Theme thm : lstThms) {
					TreeThm th = new TreeThm();
					th.setTheme(thm);
					
					List<SubTheme> lstStm = getSubThemeForApplIdAndThemeId(metrs.get(tmpApp.getId()),thm.getId(),sthms,thms);
					
					th.setSthms(lstStm);
					
					ta.addTreeThm(thm.getId(), th);
				}

				tga.addTreeApp(ta.getId(), ta);
			}
			
			res.add(tga);

		}
		return res;
	}

	public Group getGroupForApplication(int appId) {
		return GroupImpl.getGroupForApplication(appId, appGrpMgr);
	}

	public boolean deleteMetricInteraction(MetricInteraction d) {
		return mInteractManager.delMetricInteraction(d);
	}

	public List<MetricInteraction> getMetricInteractionsForMetrId(int mid) {
		return mInteractManager.getMetricInteractionsForMetrId(mid);
	}

	public boolean authentify(String username, String password, IManager manager) {
		return AuthentificationHelper.getInstance().authentify(username, password, manager);
	}

	public List<Observatoire> getObservatoiresForUserId(int userId) {
		return obsMgr.findObservatoireByUserId(userId);
	}
	
	public Observatoire getObservatoireById(int id) {
		return obsMgr.getObservatoireById(id);
	}
	
	public Observatoire getObservatoireByName(String name) {
		return obsMgr.getObservatoireByName(name);
	}
	
	public void update(Observatoire observatoire) {
		obsMgr.update(observatoire);
	}
	
	public void deleteObservatoire(Observatoire obs) throws UserObservatoireException, ThemeObservatoireException {
		obsMgr.delete(obs);
	}
	

	public int addObservatoire(Observatoire obs) {
		return obsMgr.add(obs);
	}

	public int associateThemeObservatoire(Theme theme, Observatoire observatoire) {
		ObservatoiresThemes ot = new ObservatoiresThemes();
		ot.setObsId(observatoire.getId());
		ot.setThemeId(theme.getId());
		return obsMgr.addObservatoiresThemes(ot);
	}

	public int associateUserObservatoire(FmUser user, Observatoire observatoire) throws Exception {
		ObservatoiresUsers ou = new ObservatoiresUsers();
		ou.setObsId(observatoire.getId());
		ou.setUserId(user.getId());
		return obsMgr.addObservatoiresUsers(ou);
	}
	
	public List<FmUser> getUsersForObservatoire(int observatoireId) {
		List<FmUser> res = new ArrayList<FmUser>();
		List<ObservatoiresUsers> l = obsMgr.getObservatoirsUsersByObsId(observatoireId);
		for (ObservatoiresUsers ou : l) {
			res.add(userMgr.getUserById(ou.getUserId()));
		}
		
		return res;
	}
	

	public void removeThemeFromObservatoire(Theme theme, Observatoire observatoire) {
		obsMgr.removeObservatoiresThemes(theme.getId(), observatoire.getId());
	}

	public void removeUserFromObservatoire(FmUser user,	Observatoire observatoire) {
		obsMgr.removeObservatoiresUsers(user.getId(), observatoire.getId());
	}

	public void updateObservatoire(Observatoire obs) {
		obsMgr.update(obs);	
	}
	
	public List<Observatoire> getObservatoires() {
		return obsMgr.getObservatoires();
	}

	public List<Theme> getThemesForObservatoireId(int obsId) {
		List<ObservatoiresThemes> assoThemes = obsMgr.getObservatoiresThemeByObsId(obsId);
		List<Theme> themes = new ArrayList<Theme>();
		for(ObservatoiresThemes t : assoThemes) {
			Theme theme = getThemeById(t.getThemeId());
			themes.add(theme);
		}
		return themes;
	}

	public List<Metric> getMetricsByThemeIdAndPeriode(int themeId, String periode) {
		return metricsMgr.getMetricsByThemeIdAndPeriode(themeId, periode);
	}
	
	public List<Metric> getCompteursByThemeIdAndPeriode(int themeId, String periode) {
		return metricsMgr.getCompteursByThemeIdAndPeriode(themeId, periode);
	}
	
	public List<Metric> getIndicateursByThemeIdAndPeriode(int themeId, String periode) {
		return metricsMgr.getIndicateursByThemeIdAndPeriode(themeId, periode);
	}

	public List<MetricValues> getValuesForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId, String periode, Date date) {
		List<Assoc_Application_Metric> assos = getAssociationsIdForMetricsApplications(metricsId, applicationsId);
		List<Integer> assoIds = new ArrayList<Integer>();
		for(Assoc_Application_Metric a : assos) {
			assoIds.add(a.getId());
		}
//		return getMetricsValuesForAssoId(assoIds);
		return mValsMgr.getMetricsValuesForAssoIdPeriodeDate(assoIds,periode,date);
	}

	public List<MetricValues> getMetricsValuesForAssoId(List<Integer> assoIds) {
		return mValsMgr.getValuesForAssocId(assoIds);
	}

	public List<Assoc_Application_Metric> getAssociationsIdForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId) {
		List<Assoc_Application_Metric> assocs = aTMMgr.getAssoMetricByMetricsIds(metricsId); 
		
		List<Assoc_Application_Metric> res = new ArrayList<Assoc_Application_Metric>();
		
		LOOK:for(Assoc_Application_Metric asso : assocs) {
			
			List<Application> apps = findApplicationsByAssoId(asso.getId());
			for(Application app : apps) {
				if(!applicationsId.contains(app.getId())) {
					continue LOOK;
				}
			}
			
			if(apps != null && apps.size() > 0) {
			
				asso.setApplications(apps);
				res.add(asso);
			}
		}
		
		return res; 
	}

	public List<Application> findApplicationsByAssoId(int id) {
		
		List<AssocApplicationAssocAppMetric> assos = assoAppAssocMgr.getByAssoId(id);
		
		List<Application> apps = new ArrayList<Application>();
		for(AssocApplicationAssocAppMetric asso : assos) {
			apps.add(getApplicationById(asso.getApplicationId()));
		}
		
		return apps;
	}
	
	/**
	 * Return all the possible applications for an assoId (even parent applications)
	 * @param id
	 * @return
	 */
	public List<Application> findAllApplicationByAssoId(int id) {
		List<AssocApplicationAssocAppMetric> assos = assoAppAssocMgr.getByAssoId(id);
		
		List<Application> apps = new ArrayList<Application>();
		for(AssocApplicationAssocAppMetric asso : assos) {
			Application app = getApplicationById(asso.getApplicationId());
			apps.add(app);
			if(app.getParent() != null) {
				apps.add(app.getParent());
				app = app.getParent();
				while(app.getParent() != null) {
					apps.add(app.getParent());
					app = app.getParent();
				}
			}
		}
		
		Collections.sort(apps, new Comparator<Application>() {
			@Override
			public int compare(Application o1, Application o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return apps;
	}

	public void insertUpdateDeleteValues(List<MetricValues> insertValues, List<MetricValues> deleteValues, List<MetricValues> updateValues) {
		if(insertValues != null && insertValues.size() > 0) {
			mValsMgr.insertValues(insertValues);
		}
		if(deleteValues != null && deleteValues.size() > 0) {
			mValsMgr.deleteValues(deleteValues);
		}
		if(updateValues != null && updateValues.size() > 0) {
			mValsMgr.insertValues(updateValues);
		}
		
	}

	public List<Metric> getCompteursByObservatoires(String observatoire) {
		return metricsMgr.getCompteursByObservatoires(observatoire);
	}
	
	public List<Metric> getIndicateursByObservatoires(String observatoire) {
		return metricsMgr.getIndicateursByObservatoires(observatoire);
	}

	public List<Metric> getNotAssociatedIndicateur(Metric metric) {
		return metricsMgr.getNotAssociatedIndicateur(metric);
	}

	public List<Metric> getAssciatedIndicateur(Metric metric) {
		return metricsMgr.getAssciatedIndicateur(metric.getId());
	}

	public List<Metric> getNotAssociatedCompteur(Metric metric, int observatoireId) {
		return metricsMgr.getNotAssociatedCompteur(metric, observatoireId);
	}

	public List<Metric> getAssciatedCompteur(Metric metric) {
		return metricsMgr.getAssciatedCompteur(metric.getId());
	}
	
	public void delete(AssocCompteurIndicateur assoc) {
		aCIMgr.deleteAssocForCompId(assoc);
	}

	public Date getPreviousDateByAssoId(int assoId) {
		return mValsMgr.getPreviousDateByAssoId(assoId);
	}

	public Metric getMetricByIdAssoId(Integer mvGlAssoc_ID) {
		return metricsMgr.getMetricByIdAssoId(mvGlAssoc_ID);
	}
	
	public List<Metric> getMetricsBySubThemeId(int subThemeId) {
		return metricsMgr.getMetricsBySubThemeId(subThemeId);
	}

	public List<Metric> getMetricsByThemeId(int themeId) {
		return metricsMgr.getMetricsByThemeId(themeId);
	}
	
	public Metric getIndicateurByCompteurId(int id) {
		AssocCompteurIndicateur assoCptInd = aCIMgr.getAssoc_Compteur_IndicateurByComptId(id);
		if(assoCptInd != null) {
			return metricsMgr.getMetricById(assoCptInd.getIndic_ID());
		}
		return null;
	}
	
	public List<MetricValues> getFullMetricValues(Metric compteur, Application application) throws Exception{
		Metric indicateur = getIndicateurByCompteurId(compteur.getId());
		
		if (indicateur == null){
			throw new Exception("No Indicateur match to the Compteur with id = " + compteur.getId());
		}
		Integer tolerance =null;
		
		try{
			tolerance = Integer.parseInt(indicateur.getMdCustom1());
		}catch(Exception ex){
			ex.printStackTrace();
			tolerance = 0;
		}
		
		List<Assoc_Application_Metric> assocsCpt = getAssoMetricAppByMetricAndAppIds(application.getId(), compteur.getId());
		List<Assoc_Application_Metric> assocsInd = getAssoMetricAppByMetricAndAppIds(application.getId(), indicateur.getId());
		
		List<MetricValues> results = MetricImpl.getValuesForAssocIds(assocsCpt, mValsMgr, metricsMgr, this, application.getId(), null, null, null);
		
		List<MetricValues> indValues = MetricImpl.getValuesForAssocIds(assocsInd, mValsMgr, metricsMgr, this, application.getId(), null, null, null);
		
		for(MetricValues res : results) {
			for(MetricValues ind : indValues) {
				if(Tools.isInSamePeriod(res.getMvPeriodDate(), ind.getMvPeriodDate(), compteur.getMdCalculationTimeFrame())) {
					res.setMvGlObjectif(ind.getMvGlObjectif());
					res.setMvGlValeurSeuilMaxi(ind.getMvGlValeurSeuilMaxi());
					res.setMvGlValeurSeuilMini(ind.getMvGlValeurSeuilMini());
					res.setMvMaxValue(ind.getMvMaxValue());
					res.setMvMinValue(ind.getMvMinValue());
					res.setMvTolerance(tolerance);
					break;
				}
			}
		}
		
		return results;
	}

	public List<MetricValues> getFullMetricValues(Metric compteur, Application application, Date dateValue) throws Exception {
		Metric indicateur = getIndicateurByCompteurId(compteur.getId());
		
//		if (indicateur == null){
//			throw new Exception("No Indicateur match to the Compteur with id = " + compteur.getId());
//		}
		Integer tolerance =null;
		
		try{
			tolerance = Integer.parseInt(indicateur.getMdCustom1());
		}catch(Exception ex){
			ex.printStackTrace();
			tolerance = 0;
		}
		
		List<MetricValues> results = new ArrayList<MetricValues>();
		
		List<Assoc_Application_Metric> assocsCpt = getAssoMetricAppByMetricAndAppIds(application.getId(), compteur.getId());
		
		
		results = MetricImpl.getValuesForMetricAppPeridDate(assocsCpt, dateValue, aTMMgr, mValsMgr, metricsMgr);
		if (indicateur == null){
			List<Assoc_Application_Metric> assocsInd = getAssoMetricAppByMetricAndAppIds(application.getId(), indicateur.getId());
			List<MetricValues> indValues = MetricImpl.getValuesForMetricAppPeridDate(assocsInd, dateValue, aTMMgr, mValsMgr, metricsMgr);
			for(MetricValues res : results) {
				for(MetricValues ind : indValues) {
					if(Tools.isInSamePeriod(res.getMvPeriodDate(), ind.getMvPeriodDate(), compteur.getMdCalculationTimeFrame())) {
						res.setMvGlObjectif(ind.getMvGlObjectif());
						res.setMvGlValeurSeuilMaxi(ind.getMvGlValeurSeuilMaxi());
						res.setMvGlValeurSeuilMini(ind.getMvGlValeurSeuilMini());
						res.setMvMaxValue(ind.getMvMaxValue());
						res.setMvMinValue(ind.getMvMinValue());
						res.setMvTolerance(tolerance);
						break;
					}
				}
			}	
		}
		
		return results;
	}
	
	public List<AssocCompteurIndicateur> getAssocCompteurIndicForIndicId(int indicId) {
		return aCIMgr.getAssoc_Compteur_IndicateurByIndicId(indicId);
	}

	public List<Forum> getForumByValueId(int valueId) {
		return forMgr.getForumByValueId(valueId);
	}

	public FmUser getUserByNameAndPass(String username, String password, boolean isEncrypted) {
		return userMgr.getUserByNameAndPass(username, password, isEncrypted);
	}
	
	public boolean authentify(String username, String password, boolean isEncrypted, IManager manager) {
		return AuthentificationHelper.getInstance().authentify(username, password, isEncrypted, manager);
	}

	public boolean isCommentValidator(FmUser userFM) {
		List<FmRole> roles = getRolesForUserId(userFM.getId());
		for(FmRole role : roles) {
			if(role.getGrants().contains("V")) {
				return true;
			}
		}
		return false;
	}
	public List<Assoc_Application_Metric> getAssoMetricAppByMetricAndAppIds(int appId, int metId) {
		
		Application app = getApplicationById(appId);
		
		List<Integer> appIds = new ArrayList<Integer>();
		
		appIds = findLastChildIds(app);
		List<Integer> assocIds = new ArrayList<Integer>();
		for(Integer appI : appIds) {
		
			List<AssocApplicationAssocAppMetric> assosApps = assoAppAssocMgr.getByApplicationId(appI);
			for(AssocApplicationAssocAppMetric asso : assosApps) {
				if(!assocIds.contains(asso.getAssocId())) {
					assocIds.add(asso.getAssocId());
				}
			}
		}
		
		List<Assoc_Application_Metric> result = new ArrayList<Assoc_Application_Metric>();
		for(Integer i : assocIds) {
			Assoc_Application_Metric asso = aTMMgr.getAssoc_Territoire_MetricById(i);
			if(asso.getMetr_ID() == metId) {
				result.add(asso);
			}
		}
		
		return result;
	}

	private List<Integer> findLastChildIds(Application app) {
		if(app == null) {
			return new ArrayList<Integer>();
		}
		List<Integer> results = new ArrayList<Integer>();
		if(app.getChildren() == null || app.getChildren().size() == 0) {
			results.add(app.getId());
		}
		
		else {
			for(Application appp : app.getChildren()) {
				results.addAll(findLastChildIds(appp));
			}
		}
		
		return results;
	}

	@Override
	public List<Application> getApplicationHierarchicalyByGroupId(int grpId) {
		
		HashMap<Integer, Application> applications = new HashMap<Integer, Application>();
		
		List<Application> parentsApplications = new ArrayList<Application>();
		
		List<Application> allApps = getApplicationsForGroup(grpId);
		List<ApplicationHierarchy> hierarchies = appHieraMgr.findAll();
		
		//find the root applications
		for(Application app : allApps) {
			boolean finded = false;
			for(ApplicationHierarchy appHiera : hierarchies) {
				if(appHiera.getChildId() == app.getId()) {
					finded = true;
					break;
				}
			}
			if(!finded) {
				parentsApplications.add(app);
			}
			applications.put(app.getId(), app);
		}
		
		//find child applications
		for(ApplicationHierarchy hiera : hierarchies) {
			Application app = applications.get(hiera.getParentId());
			if(app != null) {
				Application child = applications.get(hiera.getChildId());
				if(child != null) {
					app.addChild(child);
					child.setParent(app);
				}
			}
		}
		
		return parentsApplications;
	}

	@Override
	public List<Metric> getMetricsByApplicationIds(List<Integer> appIds, int metricType) {
		
		List<Metric> metrics = new ArrayList<Metric>();
		for(Integer id : appIds) {
			 List<Metric> m = getMetricsForApplicationId(id, metricType);
			 
			 if(m != null) {
				 for(Metric met : m) {
					 boolean in = false;
					 for(Metric metric : metrics) {
						 if(metric.getId() == met.getId()) {
							 in = true;
							 break;
						 }
					 }
					 if(!in) {
						 metrics.add(met);
					 }
					 
				 }
			 }
			 
		}
		return metrics;
		
	}

	@Override
	public List<String> getPossibleYearsByApplicationIds(List<Integer> appIds) {
		List<String> years = new ArrayList<String>();
		
		for(Integer id : appIds) {
			List<Assoc_Application_Metric> assos = aTMMgr.getAssoc_Application_MetricByAppId(id);
			for(Assoc_Application_Metric asso : assos) {
				List<MetricValues> values = getValuesForAssocId(asso.getId());
				for(MetricValues val : values) {
					String y = val.getMvPeriodDate().getYear() + 1900 + "";
					if(!years.contains(y)) {
						years.add(y);
					}
				}
			}
		}
		return years;
	}

	@Override
	public void addAssociations(List<Assoc_Application_Metric> assocs) throws Exception {
		
		for(Assoc_Application_Metric asso : assocs) {
			
			if(!alreadyExists(asso)) {
				
				int id = aTMMgr.addAssoc_Territoire_Metric(asso);
				for(Application app : asso.getApplications()) {
					AssocApplicationAssocAppMetric a = new AssocApplicationAssocAppMetric();
					a.setApplicationId(app.getId());
					a.setAssocId(id);
					assoAppAssocMgr.save(a);
				}
			}
		}
		
		
		
	}

	private boolean alreadyExists(Assoc_Application_Metric asso) {
		
		List<Assoc_Application_Metric> assos = aTMMgr.getAssoc_Application_MetricByMetrId(asso.getMetric().getId());
		
		LOOK:for(Assoc_Application_Metric a : assos) {
			List<AssocApplicationAssocAppMetric> appAssos = assoAppAssocMgr.getByAssoId(a.getId());
			boolean finded = false;
			for(AssocApplicationAssocAppMetric appAsso : appAssos) {
				finded = false;
				for(Application appli : asso.getApplications()) {
					if(appli.getId() == appAsso.getApplicationId().intValue()) {
						finded = true;
						break;
					}
				}
				if(!finded) {
					continue LOOK;
				}
			}
			if(finded) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<MetricValues> getValuesForAssoIdDate(List<Integer> assoIds, String period, Date date) {
		return mValsMgr.getMetricsValuesForAssoIdPeriodeDate(assoIds,period,date);
	}

	@Override
	public List<Application> getRootApplicationByGroupId(int id) {
		
		List<Application> parentsApplications = new ArrayList<Application>();

		List<Integer> hierarchies = appHieraMgr.findRootApps(appMgr);
		
		for(Integer hie : hierarchies) {
			Application a = appMgr.getApplicationById(hie);
			if(a != null) {
				parentsApplications.add(a);
			}
		}
		
		//find child applications
		
		//look if apps has children
		for(Application app : parentsApplications) {
			List<ApplicationHierarchy> children = appHieraMgr.getApplicationChildrenByParentId(app.getId());
			
			if(children != null && children.size() > 0) {
				app.setHasChildren(true);
			}
		}
		
		return parentsApplications;
	}

	@Override
	public List<Application> getChildrenApplication(int parentId) {
		List<ApplicationHierarchy> children = appHieraMgr.getApplicationChildrenByParentId(parentId);
		
		List<Application> apps = new ArrayList<Application>();
		for(ApplicationHierarchy hiera : children) {
			Application app = appMgr.getApplicationById(hiera.getChildId());
			if(app != null) {
				List<ApplicationHierarchy> ccc = appHieraMgr.getApplicationChildrenByParentId(app.getId());
				
				if(ccc != null && ccc.size() > 0) {
					app.setHasChildren(true);
				}
				apps.add(app);
			}
		}
		
		return apps;
	}

	@Override
	public Application getApplicationHistory(Application app, Date date) {
		if(app.getChildren() != null && app.getChildren().size() > 0) {
			app.getChildren().clear();
		}
		List<Application> children = getChildrenHistory(app.getId(), date);
		if(children != null) {
			app.setChildren(children);
		}
		
		return app;
	}
	
	private List<Application> getChildrenHistory(int appId, Date date) {
		List<ApplicationHierarchy> hieras = appHieraMgr.getApplicationChildrenByParentId(appId);
		
		List<Application> result = new ArrayList<Application>();
		
		for(ApplicationHierarchy hiera : hieras) {
			Application app = appMgr.getApplicationForHistory(hiera.getChildId(), date);
			if(app != null) {
				List<Application> children = getChildrenApplication(app.getId());
				if(children != null) {
					app.setChildren(children);
				}
				result.add(app);
			}
		}
		
		return result;
	}

	@Override
	public ApplicationDAO getApplicationDAO() {
		return appMgr.getDao();
	}

	@Override
	public List<MetricValues> getMetricValuesForOda(int metricId, int appId, Date startDate, Date endDate, String datePattern) {
		
		List<Assoc_Application_Metric> assocsCpt = getAssoMetricAppByMetricAndAppIds(appId, metricId);

		List<MetricValues> values = MetricImpl.getValuesForAssocIds(assocsCpt, mValsMgr, metricsMgr, this, appId, startDate, endDate, datePattern);
		return values;
		
	}

	@Override
	public List<Metric> getSubMetrics(int metricId) {
		return metricsMgr.getChildren(metricId);
	}

	@Override
	public void deleteSubMetric(Metric m, int metricId) {
		metricsMgr.deleteChild(m, metricId);
	}

	@Override
	public int getObservatoireByTheme(int themeId) {
		List<ObservatoiresThemes> th = obsMgr.getAssoThemeDAO().findByThemeId(themeId);
		return th.get(0).getObsId();
	}
}
