package bpm.fm.runtime;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Comment;
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
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.FMMapValueBean;
import bpm.vanilla.platform.core.beans.FMMetricBean;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

import com.thoughtworks.xstream.XStream;

public class FreeMetricsServlet extends HttpServlet {

	private static final long serialVersionUID = -2932618062804864382L;

	private IVanillaLogger logger;
	private IFreeMetricsManager component;

	private XStream xstream;

	public FreeMetricsServlet(FreeMetricsManagerComponent componentProvider, IVanillaLogger logger) {
		this.logger = logger;
		this.component = componentProvider;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing FreeMetricsServlet...");
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType", IXmlActionType.class, IFreeMetricsManager.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
		xstream.alias("fmcontext", FMContext.class);
		xstream.alias("fmvalue", FMMapValueBean.class);
		xstream.alias("fmmetrics", FMMetricBean.class);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if(action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if(!(action.getActionType() instanceof IFreeMetricsManager.ActionType)) {
				throw new Exception("ActionType not a IFreeMetricsManager");
			}

			Date startDate = new Date();

			IFreeMetricsManager.ActionType type = (IFreeMetricsManager.ActionType) action.getActionType();

			try {
				switch(type) {
					case OLD_GET_VALUES:
						actionResult = getValues(args);
						break;
					case OLD_GET_METRICS:
						actionResult = getMetrics(args);
						break;
					case ADD_AXIS:
						actionResult = component.addAxis((Axis) args.getArguments().get(0));
						break;
					case ADD_METRIC:
						actionResult = component.addMetric((Metric) args.getArguments().get(0));
						break;
					case DELETE_AXIS:
						component.deleteAxis((Axis) args.getArguments().get(0));
						break;
					case DELETE_METRIC:
						component.deleteMetric((Metric) args.getArguments().get(0));
						break;
					case GET_AXIS:
						if(args.getArguments().size() > 0) {
							actionResult = component.getAxis((Integer) args.getArguments().get(0));
						}
						else {
							actionResult = component.getAxis();
						}
						break;
					case GET_METRICS:
						if(args.getArguments().size() > 0) {
							actionResult = component.getMetric((Integer) args.getArguments().get(0));
						}
						else {
							actionResult = component.getMetrics();
						}
						break;
					case UPDATE_AXIS:
						actionResult = component.updateAxis((Axis) args.getArguments().get(0));
						break;
					case UPDATE_METRIC:
						actionResult = component.updateMetric((Metric) args.getArguments().get(0));
						break;
					case ADD_LEVEL:
						component.addLevel((Level) args.getArguments().get(0));
						break;
					case DELETE_LEVEL:
						component.deleteLevel((Level) args.getArguments().get(0));
						break;
					case GET_LEVEL:
						actionResult = component.getLevel((Integer) args.getArguments().get(0));
						break;
					case GET_LEVELS:
						actionResult = component.getLevels((Integer) args.getArguments().get(0));
						break;
					case UPDATE_LEVEL:
						component.updateLevel((Level) args.getArguments().get(0));
						break;
					case GET_VALUES_BY_DATE:
						actionResult = component.getValuesByDate((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1));
						break;
					case GET_VALUES_BY_AXIS_AND_DATE:
						actionResult = component.getValuesByDateAndAxis((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
						break;
					case GET_VALUES_BY_METRIC_AND_DATE:
						actionResult = component.getValueByMetricAndDate((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
						break;
					case GET_VALUES_BY_METRIC_AXIS_AND_DATE:
						actionResult = component.getValuesByDateAndAxisAndMetric((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
						break;
					case GET_VALUES_PREVIOUSVALUES_BY_AXIS_AND_DATE:
						actionResult = component.getValuesAndPreviousByDateAndAxis((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
						break;
					case GET_VALUES_PREVIOUSVALUES_BY_DATE:
						actionResult = component.getValuesAndPreviousByDate((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1));
						break;
					case GET_VALUES_PREVIOUSVALUES_BY_METRIC_AND_DATE:
						actionResult = component.getValueAndPreviousByMetricAndDate((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
						break;
					case GET_VALUES_PREVIOUSVALUES_BY_METRIC_AXIS_AND_DATE:
						actionResult = component.getValuesAndPreviousByDateAndAxisAndMetric((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
						break;
					case GET_VALUES_FOR_LOADER:
						actionResult = component.getValuesForLoader((List<Integer>) args.getArguments().get(0), (Date) args.getArguments().get(1));
						break;
					case GET_VALUES_FOR_LOADER_WITH_END_DATE:
						actionResult = component.getValuesForLoader((List<Integer>) args.getArguments().get(0), (Date) args.getArguments().get(1), (Date) args.getArguments().get(2));
						break;
//					case GET_ALERT:
//						actionResult = component.getAlerts((Integer) args.getArguments().get(0));
//						break;
//					case ADD_ALERT:
//						component.addAlert((MetricAlert) args.getArguments().get(0));
//						break;
					case DELETE_ALERTE:
						component.deleteAlert((Alert) args.getArguments().get(0));
						break;
//					case UPDATE_ALERT:
//						component.updateAlert((MetricAlert) args.getArguments().get(0));
//						break;
					case UPDATE_LOADER_VALUES:
						component.updateValuesFromLoader((LoaderDataContainer) args.getArguments().get(0), (String) args.getArguments().get(1));
						break;
					case ADD_THEME:
						actionResult = component.addTheme((Theme) args.getArguments().get(0));
						break;
					case DELETE_THEME:
						component.deleteTheme((Theme) args.getArguments().get(0));
						break;
					case GET_THEME:
						actionResult = component.getThemes();
						break;
					case GET_THEME_BY_GROUP:
						actionResult = component.getThemesByGroup((Integer) args.getArguments().get(0));
						break;
					case UPDATE_THEME:
						component.updateTheme((Theme) args.getArguments().get(0));
						break;
					case GET_THEME_METRIC:
						actionResult = component.getThemeMetricByMetric((Integer) args.getArguments().get(0));
						break;
					case ADD_THEME_AXIS:
						component.addThemeAxis((List<ThemeAxis>) args.getArguments().get(0));
						break;
					case ADD_THEME_METRIC:
						component.addThemeMetric((List<ThemeMetric>) args.getArguments().get(0));
						break;
					case DELETE_THEME_AXIS:
						component.deleteThemeAxis((List<ThemeAxis>) args.getArguments().get(0));
						break;
					case DELETE_THEME_METRIC:
						component.deleteThemeMetric((List<ThemeMetric>) args.getArguments().get(0));
						break;
					case GET_THEME_AXIS:
						actionResult = component.getThemeAxisByAxis((Integer) args.getArguments().get(0));
						break;
					case GET_THEME_BY_ID:
						actionResult = component.getThemeById((Integer) args.getArguments().get(0));
						break;
					case GET_CREATE_QUERIES_FOR_METRIC:
						actionResult = component.getCreateQueries((Integer) args.getArguments().get(0));
						break;
					case EXECUTE_QUERIES:
						actionResult = component.executeQueries((MetricSqlQueries) args.getArguments().get(0));
						break;
					case GET_VALUES_BY_METRIC_AXIS_INTERVAL:
						actionResult = component.getValuesByMetricAxisAndDateInterval((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3));
						break;
					case ADD_GROUP_OBSERVATORY:
						component.addGroupObservatories((List<GroupObservatory>) args.getArguments().get(0));
						break;
					case ADD_OBSERVATORY:
						actionResult = component.addObservatory((Observatory) args.getArguments().get(0));
						break;
					case DELETE_GROUP_OBSERVATORY:
						component.deleteGroupObservatories((List<GroupObservatory>) args.getArguments().get(0));
						break;
					case DELETE_OBSERVATORY:
						component.deleteObservatory((Observatory) args.getArguments().get(0));
						break;
					case GET_GROUP_OBSERVATORY:
						// XXX
						break;
					case GET_GROUP_OBSERVATORY_BY_OBSID:
						actionResult = component.getGroupObservatoryByObservatory((Integer) args.getArguments().get(0));
						break;
					case GET_OBSERVATORIES:
						actionResult = component.getObservatories();
						break;
					case GET_OBSERVATORY_BY_GROUP:
						actionResult = component.getObservatoriesByGroup((Integer) args.getArguments().get(0));
						break;
					case GET_OBSERVATORY_BY_ID:
						actionResult = component.getObservatoryById((Integer) args.getArguments().get(0));
						break;
					case UPDATE_OBSERVATORY:
						component.updateObservatory((Observatory) args.getArguments().get(0));
						break;
					case ADD_COMMENT:
						component.addComment((Comment) args.getArguments().get(0));
						break;
					case GET_COMMENTS:
						actionResult = component.getComments();
						break;
					case GET_COMMENTS_BY_DATE_METRIC:
						actionResult = component.getCommentsByDateMetric((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1));
						break;
					case RESOLVE_ALERT:
						component.addAlertComment((AlertRaised) args.getArguments().get(0), (String) args.getArguments().get(1), ((Boolean) args.getArguments().get(2)), (Integer) args.getArguments().get(3));
						break;
					case GET_RAISED_ALERTS:
						actionResult = component.getRaisedAlert((Boolean) args.getArguments().get(0));
						break;
					case GET_ALERT_COMMENTS:
						actionResult = component.getAlertComments((Integer) args.getArguments().get(0));
						break;
					case GET_VALUE_BY_DATE_LEVEL_METRIC:
						actionResult = component.getValuesByDateAndLevelAndMetric((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
						break;
					case GET_LOADER_VALUES_FOR_AXIS:
						actionResult = component.getLoaderValuesForAxes((List<Metric>) args.getArguments().get(0), (List<Axis>) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3));
						break;
					case GET_LOADER_AXIS:
						actionResult = component.getLoaderAxe((Integer) args.getArguments().get(0));
						break;
					case ADD_METRIC_ACTION:
						component.addMetricAction((MetricAction) args.getArguments().get(0));
						break;
					case DELETE_METRIC_ACTION:
						component.deleteMetricAction((MetricAction) args.getArguments().get(0));
						break;
					case ADD_MAP:
						component.addMetricMap((MetricMap) args.getArguments().get(0));
						break;
					case DELETE_MAP:
						component.deleteMetricMap((MetricMap) args.getArguments().get(0));
						break;
					case GET_MAP_VALUES:
						actionResult = component.getMapZoneValues((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3), (Integer) args.getArguments().get(4));
						break;
					case GET_VALUES_PREVIOUSVALUES_BY_METRIC_LEVEL_DATE:
						actionResult = component.getValuesAndPreviousByDateAndLevelAndMetric((Date) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
						break;
					case GET_VALUES_BY_METRIC_DATE_INTERVAL:
						actionResult = component.getValuesByMetricAndDateInterval((Integer) args.getArguments().get(0), (Date) args.getArguments().get(1), (Date) args.getArguments().get(2), (Integer) args.getArguments().get(3));
						break;
					case GET_VALUES_BY_METRIC_LEVEL_DATE_INTERVAL:
						actionResult = component.getValuesByMetricAndLevelAndDateInterval((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3), (Integer) args.getArguments().get(4));
						break;
					case GET_MULTI_MAP_VALUES:
						List<ComplexObjectViewer> res = component.getMultiMapZoneValues((List<ComplexMapMetric>) args.getArguments().get(0), (List<ComplexMapLevel>) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3), (List<LevelMember>) args.getArguments().get(4), (Integer) args.getArguments().get(5));
						serializeResult(res, resp.getOutputStream());
						break;
					case GET_COMPLEX_MAPS:
						actionResult = component.getAllComplexMaps();
						break;
					case UPDATE_COMPLEX_MAP:
						component.updateComplexMap((ComplexMap) args.getArguments().get(0));
						break;
					case SAVE_COMPLEX_MAP:
						component.saveComplexMap((ComplexMap) args.getArguments().get(0));
						break;
					case DELETE_COMPLEX_MAP:
						component.deleteComplexMap((ComplexMap) args.getArguments().get(0));
						break;
					case GET_COMPLEX_MAP_METRICS_BY_METRICS:
						actionResult = component.getComplexMapMetricsByMetrics((List<Metric>) args.getArguments().get(0));
						break;
					case GET_COMPLEX_MAP_BY_ID:
						actionResult = component.getComplexMapById((int) args.getArguments().get(0));
						break;
					case CREATE_AXE_ETL_ARCHITECT:
						actionResult = component.createAxeETL((String) args.getArguments().get(0), (IRepositoryContext) args.getArguments().get(1), (HasItemLinked) args.getArguments().get(2), (Contract) args.getArguments().get(3), (HashMap<String, Integer>) args.getArguments().get(4));
						break;
					case CREATE_AXE_ETL_METADATA:
						actionResult = component.createAxeETL((String) args.getArguments().get(0), (IRepositoryContext) args.getArguments().get(1), (HasItemLinked) args.getArguments().get(2), (RepositoryItem) args.getArguments().get(3), (String) args.getArguments().get(4), (HashMap<String, Integer>) args.getArguments().get(5));
						break;
				}
			} catch(Exception ex) {
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			Date endDate = new Date();

			logger.trace("Execution time for : " + type.toString() + " -> " + (endDate.getTime() - startDate.getTime()) + " with arguments -> " + args.toString());

			if(actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();
			}

		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private void serializeResult(List<ComplexObjectViewer> res, ServletOutputStream outputStream) throws Exception {
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
		}
		
		try {
			oos.writeObject(res);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
	}

	private Object getMetrics(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments() == null || args.getArguments().size() < 2) {
			throw new Exception("Missing argument for the getMetrics action.");
		}
		Integer mapId = null;
		try {
			mapId = (Integer) args.getArguments().get(0);
		} catch(Exception e) {
			throw new Exception("Invalid argument (applicationIds) : ", e);
		}

		FMContext fmcontext = null;
		try {
			fmcontext = (FMContext) args.getArguments().get(1);
		} catch(Exception e) {
			throw new Exception("Invalid argument (fmContext) : ", e);
		}

		return component.getMetrics(mapId, fmcontext);
	}

	private Object getValues(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments() == null || args.getArguments().size() < 4) {
			throw new Exception("Missing argument for the getValues action.");
		}

		Integer fmmetric = null;
		try {
			fmmetric = (Integer) args.getArguments().get(0);
		} catch(Exception e) {
			throw new Exception("Invalid argument (fmMetric) : ", e);
		}

		Integer mapId = null;
		try {
			mapId = (Integer) args.getArguments().get(1);
		} catch(Exception e) {
			throw new Exception("Invalid argument (applicationIds) : ", e);
		}

		Date date = null;
		try {
			date = (Date) args.getArguments().get(2);
		} catch(Exception e) {
			throw new Exception("Invalid argument (date) : ", e);
		}

		FMContext fmcontext = null;
		try {
			fmcontext = (FMContext) args.getArguments().get(3);
		} catch(Exception e) {
			throw new Exception("Invalid argument (fmContext) : ", e);
		}

		return component.getFreeMetricsValues(fmmetric, mapId, date, fmcontext);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
