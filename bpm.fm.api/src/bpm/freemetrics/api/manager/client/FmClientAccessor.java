package bpm.freemetrics.api.manager.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.dashOrTheme.Theme;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.observatoire.Observatoire;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;

public class FmClientAccessor {

	private static List<FmClientAccessor> clients = new ArrayList<FmClientAccessor>();
	
	
	private IManager mgr;
	private FmUser user;
	private List<Group> userGroups;
	private List<Application> userApplications;
	private HashMap<Application, List<Metric>> metrics = new HashMap<Application, List<Metric>>();
	
	private RemoteFreeMetricsManager remoteFm;
	
	private int groupId;
	private int themeId;


	private bpm.fm.api.model.Theme theme;
	
	public static FmClientAccessor getClient(IManager mgr, String login, String password, String url) throws Exception{
		
		for(FmClientAccessor a : clients){
			if (a.user.getPassword() .equals(password) && a.user.getLogin() .equals(login)){
				return a;
			}
		}
		
		FmClientAccessor a = new FmClientAccessor(mgr, login, password, url);
		clients.add(a);
		return a;
	}
	
	public static FmClientAccessor getClient(IManager mgr, FmUser user) throws Exception{
		for(FmClientAccessor a : clients){
			if (user.getLogin() == a.user.getLogin()){
				return a;
			}
		}
		
		FmClientAccessor a = new FmClientAccessor(mgr, user);
		clients.add(a);
		return a;
	}
	 
	private FmClientAccessor(IManager mgr, String login, String password, String url)throws Exception{
//		FmUser user = mgr.getUserByNameAndPass(login, password);
//		if (user == null){
//			throw new Exception("User not found");
//		}
		
		remoteFm = new RemoteFreeMetricsManager(url, login, password);
		this.mgr = mgr;
		this.user = new FmUser();
		user.setLogin(login);
		user.setPassword(password);
//		reload();
	}
	
	public void reload(){
		loadUserGroups();
		loadApplicationsForUser();
		loadMetric();
	}
	
	private FmClientAccessor(IManager mgr, FmUser user){
		this.mgr = mgr;
		this.user = user;
		reload();

	}
	
	public List<Application> getApplications(){
		return new ArrayList<Application>(userApplications);
	}
	
	public List<Metric> getMetricsForApplication(Application app){
		if(app == null) {
			return mgr.getCompteurs();
		}
		else {
			for(Application a : metrics.keySet()){
				if (a.getId() == app.getId()){
					return new ArrayList<Metric>(metrics.get(a));
				}
			}
		}
		return new ArrayList<Metric>();
	}
	
	public List<MetricValues> getValues(Metric cpt, Application app)throws Exception{
		return mgr.getFullMetricValues(cpt, app);
	}
	public List<MetricValues> getValues(Metric cpt, Application app, Date dateValue)throws Exception{
		return mgr.getFullMetricValues(cpt, app, dateValue);
	}
	
	private void loadMetric(){
		
		
		List<Assoc_Application_Metric> userAssoc = new ArrayList<Assoc_Application_Metric>();
			
		for(Application a : userApplications){
			boolean found = false;
			for(Assoc_Application_Metric as : mgr.getAssoc_Application_MetricsByAppId(a.getId())){
				for(Assoc_Application_Metric _as : userAssoc){
					if (_as.getId() == as.getId()){
						found = true;
						break;
					}
				}
				if (!found){
					userAssoc.add(as);
					continue;
				}
			}
		}
		
		
		for(Assoc_Application_Metric a : userAssoc){
			Metric m = mgr.getMetricByIdAssoId(a.getId());
			
			if (!m.getMdGlIsCompteur()){
				continue;
			}
			
			Metric indicateur = mgr.getIndicateurByCompteurId(m.getId());
			if (indicateur == null){
				continue;
			}
			
			List<Application> apps = mgr.findAllApplicationByAssoId(a.getId());
			
			for(Application app : userApplications){
				
				for(Application appp : apps) {
				
					if (app.getId() == appp.getId()){
						if (metrics.get(app) == null){
							metrics.put(app, new ArrayList<Metric>());
						}
						
						boolean found = false;
						for(Metric _m : metrics.get(app)){
							if (_m.getId() == m.getId()){
								found = true;
								break;
							}
						}
						if (!found){
							metrics.get(app).add(m);
							continue;
						}
					}
				}
			}
			
			
			
		}
		//remove the applications without Valid metrics
		List<Application> toRemove = new ArrayList<Application>();
		for(Application app : userApplications){
			if (metrics.get(app) == null || metrics.get(app).isEmpty()){
				toRemove.add(app);
			}
		}
		userApplications.removeAll(toRemove);
	}
	
	
	
	
	/**
	 * load the Application for the User using its Groups
	 */
	private void loadApplicationsForUser(){
		List<Application> applications = new ArrayList<Application>();
		
		for(Group g : userGroups){
			for(Application a : mgr.getApplicationsForGroup(g.getId())){
				
				boolean found = false;
				
				for(Application _a : applications){
					if (a.getId() == _a.getId()){
						found = true;
						break;
					}
				}
				if (!found){
					applications.add(a);
				}
			}
		}
		userApplications = applications;
		
	}
	
	
	private void loadUserGroups(){
		userGroups = mgr.getGroupsForUser(user.getId());
	}
	
	public List<MetricValues> getMetricValues(int appId, int metricId, Date startDate, Date endDate, String datePattern) {
		return mgr.getMetricValuesForOda(metricId, appId, startDate, endDate, datePattern);
	}
	
	public List<Observatoire> getObservatoires() {
		return mgr.getObservatoiresForUserId(user.getId());
	}
	
	public List<Theme> getThemeByObservatoire(int obsId) {
		return mgr.getThemesForObservatoireId(obsId);
	}
	
	public List<Metric> getMetricByTheme(int themeId) {
		return mgr.getMetricsByThemeId(themeId);
	}
	
	public int getObservatoryForTheme(int themeId) {
		return mgr.getObservatoireByTheme(themeId);
	}
	
	public IManager getManager() {
		return mgr;
	}
	
	public List<Axis> getAxis() throws Exception {
		return theme.getAxis();
	}
	
	public List<bpm.fm.api.model.Metric> getMetrics() throws Exception {
		return theme.getMetrics();
	}
	
	public RemoteFreeMetricsManager getRemoteFm() {
		return remoteFm;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getThemeId() {
		return themeId;
	}

	public void setThemeId(int themeId) {
		this.themeId = themeId;
		if(remoteFm != null) {
			try {
				theme = remoteFm.getThemeById(themeId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxisAndMetric(java.util.Date startDate, int axis, int metric) throws Exception {
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> res = remoteFm.getValuesAndPreviousByDateAndAxisAndMetric(startDate, axis, metric, 1);
		return res;
	}
	
	
}
