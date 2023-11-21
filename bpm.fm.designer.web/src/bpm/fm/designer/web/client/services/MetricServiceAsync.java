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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetricServiceAsync {

	void addAxe(Axis axe, AsyncCallback<Axis> callback);

	void addLevel(Level level, AsyncCallback<Void> callback);

	void deleteLevel(Level level, AsyncCallback<Void> callback);

	void getAxe(int id, AsyncCallback<Axis> callback);

	void deleteAxe(Axis ds, AsyncCallback<Void> asyncCallback);

	void getAxes(int obsId, int themeId, AsyncCallback<List<Axis>> asyncCallback);

	void getMetrics(int obsId, int themeId, AsyncCallback<List<Metric>> asyncCallback);

	void addMetric(Metric metric, AsyncCallback<Metric> asyncCallback);

	void deleteMetric(Metric ds, AsyncCallback<Void> asyncCallback);

	void getMetric(int id, AsyncCallback<Metric> asyncCallback);

	void updateMetric(Metric metric, AsyncCallback<Void> asyncCallback);

//	void addAlert(MetricAlert alert, AsyncCallback<Void> asyncCallback);

//	void getAlerts(int metricId, AsyncCallback<List<Alert>> callback);

	void getDatabaseStructure(int datasourceId, AsyncCallback<List<DatabaseTable>> callback);

	void addTheme(Theme theme, AsyncCallback<Void> asyncCallback);

	void getThemes(AsyncCallback<List<Theme>> asyncCallback);

	void updateGroupForTheme(List<Group> groups, Theme them, AsyncCallback<Void> callback);

	void updateThemeForAxis(List<Theme> themes, Axis axeMetric, AsyncCallback<Void> asyncCallback);

	void updateThemeForMetric(List<Theme> themes, Metric axeMetric, AsyncCallback<Void> asyncCallback);

	void getCreateQueries(Metric metric, AsyncCallback<MetricSqlQueries> callback);

	void executeQueries(MetricSqlQueries queries, AsyncCallback<HashMap<String, Exception>> callback);

	void addObservatory(Observatory observatory, AsyncCallback<Void> asyncCallback);

	void updateGroupForObservatory(List<Group> groups, Observatory observatory, AsyncCallback<Void> asyncCallback);

	void getObservatories(AsyncCallback<List<Observatory>> asyncCallback);

	void updateObservatories(List<Observatory> observatories, AsyncCallback<Void> asyncCallback);

	void deleteAlert(Alert alert, AsyncCallback<Void> callback);

	void deleteObservatory(Observatory ds, AsyncCallback<Void> asyncCallback);

	void testConnection(Datasource ds, AsyncCallback<Boolean> callback);

	void addMetricAction(MetricAction action, AsyncCallback<Void> callback);

	void deleteMetricAction(MetricAction action, AsyncCallback<Void> callback);

	void deleteMetricMap(MetricMap ds, AsyncCallback<Void> asyncCallback);

	void addMetricMap(MetricMap map, AsyncCallback<Void> asyncCallback);

	void browseAxis(Axis axis, AsyncCallback<AxisInfo> asyncCallback);
	
	void createMetadata(Theme theme, String metadataName, List<Metric> metrics, RepositoryDirectory target, AsyncCallback<Integer> asyncCallback);

}
