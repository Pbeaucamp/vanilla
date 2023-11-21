package bpm.freemetrics.api.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.freemetrics.api.exception.ThemeObservatoireException;
import bpm.freemetrics.api.exception.UserObservatoireException;
import bpm.freemetrics.api.features.actions.Action;
import bpm.freemetrics.api.features.alerts.Alert;
import bpm.freemetrics.api.features.alerts.AlertCondition;
import bpm.freemetrics.api.features.alerts.MetricWithAlert;
import bpm.freemetrics.api.features.favorites.UserAction;
import bpm.freemetrics.api.features.favorites.UserAlertePreference;
import bpm.freemetrics.api.features.favorites.UserPreference;
import bpm.freemetrics.api.features.favorites.WatchList;
import bpm.freemetrics.api.features.forum.Forum;
import bpm.freemetrics.api.features.infos.FMDatasource;
import bpm.freemetrics.api.features.infos.Organisation;
import bpm.freemetrics.api.features.infos.TypeMetric;
import bpm.freemetrics.api.features.infos.TypeOrganisation;
import bpm.freemetrics.api.features.infos.Unit;
import bpm.freemetrics.api.organisation.metrics.AssocCompteurIndicateur;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.application.ApplicationDAO;
import bpm.freemetrics.api.organisation.application.Decompo_Territoire;
import bpm.freemetrics.api.organisation.application.TypeDecompTerritoire;
import bpm.freemetrics.api.organisation.application.TypeTerritoire;
import bpm.freemetrics.api.organisation.dashOrTheme.DashboardRelation;
import bpm.freemetrics.api.organisation.dashOrTheme.SubTheme;
import bpm.freemetrics.api.organisation.dashOrTheme.Theme;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.group.TypeCollectivite;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricInteraction;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.observatoire.Observatoire;
import bpm.freemetrics.api.organisation.observatoire.ObservatoireManager;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_Groups;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.organisation.relations.appl_typeApp.Assoc_Terr_Type_Dec_Terr;
import bpm.freemetrics.api.organisation.relations.user_group.FmUser_Groups;
import bpm.freemetrics.api.security.FmRole;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.security.relations.FmUser_Roles;
import bpm.freemetrics.api.treebeans.TreeGroupsAppsThm;

public interface IManager {

	/**
	 * @return
	 */
	public List<FmUser> getUsers();

	/**
	 * @param user
	 * @return
	 */
	public int addUser(FmUser user);

	/**
	 * @param user
	 * @param isSuperAdmin  
	 * @return
	 */
	public int updateUser(FmUser user, boolean isSuperAdmin);

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public FmUser getUserById(int id);

	/**
	 * @param name
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public FmUser getUserByNameAndPass(String name, String password) throws Exception;

	/**
	 * @param group
	 * @throws Exception
	 */
	public int addGroup(Group group) throws Exception;


	/**
	 * @param sthm
	 * @throws Exception
	 */
	public int addAssoc_Territoire_Metric(Assoc_Application_Metric sthm) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public List<Assoc_Application_Metric> getAssoc_Territoire_Metrics() throws Exception;

	
	public Assoc_Application_Metric getAssoc_Territoire_Metric_ById(int assocId) throws Exception ;
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Assoc_Application_Metric getAssoc_Territoire_MetricById(int id) throws Exception;

	/**
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Assoc_Application_Metric getAssoc_Territoire_MetricByName(String name) throws Exception;


	/**
	 * @param ug
	 * @throws Exception
	 */
	public void addUserGroup(FmUser_Groups ug) throws Exception;

	/**
	 * @param ug
	 * @throws Exception
	 */
	public void deleteUserGroup(FmUser_Groups ug) throws Exception;

	/**
	 * @param ug
	 * @throws Exception
	 */
	public void updateUserGroup(FmUser_Groups ug) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public List<FmUser_Groups> getUserGroups() throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public FmUser_Groups getUserGroupById(int fmUserGroupId) throws Exception;
	
	public List<FmUser_Groups> getUserGroupByUserId(int id);

	/**
	 * @param groupId
	 * @param userId 
	 * @return
	 */
	public List<Integer> getUserIdsForGroup(int groupId, int userId);

	/**
	 * @param userId
	 * @return
	 */
	public List<Integer> getGroupIdForUser(int userId);

	/**
	 * @param groupId
	 */
	public boolean deleteUserGroupByGroupId(int groupId);

	/**
	 * @param userId
	 * @param groupId
	 */
	public boolean deleteUserGroupByUserAndGroupIds(int userId, int groupId);

	/**
	 * @param group
	 * @throws Exception
	 */
	public void updateGroup(Group group) throws Exception;

	/**
	 * @param groupId
	 * @return
	 */
	public Group getGroupById(int groupId);

	/**
	 * @param groupId
	 */
	public boolean deleteApplicationGroupByGroupId(int groupId);


	/**
	 * @param ag
	 * @return an int return code value : 
	 * <br> -5 = FM_APPLICATIONGROUPS_ADD_SUCCESFULLY
	 * <br> -3 = APP_ALREADY_LINKED_WITH_GROUP
	 * <br> -4 = ASSOCIATION_APPLICATION_GROUP_ALREADY_EXIST
	 * 
	 */
	public int addApplicationGroup(FmApplication_Groups ag);

	/**
	 * @param applicationId
	 * @return
	 */
	public int getGroupIdForApplication(int applicationId);

	/**
	 * @param applicationId
	 * @param groupId
	 */
	public void deleteApplicationGroupByApplicationAndGroupIds(int applicationId,
			int groupId);

	/**
	 * @param groupId
	 * @return
	 */
	public List<Integer> getApplicationIdsForGroup(int groupId);

	public List<Group> getGroups();

	public List<Integer> getGroupsIds();

	public List<Application> getApplications();

//	public List<Dashboard> getDashboardsForApplId(int appId);

//	public Dashboard getDashboardsById(int dashId);

//	public List<Dashboard> getSubDashboards(Dashboard dashboard);

	public List<Application> getApplicationsForMetricId(int metrId);

	public List<Metric> getMetricsForGroupId(int grpId);

	public List<FmRole> getRolesForUserId(int userId);

	public List<Metric> getMetricsForOwnerId(int userId);

	/**@deprecated
	 * @param typeId
	 * @return
	 */
	public List<Metric> getMetricsForTypeId(int typeId);

	public List<Metric> getMetricsForType(String calculationType);

	public List<Metric> getMetrics();

	public FmUser getUserByName(String name);

//	public List<Metric> getMetricsForAppAndDash(int appId, int dashId);

	public Metric getMetricById(int metricID);

	public List<MetricValues> getValuesForMetricAppBeforeDate(int appID, int metricID,
			Date date);

	public boolean isSuperAdmin(int userId);

	public List<Group> getGroupsForUser(int userId);

	public Integer[] getMetricsTopIDs(int appID, int metricID);

	public String[] getMetricsTopNames(int appID, int metricID);

	public float[] getMetricsTopMinimum(int appID, int metricID);

	public float[] getMetricsTopTargets(int appID, int metricID);

	public Integer[] getMetricsDownIDs(int appID, int metricID);

	public String[] getMetricsDownNames(int appID, int metricID);

	public float[] getMetricsDownTargets(int appID, int metricID);

	public float[] getMetricsDownMinimum(int appID, int metricID);

	public MetricWithAlert getMetricWithAlertById(int appID, int metricID, int alertID);

	public boolean updateMetrWithAlert(MetricWithAlert alert) throws Exception;

	public int addMetrWithAlert(MetricWithAlert alert)throws Exception;

	public void deleteMetrWithAlert(MetricWithAlert alert)throws Exception;

	public List<MetricWithAlert> getAlertsForMetricId(int metricID);

	public List<MetricWithAlert> getMetricWithAlerts();

	public List<Action> getActions();

	public List<FmRole> getRoles();

	public List<Integer> getUserIdsForRoleId(int roleId, int userId);

	public int addAlert(Alert alert)throws Exception;

	public boolean updateAlert(Alert alert) throws Exception;

	public void deleteMetrWithAlert(Alert alert)throws Exception;

	public Application getApplicationById(int appliId);

	public List<Metric> getMetricsForApplicationId(int appId,int filter);

	public int addAction(Action action)throws Exception;

	public Action getActionById(int actId);

	public void updateAction(Action action)throws Exception;

	public AlertCondition getAlertConditionForMWAId(int mwalId);

	public Alert getAlertById(Integer maAlertId);

	public void updateForum(Forum forum) throws Exception;

	public Forum getForumById(int forumId);

	public boolean delete(Forum forum);

	public List<Forum> getForumForAppAndMetrId(int appId, int metrId);

	public int addForum(Forum forum)throws Exception;

	public List<Forum> getForumForAppMetrAndMetrValId(int appId, int metrId,
			int metrValId);

	public MetricValues getMetricValueById(int metrValId);

	public List<Alert> getAlertForUserId(int userId);

	public MetricWithAlert getMetricWithAlertById(int ma_id);

	public List<MetricValues> getValuesForAppIdAndMetrId(int appId, int metrId);

	public List<SubTheme> getSubThemeForThemeId(int themeId);

	public List<Theme> getThemeForApplId(int appId,int filter);

	public List<Metric> getMetricsForAppAndTheme(int application, int themeId);

	public List<WatchList> getWatchlistByUserId(int userId);

	public int addUserWatchList(WatchList userWatchList)throws Exception;

	public WatchList getWatchlistById(int mw_id);

	public boolean deleteUserWatchList(WatchList del);

	public int addUserPreference(UserPreference pref)throws Exception;

	public UserPreference getUserPreferenceById(int mp_id);

	public boolean deleteUserPreference(UserPreference pref);

	public int addUserAlertePreference(UserAlertePreference usrAlPref)throws Exception;

	public Theme getThemeById(int thmId);

	public List<UserPreference> getUserPrefForUserId(int userPrefId);

	public List<WatchList> getWatchlistForUserIdAppIdAndMetrId(int userId, int appId,
			int metrId);

	public boolean deleteValueWithId(int valueID);

	public int addMetricValue(MetricValues val)throws Exception;

	public int getAssociationIdForMetrIdAndAppId(int metricID, int applicationID);

	public void updateMetricValue(MetricValues val)throws Exception;

	public List<Application> getAllowedApplicationForUser(int userId);

	public List<Theme> getAllowedThemeForUser(int userId);

	public List<Metric> getAllowedMetricsForUser(int userId,int filter);

	public int addUserAction(UserAction op) throws Exception;

	public List<UserAction> getUserActionsForUser(int userId);

	public List<Alert> getAlerts();

	public List<MetricWithAlert> getAlertsForMetricAndAppId(int metricId,
			int appId);

	public UserAction getUserActionById(int id);

	public boolean deleteUserAction(UserAction del);

	public UserAlertePreference getUserAlertePreferenceById(int id);

	public boolean deleteUserAlertePreference(UserAlertePreference del);

	public MetricValues getLastValueForAppIdAndMetrId(int applid, int metrId);

	public Unit getUnitById(int unitId);

	public boolean updateUnit(Unit unit);

	public boolean deleteUnit(Unit unit);

	public int addRole(FmRole role);

	public FmRole getRoleById(int roleid);

	public List<Unit> getUnits();

	public int addUnit(Unit u);

	public List<Theme> getThemes();

	public boolean updateRole(FmRole role);

	public int addUserRole(FmUser_Roles userRole);

	public boolean deleteUserRoleByUsrIdAndRoleId(int userId, int roleId);

	public boolean deleteTheme(Theme thm);

	public boolean updateTheme(Theme thm);

	public boolean deleteGroupById(int id);

	public boolean deleteUserById(int id) throws Exception;

	public List<FmUser> getAllowedUserForUser(int userId);

	public List<TypeCollectivite> getTypeCollectivites();

	public boolean deleteTypeCollectivite(TypeCollectivite col);

	public boolean updateTypeCollectivite(TypeCollectivite col);

	public int addTypeCollectivite(TypeCollectivite sthm);

	public TypeCollectivite getTypeCollectiviteById(int id);

	public Unit getUnitByName(String unitName);

	public int addOrganisation(Organisation org) throws Exception;

	public List<Organisation> getOrganisations() throws Exception;

	public Organisation getOrganisationById(int id) throws Exception;

	public Organisation getOrganisationByName(String name) throws Exception;

	public boolean deleteOrganisation(Organisation o);

	public boolean updateOrganisation(Organisation o) throws Exception;

	public int addTypeOrganisation(TypeOrganisation sthm) throws Exception;

	public List<TypeOrganisation> getTypeOrganisations() throws Exception;

	public TypeOrganisation getTypeOrganisationById(int id) throws Exception;

	public TypeOrganisation getTypeOrganisationByName(String name) throws Exception;

	public boolean deleteTypeOrganisation(TypeOrganisation to);

	public boolean updateTypeOrganisation(TypeOrganisation to) throws Exception;

	public int addTheme(Theme thm) throws Exception;

	public int addTypeTerritoire(TypeTerritoire thm);

	public int addTypeDecompTerritoire(TypeDecompTerritoire sthm);

	public TypeTerritoire getTypeTerritoireById(int id) throws Exception;

	public TypeDecompTerritoire getTypeDecompTerritoireById(int id) throws Exception;

	public List<TypeDecompTerritoire> getTypeDecompTerritoires() throws Exception;

	public List<TypeTerritoire> getTypeTerritoires() throws Exception;

	public boolean deleteTypeTerritoire(TypeTerritoire typTer);

	public boolean updateTypeTerritoire(TypeTerritoire typTer);

	public List<Application> getApplicationForTypeTerrId(int typTerId);

	public boolean deleteTypeDecompTerritoire(TypeDecompTerritoire typTer);

	public boolean updateTypeDecompTerritoire(TypeDecompTerritoire typTer);

	public List<Application> getApplicationForTypeDecomTerrId(int typDecId);

	public Theme getThemeByName(String name);

	public List<FmUser> getUserForRoleId(int id);

	public boolean deleteRole(FmRole role);

	public boolean isRoleSuperAdmin(int roleId);

	public TypeMetric getTypeMetricByName(String name);

	public boolean deleteTypeMetric(TypeMetric col);

	public boolean updateTypeMetric(TypeMetric col);

	public int addTypeMetric(TypeMetric u);

	public TypeMetric getTypeMetricById(int id);

	public List<TypeMetric> getTypeMetrics();

	public FmUser getUserByLogin(String login);

	public List<Assoc_Application_Metric> getAssoc_Application_MetricByMetricId(int metricId);

	public List<Assoc_Application_Metric> getAssoc_Application_MetricsByAppId(int appId);

	public boolean deleteAssoc_Application_Metric(Assoc_Application_Metric assoc);

	public boolean deleteActionById(int acId);

	public boolean updateApplication(Application application);

	public List<Integer> getTypeDecTerrIDsForAppId(int appid);

	public List<Integer> getDecompoTerritoireForAppIdAndTypeDecTerrId(int appId,int tdtId);

	public int addAssoc_Terr_TypeDecom(Assoc_Terr_Type_Dec_Terr dec);

	public boolean deleteAssoc_Terr_TypeDecom(int id, int integer);

	public List<Assoc_Terr_Type_Dec_Terr> getAssoc_Terr_Type_Dec_Terrs();

	public int addDecompoTerritoire(Decompo_Territoire dec);

	public boolean deleteDecompForParentAndChildId(int parAppId, int childAppId);

	public Application getApplicationByName(String text);

	public List<Theme> getThemesForParentThemeId(int parThemeId);

	public List<Theme> getParentThemeForThemeId(int id);

	public boolean updateMetric(Metric metricCopy);

	public boolean deleteApplinksForAppId(int id);

	public int addApplication(Application app);

	public boolean deleteApplicationById(int id);

	public int addMetric(Metric met);

	public Metric getMetricByName(String text);

	public boolean deleteMetrLinksForMetrId(int metid);

	public boolean deleteMetricById(int metid);

	public int addDashboardRelation(DashboardRelation ug);

	public boolean deleteDashboardRelation(int childDashId, int parentDashId);

	public boolean delete(Alert al);

	public SubTheme getSubThemeById(Integer mdGlSubThemeId);

	public List<MetricValues> getAllowedValuesForUser(int userId, List<String> listeTerritoire, List<String> themes);

	public List<Alert> getAllowedAlertsForUser(int userId);

	public List<Alert> getAlertsForMetric(int mId);

	public Action getActionByName(String name);

	public List<Alert> getAlertsForActionId(int actionId);

	public List<Application> getApplicationsForGroup(int gpId);

	public List<MetricWithAlert> getMetricWithAlertForAlertId(int alId);

	public boolean updateAlertCondition(AlertCondition ac);

	/**
	 * @deprecated
	 * @param values
	 * @return
	 */
	public List<MetricValues> getObjectifsForValueWithSamePeriod(MetricValues values);

	public MetricValues getPreviousMetricValue(MetricValues values, String periodicity);

	public List<MetricValues> getValuesForMetricAppPeridDate(int appId, int metricId, Date date);

	public MetricValues getMetricValueAtPeriod(MetricValues values, Date end);

	public AssocCompteurIndicateur getAssoc_Compteur_IndicateurForComptId(int compID);

	public boolean update(AssocCompteurIndicateur assoc);

	public int addAssoc(AssocCompteurIndicateur assoc, boolean allowMulti);

	public List<SubTheme> getSubThemes();

	public TypeDecompTerritoire getTypeDecompTerritoireByName(String name);

	public TypeCollectivite getTypeCollectiviteByName(String name);

	public TypeTerritoire getTypeTerritoireByName(String name);

	public SubTheme getSubThemeByName(String text);

	public List<Group> getAllowedGroupsForUser(int userId, boolean isSuperAdmin);

	public int addSubTheme(SubTheme sthm);

	public boolean isAdmin(int userId);

	public boolean updateSubTheme(SubTheme ssthm);

	public List<FmUser> getAllowedUsersForUserId(int userId, boolean superAdmin);

	public boolean deleteSubTheme(SubTheme ssthm);

	public FmRole getRoleByName(String name);

	public Group getGroupByName(String name);

	public boolean isMetricHasValues(int metid);

	public List<Metric> getCompteurs();

	public List<Metric> getIndicateurs();

	public int addMetricInteraction(MetricInteraction n) throws Exception;

	public int addDatasource(FMDatasource ds) throws Exception;

	public boolean updateDatasource(FMDatasource ds);

	public boolean deleteDatasource(FMDatasource ds);

	public List<FMDatasource> getDatasources();

	public FMDatasource getDatasourceByName(String name);

	public FMDatasource getDatasourceById(int id);

	public List<SubTheme> getSubThemeForApplIdAndThemeId(int appId, int id,int filter);

	public List<Metric> getMetricsForAppThemeAndSubTheme(int appId, int thmId,
			int sthmId);


	public MetricValues getValuesForAssocIdPeridDate(int mvGlAssoc_ID,
			Date mvPeriodDate);


	public MetricValues getObjectifsForValueAndPeriod(MetricValues val,
			Date mvPeriodDate);

	public MetricValues getObjectifForMetricForPeriode(int appId, int metId,
			Date date);

	public List<MetricValues> getValuesForAssocId(int assocId);

	public List<TreeGroupsAppsThm> getTreeGroupsAppsThmForUser(int userId);

	public Group getGroupForApplication(int appId);

	public List<MetricInteraction> getMetricInteractionsForMetrId(int id);

	public boolean deleteMetricInteraction(MetricInteraction assoc);
	
	public boolean authentify(String username, String password, IManager manager);
	
	public List<Observatoire> getObservatoiresForUserId(int userId);
	
	public List<Theme> getThemesForObservatoireId(int obsId);
	
	public List<Metric> getMetricsByThemeIdAndPeriode(int themeId, String periode);
	
	public List<Metric> getCompteursByThemeIdAndPeriode(int themeId, String periode);
	
	public List<Metric> getIndicateursByThemeIdAndPeriode(int themeId, String periode);
	
//	public List<MetricValues> getValuesForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId);
	
	public List<Assoc_Application_Metric> getAssociationsIdForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId);
	
	public List<MetricValues> getMetricsValuesForAssoId(List<Integer> assoIds);
	
	public void insertUpdateDeleteValues(List<MetricValues> insertValues, List<MetricValues> deleteValues, List<MetricValues> updateValues);

	public List<MetricValues> getValuesForMetricsApplications(List<Integer> metricsIds, List<Integer> appIds,String selectedPeriode, Date selectedDate);

	public Observatoire getObservatoireById(int id);

	public List<Observatoire> getObservatoires();

	public Observatoire getObservatoireByName(String text);
	
	public void update(Observatoire observatoire);

	public void deleteObservatoire(Observatoire observatoire) throws UserObservatoireException, ThemeObservatoireException;
	
	public int addObservatoire(Observatoire obs);
	
	public void updateObservatoire(Observatoire obs);
	
	public int associateThemeObservatoire(Theme theme, Observatoire observatoire);
	
	public int associateUserObservatoire(FmUser user, Observatoire observatoire) throws Exception;
	
	public void removeUserFromObservatoire(FmUser user, Observatoire observatoire);
	
	public void removeThemeFromObservatoire(Theme theme, Observatoire observatoire);

	public List<FmUser> getUsersForObservatoire(int id);

	public List<Metric> getCompteursByObservatoires(String observatoire);

	public List<Metric> getIndicateursByObservatoires(String observatoire);

	public List<Metric> getNotAssociatedIndicateur(Metric compteur);

	public List<Metric> getAssciatedIndicateur(Metric compteur);

	public List<Metric> getNotAssociatedCompteur(Metric indicateur, int observatoireId);

	public List<Metric> getAssciatedCompteur(Metric indicateur);
	
	public void delete(AssocCompteurIndicateur assoc);

	public boolean deleteGroup(Group group, boolean cascade);

	public Date getPreviousDateByAssoId(int assoId);

	public Metric getMetricByIdAssoId(Integer mvGlAssoc_ID);
	
	public List<Metric> getMetricsByThemeId(int themeId);
	
	public List<Metric> getMetricsBySubThemeId(int subThemeId);

	public Metric getIndicateurByCompteurId(int id);
	
	/**
	 * this method will load all metricvalues for the given CompteurID
	 * it will also load the Compteur's Indicator MetricValue matching to each metricValues on the same row
	 * 
	 *  ie : 
	 *  MvValue	MvPeriodDate MvMin	MvMax	MvTarget
	 *  10			2000	null	null		null
	 *  null		2000	4		10			6
	 *  
	 *  will return a unique Row
	 * MvValue	MvPeriodDate MvMin	MvMax	MvTarget
	 *  10			2000		4	 10		  6	
	 *  
	 *  
	 * @param compteurId
	 * @param applicationId
	 * @return
	 */
	public List<MetricValues> getFullMetricValues(Metric compteur, Application application) throws Exception;
	
	/**
	 * same as above but for a single ValueDate -> this should return only one row 
	 * @param compteur
	 * @param application
	 * @param dateValue
	 * @return
	 * @throws Exception
	 */
	public List<MetricValues> getFullMetricValues(Metric compteur, Application application, Date dateValue) throws Exception;
	
	/**
	 * Get an association for a metric Id and an association id
	 * @param appId
	 * @param metId
	 * @return
	 */
	public List<Assoc_Application_Metric> getAssoMetricAppByMetricAndAppIds(int appId, int metId);
	public List<AssocCompteurIndicateur> getAssocCompteurIndicForIndicId(int indicId);
	
	public List<Forum> getForumByValueId(int valueId);

	public FmUser getUserByNameAndPass(String username, String password, boolean isEncrypted);
	
	public boolean authentify(String username, String password, boolean isEncrypted, IManager manager);

	public boolean isCommentValidator(FmUser userFM);
	
	public List<Application> getApplicationHierarchicalyByGroupId(int grpId);
	
	/**
	 * Return all metrics which are associated with one or more application in the list
	 * @param appIds
	 * @param metricType Metric/Indicator filter (use the IConstant class for this)
	 * @return
	 */
	public List<Metric> getMetricsByApplicationIds(List<Integer> appIds, int metricType);
	
	public List<String> getPossibleYearsByApplicationIds(List<Integer> appIds);

	public void addAssociations(List<Assoc_Application_Metric> assocs) throws Exception;
	
	public List<MetricValues> getValuesForAssoIdDate(List<Integer> assoIds, String period, Date date);
	
	public List<Application> findApplicationsByAssoId(int id);
	
	/**
	 * Return all the possible applications for an assoId (even parent applications)
	 * @param id
	 * @return
	 */
	public List<Application> findAllApplicationByAssoId(int id);
	
	public List<Application> getRootApplicationByGroupId(int id);
	
	public List<Application> getChildrenApplication(int parentId);
	
	public Application getApplicationHistory(Application app, Date date);
	
	public ApplicationDAO getApplicationDAO();
	
	/**
	 * Used in the oda driver to get back the values
	 * @param metricId
	 * @param appId
	 * @param startDate if null, we take all dates
	 * @param endDate if null and startDate is not, we only take the value for startDate
	 * @return
	 */
	public List<MetricValues> getMetricValuesForOda(int metricId, int appId, Date startDate, Date endDate, String datePattern);

	public List<Metric> getSubMetrics(int metricId);

	public void deleteSubMetric(Metric m, int metricId);

	public int getObservatoireByTheme(int themeId);
}
