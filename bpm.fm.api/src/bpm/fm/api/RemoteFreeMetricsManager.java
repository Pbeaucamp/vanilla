package bpm.fm.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.HttpURLConnection;
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
import bpm.fm.api.model.Level;
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
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.FMMapValueBean;
import bpm.vanilla.platform.core.beans.FMMetricBean;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteFreeMetricsManager implements IFreeMetricsManager {

	public static class FreeMetricsHttpComunicator extends HttpCommunicator {
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_FREEMETRICS);

		}
	}
	
	private FreeMetricsHttpComunicator httpCommunicator;
	private static XStream xstream;
	
	private int groupId = -1;

	public RemoteFreeMetricsManager(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new FreeMetricsHttpComunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}
	
	public RemoteFreeMetricsManager(IVanillaContext vanillaContext) {
		this(vanillaContext.getVanillaUrl(), vanillaContext.getLogin(), vanillaContext.getPassword());
	}

	static{
		xstream = new XStream();

	}
	
	@Override
	public List<FMMapValueBean> getFreeMetricsValues(int metric, int mapId, Date date, FMContext fmContext) throws Exception {
		XmlAction op = new XmlAction(createArguments(metric, mapId, date, fmContext), IFreeMetricsManager.ActionType.OLD_GET_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<FMMetricBean> getMetrics(int mapId, FMContext fmContext) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapId, fmContext), IFreeMetricsManager.ActionType.OLD_GET_METRICS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}
	
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public List<Metric> getMetrics() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_METRICS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Metric>)xstream.fromXML(xml);
	}

	@Override
	public List<Axis> getAxis() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public Metric addMetric(Metric metric) throws Exception {
		XmlAction op = new XmlAction(createArguments(metric), IFreeMetricsManager.ActionType.ADD_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Metric)xstream.fromXML(xml);
	}

	@Override
	public Axis addAxis(Axis axe) throws Exception {
		XmlAction op = new XmlAction(createArguments(axe), IFreeMetricsManager.ActionType.ADD_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Axis)xstream.fromXML(xml);
	}

	@Override
	public Metric updateMetric(Metric metric) throws Exception {
		XmlAction op = new XmlAction(createArguments(metric), IFreeMetricsManager.ActionType.UPDATE_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Metric)xstream.fromXML(xml);
	}

	@Override
	public Axis updateAxis(Axis axe) throws Exception {
		XmlAction op = new XmlAction(createArguments(axe), IFreeMetricsManager.ActionType.UPDATE_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Axis)xstream.fromXML(xml);
	}

	@Override
	public void deleteMetric(Metric metric) throws Exception {
		XmlAction op = new XmlAction(createArguments(metric), IFreeMetricsManager.ActionType.DELETE_METRIC);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteAxis(Axis axe) throws Exception {
		XmlAction op = new XmlAction(createArguments(axe), IFreeMetricsManager.ActionType.DELETE_AXIS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public Metric getMetric(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_METRICS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Metric)xstream.fromXML(xml);
	}

	@Override
	public Axis getAxis(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Axis)xstream.fromXML(xml);
	}

	@Override
	public List<Level> getLevels(int axeId) throws Exception {
		XmlAction op = new XmlAction(createArguments(axeId), IFreeMetricsManager.ActionType.GET_LEVELS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Level>)xstream.fromXML(xml);
	}

	@Override
	public Level getLevel(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_LEVEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Level)xstream.fromXML(xml);
	}

	@Override
	public void addLevel(Level level) throws Exception {
		XmlAction op = new XmlAction(createArguments(level), IFreeMetricsManager.ActionType.ADD_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteLevel(Level level) throws Exception {
		XmlAction op = new XmlAction(createArguments(level), IFreeMetricsManager.ActionType.DELETE_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void updateLevel(Level level) throws Exception {
		XmlAction op = new XmlAction(createArguments(level), IFreeMetricsManager.ActionType.UPDATE_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public HashMap<Metric, MetricValue> getValuesByDate(Date date, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, MetricValue>)xstream.fromXML(xml);
	}

	@Override
	public MetricValue getValueByMetricAndDate(Date date, int metricId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, metricId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_METRIC_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (MetricValue)xstream.fromXML(xml);
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxis(Date date, int axisId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, axisId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_AXIS_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, axisId, metricId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_METRIC_AXIS_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDate(Date date, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, groupId), IFreeMetricsManager.ActionType.GET_VALUES_PREVIOUSVALUES_BY_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
	}

	@Override
	public List<MetricValue> getValueAndPreviousByMetricAndDate(Date date, int metricId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, metricId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_PREVIOUSVALUES_BY_METRIC_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetricValue>)xstream.fromXML(xml);
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxis(Date date, int axisId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, axisId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_PREVIOUSVALUES_BY_AXIS_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, axisId, metricId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_PREVIOUSVALUES_BY_METRIC_AXIS_AND_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
	}

	@Override
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date date) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricIds, date), IFreeMetricsManager.ActionType.GET_VALUES_FOR_LOADER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<LoaderDataContainer>)xstream.fromXML(xml);
	}

	@Override
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date firstDate, Date endDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricIds, firstDate, endDate), IFreeMetricsManager.ActionType.GET_VALUES_FOR_LOADER_WITH_END_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<LoaderDataContainer>)xstream.fromXML(xml);
	}

//	@Override
//	public List<MetricAlert> getAlerts(int metricId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(metricId), IFreeMetricsManager.ActionType.GET_ALERT);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		return (List<MetricAlert>)xstream.fromXML(xml);
//	}
//
//	@Override
//	public void addAlert(MetricAlert alert) throws Exception {
//		XmlAction op = new XmlAction(createArguments(alert), IFreeMetricsManager.ActionType.ADD_ALERT);
//		httpCommunicator.executeAction(op, xstream.toXML(op), false);
//	}
//
//	@Override
//	public void updateAlert(MetricAlert alert) throws Exception {
//		XmlAction op = new XmlAction(createArguments(alert), IFreeMetricsManager.ActionType.UPDATE_ALERT);
//		httpCommunicator.executeAction(op, xstream.toXML(op), false);
//	}

	@Override
	public void deleteAlert(Alert alert) throws Exception {
		XmlAction op = new XmlAction(createArguments(alert), IFreeMetricsManager.ActionType.DELETE_ALERTE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<Exception> updateValuesFromLoader(LoaderDataContainer values, String date) throws Exception {
		XmlAction op = new XmlAction(createArguments(values, date), IFreeMetricsManager.ActionType.UPDATE_LOADER_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Exception>)xstream.fromXML(xml);
	}

	@Override
	public Theme addTheme(Theme theme) throws Exception {
		XmlAction op = new XmlAction(createArguments(theme), IFreeMetricsManager.ActionType.ADD_THEME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Theme) xstream.fromXML(xml);
	}

	@Override
	public void updateTheme(Theme theme) throws Exception {
		XmlAction op = new XmlAction(createArguments(theme), IFreeMetricsManager.ActionType.UPDATE_THEME);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteTheme(Theme theme) throws Exception {
		XmlAction op = new XmlAction(createArguments(theme), IFreeMetricsManager.ActionType.DELETE_THEME);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<Theme> getThemes() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_THEME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Theme>)xstream.fromXML(xml);
	}

	@Override
	public List<Theme> getThemesByGroup(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IFreeMetricsManager.ActionType.GET_THEME_BY_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Theme>)xstream.fromXML(xml);
	}

	@Override
	public void addThemeAxis(List<ThemeAxis> themeAxis) throws Exception {
		XmlAction op = new XmlAction(createArguments(themeAxis), IFreeMetricsManager.ActionType.ADD_THEME_AXIS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addThemeMetric(List<ThemeMetric> themeMetric) throws Exception {
		XmlAction op = new XmlAction(createArguments(themeMetric), IFreeMetricsManager.ActionType.ADD_THEME_METRIC);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteThemeAxis(List<ThemeAxis> themeAxis) throws Exception {
		XmlAction op = new XmlAction(createArguments(themeAxis), IFreeMetricsManager.ActionType.DELETE_THEME_AXIS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteThemeMetric(List<ThemeMetric> themeMetrics) throws Exception {
		XmlAction op = new XmlAction(createArguments(themeMetrics), IFreeMetricsManager.ActionType.DELETE_THEME_METRIC);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<ThemeAxis> getThemeAxisByAxis(int axisId) throws Exception {
		XmlAction op = new XmlAction(createArguments(axisId), IFreeMetricsManager.ActionType.GET_THEME_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ThemeAxis>)xstream.fromXML(xml);
	}

	@Override
	public List<ThemeMetric> getThemeMetricByMetric(int metricId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId), IFreeMetricsManager.ActionType.GET_THEME_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ThemeMetric>)xstream.fromXML(xml);
	}

	@Override
	public Theme getThemeById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_THEME_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Theme)xstream.fromXML(xml);
	}

	@Override
	public MetricSqlQueries getCreateQueries(int metricId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId), IFreeMetricsManager.ActionType.GET_CREATE_QUERIES_FOR_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (MetricSqlQueries)xstream.fromXML(xml);
	}

	@Override
	public HashMap<String, Exception> executeQueries(MetricSqlQueries metricSqlQueries) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricSqlQueries), IFreeMetricsManager.ActionType.EXECUTE_QUERIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<String, Exception>)xstream.fromXML(xml);
	}

	public HashMap<Metric, List<MetricValue>> getValuesByMetricAxisAndDateInterval(int metricId, int axisId, Date startDate, Date endDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId, axisId, startDate, endDate), IFreeMetricsManager.ActionType.GET_VALUES_BY_METRIC_AXIS_INTERVAL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<Metric, List<MetricValue>>)xstream.fromXML(xml);
		
	}

	@Override
	public void addGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupObservatory), IFreeMetricsManager.ActionType.ADD_GROUP_OBSERVATORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupObservatory), IFreeMetricsManager.ActionType.DELETE_GROUP_OBSERVATORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<GroupObservatory> getGroupObservatoryByObservatory(int observatoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(observatoryId), IFreeMetricsManager.ActionType.GET_GROUP_OBSERVATORY_BY_OBSID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GroupObservatory>)xstream.fromXML(xml);
	}

	@Override
	public List<Observatory> getObservatories() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_OBSERVATORIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Observatory>)xstream.fromXML(xml);
	}

	@Override
	public List<Observatory> getObservatoriesByGroup(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IFreeMetricsManager.ActionType.GET_OBSERVATORY_BY_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Observatory>)xstream.fromXML(xml);
	}

	@Override
	public Observatory getObservatoryById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_OBSERVATORY_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Observatory)xstream.fromXML(xml);
	}

	@Override
	public Observatory addObservatory(Observatory observatory) throws Exception {
		XmlAction op = new XmlAction(createArguments(observatory), IFreeMetricsManager.ActionType.ADD_OBSERVATORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Observatory)xstream.fromXML(xml);
	}

	@Override
	public void updateObservatory(Observatory observatory) throws Exception {
		XmlAction op = new XmlAction(createArguments(observatory), IFreeMetricsManager.ActionType.UPDATE_OBSERVATORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteObservatory(Observatory observatory) throws Exception {
		XmlAction op = new XmlAction(createArguments(observatory), IFreeMetricsManager.ActionType.DELETE_OBSERVATORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addComment(Comment comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IFreeMetricsManager.ActionType.ADD_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<Comment> getComments() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Comment>)xstream.fromXML(xml);
	}

	@Override
	public List<Comment> getCommentsByDateMetric(Date date, int metricId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, metricId), IFreeMetricsManager.ActionType.GET_COMMENTS_BY_DATE_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Comment>)xstream.fromXML(xml);
	}

//	@Override
//	public void checkAlerts(int gatewayId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gatewayId), IFreeMetricsManager.ActionType.CHECK_ALERTS);
//		httpCommunicator.executeAction(op, xstream.toXML(op), false);
//	}

	@Override
	public void addAlertComment(AlertRaised alert, String comment, boolean resolve, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(alert, comment, resolve, userId), IFreeMetricsManager.ActionType.RESOLVE_ALERT);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<AlertRaised> getRaisedAlert(boolean withResolved) throws Exception {
		XmlAction op = new XmlAction(createArguments(withResolved), IFreeMetricsManager.ActionType.GET_RAISED_ALERTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AlertRaised>)xstream.fromXML(xml);
	}

	@Override
	public List<CommentAlert> getAlertComments(int raisedAlertId) throws Exception {
		XmlAction op = new XmlAction(createArguments(raisedAlertId), IFreeMetricsManager.ActionType.GET_ALERT_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<CommentAlert>)xstream.fromXML(xml);
	}

	@Override
	public List<MetricValue> getValuesByDateAndLevelAndMetric(Date date, int levelId, int metricId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, levelId, metricId, groupId), IFreeMetricsManager.ActionType.GET_VALUE_BY_DATE_LEVEL_METRIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetricValue>)xstream.fromXML(xml);
	}

	@Override
	public List<LoaderDataContainer> getLoaderValuesForAxes(List<Metric> metrics, List<Axis> axes, Date startDate, Date endDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(metrics, axes, startDate, endDate), IFreeMetricsManager.ActionType.GET_LOADER_VALUES_FOR_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<LoaderDataContainer>)xstream.fromXML(xml);
	}
	
	@Override
	public AxisInfo getLoaderAxe(int idAxis) throws Exception {
		XmlAction op = new XmlAction(createArguments(idAxis), IFreeMetricsManager.ActionType.GET_LOADER_AXIS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (AxisInfo)xstream.fromXML(xml);
	}

	@Override
	public void addMetricAction(MetricAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(action), IFreeMetricsManager.ActionType.ADD_METRIC_ACTION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteMetricAction(MetricAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(action), IFreeMetricsManager.ActionType.DELETE_METRIC_ACTION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addMetricMap(MetricMap map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), IFreeMetricsManager.ActionType.ADD_MAP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteMetricMap(MetricMap map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), IFreeMetricsManager.ActionType.DELETE_MAP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<MapZoneValue> getMapZoneValues(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId, levelId, startDate, endDate, groupId), IFreeMetricsManager.ActionType.GET_MAP_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MapZoneValue>)xstream.fromXML(xml);
	}

	@Override
	public List<MetricValue> getValuesAndPreviousByDateAndLevelAndMetric(Date date, int metricId, int levelId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(date, metricId, levelId, groupId), IFreeMetricsManager.ActionType.GET_VALUES_PREVIOUSVALUES_BY_METRIC_LEVEL_DATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetricValue>)xstream.fromXML(xml);
	}

	@Override
	public List<MetricValue> getValuesByMetricAndLevelAndDateInterval(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId, levelId, startDate, endDate, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_METRIC_LEVEL_DATE_INTERVAL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetricValue>)xstream.fromXML(xml);
	}

	@Override
	public List<MetricValue> getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metricId, startDate, endDate, groupId), IFreeMetricsManager.ActionType.GET_VALUES_BY_METRIC_DATE_INTERVAL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetricValue>)xstream.fromXML(xml);
	}
	
	@Override
	public List<ComplexObjectViewer> getMultiMapZoneValues(List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, Date startDate, Date endDate, List<LevelMember> filters, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metrics, levels, startDate, endDate, filters, groupId), IFreeMetricsManager.ActionType.GET_MULTI_MAP_VALUES);
		InputStream is = httpCommunicator.executeActionAsStream(op, xstream.toXML(op), false);
		List<ComplexObjectViewer> res = null;
		if (is != null){
			ObjectInputStream ois = new OsgiContextObjectInputStream(is);
			
			try{
				Object o = ois.readObject();
				if (o instanceof List){
					res = (List<ComplexObjectViewer>)o;
				}
				else{
					throw new Exception("The received object is not a List");
				}
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
			
		}
		
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return res;
	}
	
	private static class OsgiContextObjectInputStream extends ObjectInputStream{

		public OsgiContextObjectInputStream(InputStream in) throws IOException {
			super(in);
		}
		
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc)
				throws IOException, ClassNotFoundException {
			return this.getClass().getClassLoader().loadClass(desc.getName());
		}
	}
	
	@Override
	public List<ComplexMap> getAllComplexMaps() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IFreeMetricsManager.ActionType.GET_COMPLEX_MAPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ComplexMap>)xstream.fromXML(xml);
	}
	
	@Override
	public void updateComplexMap(ComplexMap map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), IFreeMetricsManager.ActionType.UPDATE_COMPLEX_MAP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void saveComplexMap(ComplexMap map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), IFreeMetricsManager.ActionType.SAVE_COMPLEX_MAP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void deleteComplexMap(ComplexMap map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), IFreeMetricsManager.ActionType.DELETE_COMPLEX_MAP);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Override
	public List<ComplexMapMetric> getComplexMapMetricsByMetrics(List<Metric> metrics) throws Exception {
		XmlAction op = new XmlAction(createArguments(metrics), IFreeMetricsManager.ActionType.GET_COMPLEX_MAP_METRICS_BY_METRICS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ComplexMapMetric>)xstream.fromXML(xml);
	}

	@Override
	public ComplexMap getComplexMapById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IFreeMetricsManager.ActionType.GET_COMPLEX_MAP_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ComplexMap)xstream.fromXML(xml);
	}

	@Override
	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex) throws Exception {
		XmlAction op = new XmlAction(createArguments(etlName, ctx, hasItemLinked, selectedContract, columnsIndex), IFreeMetricsManager.ActionType.CREATE_AXE_ETL_ARCHITECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}

	@Override
	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex) throws Exception {
		XmlAction op = new XmlAction(createArguments(etlName, ctx, hasItemLinked, metadata, queryName, columnsIndex), IFreeMetricsManager.ActionType.CREATE_AXE_ETL_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}
}
