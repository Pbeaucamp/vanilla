package bpm.fm.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import bpm.fm.api.model.AbstractFactTable;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.CalculatedFactTableMetric;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.GroupObservatory;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.ObservatoryTheme;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeAxis;
import bpm.fm.api.model.ThemeMetric;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

@Repository
public class FreeMetricsDao extends HibernateDaoSupport {

	private IVanillaAPI vanillaApi;
	private FreeMetricsJdbc jdbcManager;
	
	public void setFreeMetricsJdbc(FreeMetricsJdbc jdbcManager) {
		this.jdbcManager = jdbcManager;
	}
	
	public List<bpm.fm.api.model.Metric> getMetrics() throws Exception {
		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
		
		Group group = getRootApi().getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		bpm.vanilla.platform.core.beans.Repository repository = getRootApi().getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
		IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(getRootApi().getVanillaContext(), group, repository));
		
		List<Alert> alerts = repoApi.getAlertService().getAlerts();
		if(alerts == null) {
			alerts = new ArrayList<>();
		}
		
		List<Metric> metrics = jdbcManager.getMetrics(null);
		for(Metric met : metrics){
			List<Alert> metAlerts = new ArrayList<Alert>();
			for(Alert al : alerts){
				if(al.getEventObject() instanceof AlertKpi && ((AlertKpi)al.getEventObject()).getMetricId() == met.getId()){
					metAlerts.add(al);
				}
			}
			met.setAlerts(metAlerts);
		}
		
		return metrics;
	}

	public List<Axis> getAxes() throws Exception {
		List<Axis> axes = find("From Axis");
		for(Axis axe : axes) {
			axe.setChildren(getLevels(axe.getId()));
		}
		return axes;
	}
	
	public AbstractFactTable getFactTableByMetric(Metric metric) throws Exception {
		if(metric.getMetricType().equals(bpm.fm.api.model.Metric.METRIC_TYPES.get(bpm.fm.api.model.Metric.TYPE_CLASSIC))) {
			FactTable table = (FactTable) find("From FactTable where metricId = " + metric.getId()).get(0);		
			try {
				table.setFactTableAxis(getFactTableAxis(table.getId()));
				table.setObjectives(getObjectivesByFactTable(table.getId()));
				table.setDatasource(getDatasource(table.getDatasourceId()));
			} catch (Exception e) {
			}
			return table;
		}
		else {
			CalculatedFactTable table = (CalculatedFactTable) find("From CalculatedFactTable where metricId = " + metric.getId()).get(0);	
			
			List<CalculatedFactTableMetric> metrics = find("From CalculatedFactTableMetric where fact_table_id = " + table.getId());
			for(CalculatedFactTableMetric m :metrics) {
				m.setMetric(getMetric(m.getMetricId()));
			}
			table.setMetrics(metrics);
			return table;
		}
		
		
	}
	
	public List<FactTableAxis> getFactTableAxis(int id) throws Exception {
		List<FactTableAxis> axis = find("From FactTableAxis where factTableId = "+id);
		
		for(FactTableAxis a : axis) {
			a.setAxis(getAxe(a.getAxisId()));
		}
		
		return axis;
	}

	

	public FactTableObjectives getObjectivesByFactTable(int id) {
		return (FactTableObjectives) find("From FactTableObjectives where factTableId = " + id).get(0);
	}

	public List<bpm.fm.api.model.Metric> getMetrics(List<Integer> ids) throws Exception {
		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
		
		Group group = getRootApi().getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		bpm.vanilla.platform.core.beans.Repository repository = getRootApi().getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
		IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(getRootApi().getVanillaContext(), group, repository));
		
		List<Alert> alerts = repoApi.getAlertService().getAlerts();
		if(alerts == null) {
			alerts = new ArrayList<>();
		}
		List<Metric> metrics = (List<Metric>)jdbcManager.getMetrics(ids);
		for(Metric met : metrics){
			List<Alert> metAlerts = new ArrayList<Alert>();
			for(Alert al : alerts){
				if(al.getEventObject() instanceof AlertKpi && ((AlertKpi)al.getEventObject()).getMetricId() == met.getId()){
					metAlerts.add(al);
				}
			}
			met.setAlerts(metAlerts);
		}
		if(metrics != null && metrics.size() > 0){
			return metrics;
		} else {
			return null;
		}
	}
	
	public bpm.fm.api.model.Metric getMetric(int id) throws Exception {
		List<Integer> ids = new ArrayList<>();
		ids.add(id);
		List<Metric> metrics = getMetrics(ids);
		if(metrics != null && metrics.size() > 0){
			return metrics.get(0);
		} else {
			return null;
		}
	}

	public Axis getAxe(int id) throws Exception {
		List<Axis> axis = (List<Axis>) find("From Axis where id = " + id);
		if(axis != null && axis.size() > 0){
			Axis axe = axis.get(0);
			axe.setChildren(getLevels(axe.getId()));
			return axe;
		} else {
			return null;
		}
		
	}

	public Datasource getDatasource(int id) throws Exception {
		
		return getRootApi().getVanillaPreferencesManager().getDatasourceById(id);
		
	}

	public List<Level> getLevels(int axeId) throws Exception {
		List<Level> levels = find("from Level where parentId = " + axeId + " order by levelIndex");
		 
		for(Level level : levels) {
//			for(Datasource ds : datasources) {
//				if(level.getDatasourceId() == ds.getId()) {
					level.setDatasource(getDatasource(level.getDatasourceId()));
//					break;
//				}
//			}
		}
		
		return levels;
	}

	public Level getLevel(int id) throws Exception {
		List<Level> levels = (List<Level>)find("from Level where id = " + id);
		if(levels != null && levels.size() > 0){
			Level level = levels.get(0);
			level.setDatasource(getDatasource(level.getDatasourceId()));
			return level; 
		} else {
			return null;
		}
		

		
	}

//	public List<MetricAlert> getAlerts(Metric metric) {
//		List<MetricAlert> alerts = find("From MetricAlert where metricId = " + metric.getId());
//		for(MetricAlert alert : alerts) {
//			try {
//				alert.setMetric(metric);
//				MetricAlertCondition cond = (MetricAlertCondition) find("From MetricAlertCondition where alertId = " + alert.getId()).get(0);
//				alert.setCondition(cond);
//				MetricAlertEvent event = (MetricAlertEvent) find("From MetricAlertEvent where alertId = " + alert.getId()).get(0);
//				alert.setEvent(event);			
//			} catch (Exception e) {
//			}
//		}
//		return alerts;
//	}
	
	public Theme getTheme(int themeId) throws Exception {
		Theme theme = (Theme) find("From Theme where id = " + themeId).get(0);
		
		fillTheme(theme);
		
		return theme;
	}
	
	private void fillTheme(Theme theme) throws Exception {
		List<ThemeMetric> themeMetrics = getThemeMetricByTheme(theme.getId());
		for(ThemeMetric th : themeMetrics) {
			theme.getMetrics().add(getMetric(th.getMetricId()));
		}
		
		//get the axis
		List<ThemeAxis> themeAxis = getThemeAxisByTheme(theme.getId());
		for(ThemeAxis th : themeAxis) {
			theme.getAxis().add(getAxe(th.getAxisId()));
		}
		
	}

	public List<Theme> getThemes() throws Exception {
		List<Theme> themes = find("From Theme");
		
		for(Theme theme : themes) {
			fillTheme(theme);
		}
		
		return themes;
	}

	public List<Theme> getThemesByGroup(int groupId) throws Exception {
		
		List<GroupObservatory> gts = getGroupObservatoryByGroup(groupId);
		if(gts == null || gts.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Theme> themes = new ArrayList<Theme>();
		
		for(GroupObservatory go : gts) {
			List<Theme> ths = getThemesByObservatory(go.getObservatoryId());
			
			if(ths != null && !ths.isEmpty()) {
				themes.addAll(ths);
			}
			
		}
		
		Set<Theme> set = new HashSet<Theme>(themes);
		themes = new ArrayList<Theme>(set);
		
		for(Theme theme : themes) {
			fillTheme(theme);
		}
		
		return themes;
	}

	public List<GroupObservatory> getGroupObservatoryByObservatory(int observatoryId) {
		List<GroupObservatory> groupThemes = find("From GroupObservatory where observatoryId = " + observatoryId);
		return groupThemes;
	}
	
	public List<GroupObservatory> getGroupObservatoryByGroup(int groupId) {
		List<GroupObservatory> groupThemes = find("From GroupObservatory where groupId = " + groupId);
		return groupThemes;
	}

	public List<ThemeAxis> getThemeAxisByAxis(int axisId) {
		List<ThemeAxis> groupThemes = find("From ThemeAxis where axisId = " + axisId);
		return groupThemes;
	}

	public List<ThemeMetric> getThemeMetricByMetric(int metricId) {
		List<ThemeMetric> groupThemes = find("From ThemeMetric where metricId = " + metricId);
		return groupThemes;
	}

	public List<ThemeMetric> getThemeMetricByTheme(int themeId) {
		List<ThemeMetric> groupThemes = find("From ThemeMetric where themeId = " + themeId);
		return groupThemes;
	}
	
	public List<ThemeAxis> getThemeAxisByTheme(int themeId) {
		List<ThemeAxis> groupThemes = find("From ThemeAxis where themeId = " + themeId);
		return groupThemes;
	}
	
	private IVanillaAPI getRootApi() {
		if(vanillaApi == null) {
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			String user = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String pass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			
			IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, user, pass);
			vanillaApi = new RemoteVanillaPlatform(vanillaContext);
		}
		
		return vanillaApi;
	}

	public Observatory getObservatoryById(int id) {
		Observatory obs = (Observatory) find("From Observatory where id = " + id).get(0);		
	
		fillObservatory(obs);

		return obs;
	}

	public List<Theme> getThemesByObservatory(int id) {
		List<ObservatoryTheme> ots = find("From ObservatoryTheme where observatoryId = " + id);
		
		if(ots == null || ots.isEmpty()) {
			return new ArrayList<Theme>();
		}
		
		StringBuilder ids = new StringBuilder();
		boolean first = true;
		for(ObservatoryTheme gt : ots) {
			if(first) {
				first = false;
			}
			else {
				ids.append(",");
			}
			ids.append(gt.getThemeId());
			
		}
		
		List<Theme> themes = null;
		try {
			themes = find("From Theme where id in (" + ids.toString() + ")");
			
			for(Theme theme : themes) {
				fillTheme(theme);
			}
		} catch (Exception e) {
			e.printStackTrace();
			themes = new ArrayList<>();
		}
		
		return themes;
	}

	public List<Observatory> getObservatoryByGroupId(int groupId) {
		List<GroupObservatory> gos = find("From GroupObservatory where groupId = " + groupId);
		if(gos == null || gos.isEmpty()) {
			return new ArrayList<Observatory>();
		}
		StringBuilder ids = new StringBuilder();
		boolean first = true;
		for(GroupObservatory go : gos) {
			if(first) {
				first = false;
			}
			else {
				ids.append(",");
			}
			ids.append(go.getObservatoryId());
			
		}
		
		List<Observatory> obs =  null;
		
		try {
			obs = find("From Observatory where id in (" + ids.toString() + ")");
			
			for(Observatory ob : obs) {
				
				fillObservatory(ob);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Collections.sort(obs, new Comparator<Observatory>() {

			@Override
			public int compare(Observatory o1, Observatory o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return obs;
	}

	public void fillObservatory(Observatory ob) {
		List<GroupObservatory> gts = getGroupObservatoryByObservatory(ob.getId());
		List<Group> groups = new ArrayList<Group>();
		for(GroupObservatory gt : gts) {
			try {
				Group g = getRootApi().getVanillaSecurityManager().getGroupById(gt.getGroupId());
				groups.add(g);
			} catch (Exception e) {
				//if it comes here, it means an observatory is linked with a missing group
				//we don't want everything to crash because of this
//				e.printStackTrace();
			}
		}
		
		ob.setGroups(groups);
		
		ob.setThemes(getThemesByObservatory(ob.getId()));
		
	}
	
	public List<MetricAction> getMetricAction(int metricId) {
		return find("From MetricAction where metricId = " + metricId);
	}
}
