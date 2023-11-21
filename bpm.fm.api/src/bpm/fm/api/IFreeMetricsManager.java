package bpm.fm.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;
import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.GroupObservatory;
import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricMap;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeAxis;
import bpm.fm.api.model.ThemeMetric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.fm.api.model.utils.MetricValue;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.FMMapValueBean;
import bpm.vanilla.platform.core.beans.FMMetricBean;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * A component to get back informations from FreeMetrics
 * @author Marc Lanquetin
 *
 */
public interface IFreeMetricsManager {
	
	public static enum ActionType implements IXmlActionType{

		OLD_GET_VALUES(Level.DEBUG), OLD_GET_METRICS(Level.DEBUG), GET_AXIS(Level.DEBUG), GET_METRICS(Level.DEBUG), GET_LEVEL(Level.DEBUG), GET_LEVELS(Level.DEBUG), 
		GET_ALERT(Level.DEBUG), ADD_AXIS(Level.INFO), ADD_METRIC(Level.INFO), ADD_LEVEL(Level.INFO), ADD_ALERT(Level.INFO), UPDATE_AXIS(Level.INFO), 
		UPDATE_METRIC(Level.INFO), UPDATE_LEVEL(Level.INFO), UPDATE_ALERT(Level.INFO), DELETE_AXIS(Level.INFO), DELETE_METRIC(Level.INFO), 
		DELETE_LEVEL(Level.INFO), DELETE_ALERTE(Level.INFO), GET_VALUES_BY_DATE(Level.DEBUG), GET_VALUES_BY_METRIC_AND_DATE(Level.DEBUG), 
		GET_VALUES_BY_AXIS_AND_DATE(Level.DEBUG), GET_VALUES_BY_METRIC_AXIS_AND_DATE(Level.DEBUG), GET_VALUES_PREVIOUSVALUES_BY_DATE(Level.DEBUG), 
		GET_VALUES_PREVIOUSVALUES_BY_METRIC_AND_DATE(Level.DEBUG), GET_VALUES_PREVIOUSVALUES_BY_AXIS_AND_DATE(Level.DEBUG), 
		GET_VALUES_PREVIOUSVALUES_BY_METRIC_AXIS_AND_DATE(Level.DEBUG), GET_VALUES_PREVIOUSVALUES_BY_METRIC_LEVEL_DATE(Level.DEBUG), 
		GET_VALUES_BY_METRIC_LEVEL_DATE_INTERVAL(Level.DEBUG), GET_VALUES_BY_METRIC_DATE_INTERVAL(Level.DEBUG), GET_VALUES_FOR_LOADER(Level.DEBUG), GET_VALUES_FOR_LOADER_WITH_END_DATE(Level.DEBUG), 
		UPDATE_LOADER_VALUES(Level.INFO), GET_VALUES_BY_METRIC_AXIS_INTERVAL(Level.DEBUG), ADD_THEME(Level.INFO), UPDATE_THEME(Level.INFO), 
		DELETE_THEME(Level.INFO), ADD_GROUP_OBSERVATORY(Level.INFO), DELETE_GROUP_OBSERVATORY(Level.INFO), GET_THEME(Level.DEBUG), 
		GET_GROUP_OBSERVATORY(Level.DEBUG), ADD_THEME_AXIS(Level.INFO), DELETE_THEME_AXIS(Level.INFO), GET_THEME_AXIS(Level.DEBUG), ADD_THEME_METRIC(Level.INFO), 
		DELETE_THEME_METRIC(Level.INFO), GET_THEME_METRIC(Level.DEBUG), GET_THEME_BY_ID(Level.DEBUG), ADD_OBSERVATORY(Level.INFO), GET_OBSERVATORIES(Level.DEBUG), 
		GET_OBSERVATORY_BY_ID(Level.DEBUG), GET_OBSERVATORY_BY_GROUP(Level.DEBUG), UPDATE_OBSERVATORY(Level.INFO), DELETE_OBSERVATORY(Level.INFO),
		GET_CREATE_QUERIES_FOR_METRIC(Level.DEBUG), EXECUTE_QUERIES(Level.DEBUG), GET_THEME_BY_GROUP(Level.DEBUG), GET_GROUP_OBSERVATORY_BY_OBSID(Level.DEBUG),		
		ADD_COMMENT(Level.INFO), GET_COMMENTS(Level.DEBUG), GET_COMMENTS_BY_DATE_METRIC(Level.DEBUG), CHECK_ALERTS(Level.DEBUG), RESOLVE_ALERT(Level.INFO), 
		GET_RAISED_ALERTS(Level.DEBUG), GET_ALERT_COMMENTS(Level.DEBUG), GET_VALUE_BY_DATE_LEVEL_METRIC(Level.DEBUG), GET_MAP_VALUES(Level.DEBUG), 
		GET_MULTI_MAP_VALUES(Level.DEBUG), GET_LOADER_VALUES_FOR_AXIS(Level.DEBUG), GET_LOADER_AXIS(Level.DEBUG), ADD_METRIC_ACTION(Level.INFO), 
		DELETE_METRIC_ACTION(Level.INFO), ADD_MAP(Level.INFO), DELETE_MAP(Level.INFO), GET_COMPLEX_MAPS(Level.DEBUG), UPDATE_COMPLEX_MAP(Level.INFO), 
		SAVE_COMPLEX_MAP(Level.INFO), DELETE_COMPLEX_MAP(Level.INFO), GET_COMPLEX_MAP_METRICS_BY_METRICS(Level.DEBUG), GET_COMPLEX_MAP_BY_ID(Level.DEBUG), CREATE_AXE_ETL_ARCHITECT(Level.DEBUG), CREATE_AXE_ETL_METADATA(Level.DEBUG);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}

		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	/**
	 * Get freemetrics values for a map, a metric and a date
	 * @param metricId
	 * @param mapId
	 * @param date
	 * @param fmContext
	 * @return
	 * @throws Exception
	 */
	public List<FMMapValueBean> getFreeMetricsValues(int metricId, int mapId, Date date, FMContext fmContext) throws Exception;
	
	/**
	 * Get possible metrics for a map
	 * @param mapId
	 * @param fmContext
	 * @return
	 * @throws Exception
	 */
	public List<FMMetricBean> getMetrics(int mapId, FMContext fmContext) throws Exception;
	
	/**
	 * Get Metrics
	 * @return
	 * @throws Exception
	 */
	public List<Metric> getMetrics() throws Exception;
	
	/**
	 * Get the axes
	 * @return
	 * @throws Exception
	 */
	public List<Axis> getAxis() throws Exception;
	
	/**
	 * Add a new metric
	 * @param metric
	 * @throws Exception
	 */
	public Metric addMetric(Metric metric) throws Exception;
	
	/**
	 * Add an axe
	 * @param axe
	 * @throws Exception
	 */
	public Axis addAxis(Axis axe) throws Exception;
	
	public Metric updateMetric(Metric metric) throws Exception;
	
	public Axis updateAxis(Axis axe) throws Exception;
	
	public void deleteMetric(Metric metric) throws Exception;
	
	public void deleteAxis(Axis axe) throws Exception;
	
	public Metric getMetric(int id) throws Exception;
	
	public Axis getAxis(int id) throws Exception;
	
	public List<bpm.fm.api.model.Level> getLevels(int axeId) throws Exception;
	
	public bpm.fm.api.model.Level getLevel(int id) throws Exception;
	
	public void addLevel(bpm.fm.api.model.Level level) throws Exception;
	
	public void deleteLevel(bpm.fm.api.model.Level level) throws Exception;
	
	public void updateLevel(bpm.fm.api.model.Level level) throws Exception;
	
	/**
	 * Return one value per metric for the given date
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, MetricValue> getValuesByDate(Date date, int groupId) throws Exception;
	
	/**
	 * Return one value for the given metric and date
	 * @param date
	 * @param metricId
	 * @return
	 * @throws Exception
	 */
	public MetricValue getValueByMetricAndDate(Date date, int metricId, int groupId) throws Exception;
	
	/**
	 * Return one value per metric and axis value for the given date
	 * @param date
	 * @param axisId
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxis(Date date, int axisId, int groupId) throws Exception;
	
	/**
	 * Return one value per axis value for the given metric and date
	 * @param date
	 * @param axisId
	 * @param metricId
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception;
	
	/**
	 * Return the actual and the 12 previous values per metric for the given date
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDate(Date date, int groupId) throws Exception;
	
	/**
	 * Return the actual and the 12 previous values for the given metric and date
	 * @param date
	 * @param metricId
	 * @return
	 * @throws Exception
	 */
	public List<MetricValue> getValueAndPreviousByMetricAndDate(Date date, int metricId, int groupId) throws Exception;
	
	/**
	 * Return the actual and the 12 previous values per metric for the given axis and date
	 * @param date
	 * @param axisId
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxis(Date date, int axisId, int groupId) throws Exception;
	
	/**
	 * Return the actual and the 12 previous values for the given axis, metric and date
	 * @param date
	 * @param axisId
	 * @param metricId
	 * @return
	 * @throws Exception
	 */
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception;
	
	/**
	 * used to get the values for FMLoaderWeb
	 * @param metricIds
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date date) throws Exception;
	
	/**
	 * used to get the values for FMLoaderWeb
	 * @param metricIds
	 * @param date
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date firstDate, Date endDate) throws Exception;
	
//	public List<Alert> getAlerts(int metricId) throws Exception;
	
//	public void addAlert(Alert alert) throws Exception;
	
//	public void updateAlert(Alert alert) throws Exception;
	 
	public void deleteAlert(Alert alert) throws Exception;

	public List<Exception> updateValuesFromLoader(LoaderDataContainer values, String date) throws Exception;
	
	public Theme addTheme(Theme theme) throws Exception;
	
	public void addGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception;
	
	public void deleteGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception;
	
	public void updateTheme(Theme theme) throws Exception;
	
	public void deleteTheme(Theme theme) throws Exception;
	
	public List<Theme> getThemes() throws Exception;
	
	public List<Theme> getThemesByGroup(int groupId) throws Exception;
	
	public List<GroupObservatory> getGroupObservatoryByObservatory(int observatoryId) throws Exception;
	
	public void addThemeAxis(List<ThemeAxis> themeAxis) throws Exception;
	
	public void addThemeMetric(List<ThemeMetric> themeMetric) throws Exception;
	
	public void deleteThemeAxis(List<ThemeAxis> themeAxis) throws Exception;
	
	public void deleteThemeMetric(List<ThemeMetric> themeMetrics) throws Exception;
	
	public List<ThemeAxis> getThemeAxisByAxis(int axisId) throws Exception;
	
	public List<ThemeMetric> getThemeMetricByMetric(int metricId) throws Exception;
	
	public Theme getThemeById(int id) throws Exception;
	
	public MetricSqlQueries getCreateQueries(int metricId) throws Exception;
	
	public HashMap<String, Exception> executeQueries(MetricSqlQueries queries) throws Exception;
	
	public HashMap<Metric, List<MetricValue>> getValuesByMetricAxisAndDateInterval(int metricId, int axisId, Date startDate, Date endDate) throws Exception;
	
	public List<Observatory> getObservatories() throws Exception;
	
	public List<Observatory> getObservatoriesByGroup(int groupId) throws Exception;
	
	public Observatory getObservatoryById(int id) throws Exception;
	
	public Observatory addObservatory(Observatory observatory) throws Exception;
	
	public void updateObservatory(Observatory observatory) throws Exception;
	
	public void deleteObservatory(Observatory observatory) throws Exception;
	
	public void addComment(Comment comment) throws Exception;
	
	public List<Comment> getComments() throws Exception;
	
	public List<Comment> getCommentsByDateMetric(Date date, int metricId) throws Exception;
	
//	public void checkAlerts(int gatewayId) throws Exception;
	
	public void addAlertComment(AlertRaised alert, String comment, boolean resolve, int userId) throws Exception;
	
	/**
	 * Return the raised alerts
	 * @param withResolved if true, return the resolved alert also
	 * @throws Exception
	 */
	public List<AlertRaised> getRaisedAlert(boolean withResolved) throws Exception;
	
	public List<CommentAlert> getAlertComments(int raisedAlertId) throws Exception;
	
	public List<MetricValue> getValuesByDateAndLevelAndMetric(Date date, int levelId, int metricId, int groupId) throws Exception;
	
	public List<LoaderDataContainer> getLoaderValuesForAxes(List<Metric> metrics, List<Axis> axes, Date startDate, Date endDate) throws Exception;
	
	public AxisInfo  getLoaderAxe(int idAxis) throws Exception;
	
	public void addMetricAction(MetricAction action) throws Exception;
	
	public void deleteMetricAction(MetricAction action) throws Exception;
	
	public void addMetricMap(MetricMap map) throws Exception;
	
	public void deleteMetricMap(MetricMap map) throws Exception;
	
	public List<MapZoneValue> getMapZoneValues(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception;
	
	public List<ComplexObjectViewer> getMultiMapZoneValues(List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, Date startDate, Date endDate, List<LevelMember> filters, int groupId) throws Exception;

	public List<ComplexMapMetric> getComplexMapMetricsByMetrics(List<Metric> metrics) throws Exception;

	public ComplexMap getComplexMapById(int id) throws Exception;


	public List<ComplexMap> getAllComplexMaps() throws Exception;
	
	public void updateComplexMap(ComplexMap map) throws Exception;
	
	public void saveComplexMap(ComplexMap map) throws Exception;
	
	public void deleteComplexMap(ComplexMap map) throws Exception;

	public List<MetricValue> getValuesAndPreviousByDateAndLevelAndMetric(Date date, int metricId, int levelId, int groupId) throws Exception;

	public List<MetricValue> getValuesByMetricAndLevelAndDateInterval(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception;
	
	public List<MetricValue> getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId) throws Exception;

	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex) throws Exception;
	
	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex) throws Exception;
	
}   
