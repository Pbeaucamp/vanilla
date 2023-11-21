package bpm.fm.designer.web.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricMap;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("MetricService")
public interface MetricService extends RemoteService {

	public static class Connection{
		private static MetricServiceAsync instance;
		public static MetricServiceAsync getInstance(){
			if(instance == null){
				instance = (MetricServiceAsync) GWT.create(MetricService.class);
			}
			return instance;
		}
	}
	
	public Axis addAxe(Axis axe) throws Exception;
	
	public void addLevel(Level level) throws Exception;
	
	public void deleteLevel(Level level) throws Exception;

	public Axis getAxe(int id) throws Exception;

	public void deleteAxe(Axis ds) throws Exception;

	public List<Axis> getAxes(int obsId, int themeId) throws Exception;

	public List<Metric> getMetrics(int obsId, int themeId) throws Exception;

	public Metric addMetric(Metric metric) throws Exception;

	public void deleteMetric(Metric ds) throws Exception;

	public Metric getMetric(int id) throws Exception;

	public void updateMetric(Metric metric) throws Exception;

//	public void addAlert(MetricAlert alert) throws Exception;
	
//	public List<Alert> getAlerts(int metricId) throws Exception;
	
	public List<DatabaseTable> getDatabaseStructure(int datasourceId) throws Exception;

	public void addTheme(Theme theme) throws Exception;

	public List<Theme> getThemes() throws Exception;
	
	public void updateGroupForTheme(List<Group> groups, Theme them) throws Exception;

	public void updateThemeForAxis(List<Theme> themes, Axis axeMetric) throws Exception;

	public void updateThemeForMetric(List<Theme> themes, Metric axeMetric) throws Exception;
	
	public MetricSqlQueries getCreateQueries(Metric metric) throws Exception;
	
	public HashMap<String, Exception> executeQueries(MetricSqlQueries queries) throws Exception;

	public void addObservatory(Observatory observatory) throws Exception;

	public void updateGroupForObservatory(List<Group> groups, Observatory observatory) throws Exception;

	public List<Observatory> getObservatories() throws Exception;

	public void updateObservatories(List<Observatory> observatories) throws Exception;
	
	public void deleteAlert(Alert alert) throws Exception;

	public void deleteObservatory(Observatory ds) throws Exception;
	
	public boolean testConnection(Datasource ds) throws Exception;
	
	public void addMetricAction(MetricAction action) throws Exception;
	
	public void deleteMetricAction(MetricAction action) throws Exception;

	public void deleteMetricMap(MetricMap ds) throws Exception;

	public void addMetricMap(MetricMap map) throws Exception;

	public AxisInfo browseAxis(Axis axis) throws Exception;
	
	public int createMetadata(Theme theme, String metadataName, List<Metric> metrics, RepositoryDirectory target) throws Exception;
 } 
