package bpm.vanilla.api.runtime.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.MetricValue;
import bpm.vanilla.api.core.IAPIManager.KPIMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.dto.kpi.AxisDTO;
import bpm.vanilla.api.runtime.dto.kpi.LevelDTO;
import bpm.vanilla.api.runtime.dto.kpi.MetricDTO;
import bpm.vanilla.api.runtime.dto.kpi.MetricValueDTO;
import bpm.vanilla.api.runtime.dto.kpi.ObservatoryDTO;
import bpm.vanilla.api.runtime.dto.kpi.ThemeDTO;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
//import bpm.vanilla.platform.core.beans.api.exception.VanillaApiError;
//import bpm.vanilla.platform.core.beans.api.exception.VanillaApiException;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class KPIService {

	private IFreeMetricsManager freeMetricsManager;
	private IVanillaSecurityManager vanillaSecurityManager;

	public KPIService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		this.vanillaSecurityManager = vanillaApi.getVanillaSecurityManager();
		this.freeMetricsManager = new RemoteFreeMetricsManager(vanillaCtx);
	}

	public String dispatchAction(String method, JSONArray parameters) throws Exception {
		KPIMethod kpiMethod = KPIMethod.valueOf(method);

		switch (kpiMethod) {
		case GET_AXIS:
			return getAxis();
		case GET_AXIS_MEMBERS:
			return getAxisMembers(parameters);
		case GET_GROUP_AXIS_VALUE:
			return getKpiAxisValue(parameters);
		case GET_GROUP_AXIS_VALUES:
			return getKpiAxisValues(parameters);
		case GET_KPI_AXIS:
			return getKPIAxis(parameters);
		case GET_KPI_VALUE:
			return getKPIValue(parameters);
		case GET_KPI_VALUES:
			return getKPIValues(parameters);
		case GET_KPIS_BY_GROUP:
			return getKpiListByGroup(parameters);
		case GET_KPIS_BY_OBSERVATORY:
			return getKpiListByObservatoryAsJson(parameters);
		case GET_KPIS_BY_THEME:
			return getKpiListByTheme(parameters);
		case GET_KPIS_BY_USER:
			return getKpiListByUser(parameters);
		case GET_OBSERVATORIES:
			return getObservatories();
		case GET_OBSERVATORIES_BY_GROUP:
			return getObservatoriesByGroupAsJson(parameters);
		case GET_OBSERVATORIES_BY_USER:
			return getObservatoriesByUserAsJson(parameters);
		case GET_THEMES:
			return getThemes();
		case GET_THEMES_BY_GROUP:
			return getThemesByGroupAsJson(parameters);
		case GET_THEMES_BY_OBSERVATORY:
			return getThemesByObservatory(parameters);
		case GET_THEMES_BY_USER:
			return getThemesByUser(parameters);
		default:
			break;
		}
		// TODO: Manage errors
		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}

	private List<Group> getGroups(String login) throws Exception {
		User user = vanillaSecurityManager.getUserByLogin(login);
		return vanillaSecurityManager.getGroups(user);
	}

	private String buildJson(Object item) throws JsonProcessingException {
		// Create the String JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(item);
	}

	public String getAxis() {
		List<Axis> axis;
		try {
			axis = freeMetricsManager.getAxis();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USERS_NOT_FOUND);
		}

		List<AxisDTO> items = this.convertListToDTO(axis, Axis.class, AxisDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getObservatories() {
		List<Observatory> observatories = new ArrayList<>();
		try {
			observatories = freeMetricsManager.getObservatories();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.OBSERVATORIES_NOT_FOUND);
		}
		List<ObservatoryDTO> items = this.convertListToDTO(observatories, Observatory.class, ObservatoryDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	private List<ObservatoryDTO> getObservatoriesByGroup(int groupId) {
		Group group = getGroupById(groupId);

		List<Observatory> observatories = new ArrayList<>();
		try {
			observatories = freeMetricsManager.getObservatoriesByGroup(group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.OBSERVATORIES_NOT_FOUND);
		}
		return this.convertListToDTO(observatories, Observatory.class, ObservatoryDTO.class);
	}

	public String getObservatoriesByGroupAsJson(JSONArray parameters) throws JSONException {
		int groupId = parameters.getInt(0);
		
		List<ObservatoryDTO> items = getObservatoriesByGroup(groupId);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public ArrayList<ObservatoryDTO> getObservatoriesByUser(String login) throws Exception {
		List<Group> groups = getGroups(login);
		Set<ObservatoryDTO> hset = new HashSet<ObservatoryDTO>();
		for (Group group : groups) {
			List<ObservatoryDTO> obs = this.getObservatoriesByGroup(group.getId());
			for (ObservatoryDTO ob : obs) {
				hset.add(ob);
				/*
				 * if (!hset.add(ob)) {
				 * System.out.println("Doublon observatory : " + ob); }
				 */
			}
		}

		return new ArrayList<ObservatoryDTO>(hset);
	}

	public String getObservatoriesByUserAsJson(JSONArray parameters) throws Exception {
		String login = parameters.getString(0);
		
		List<ObservatoryDTO> items = getObservatoriesByUser(login);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getThemes() {
		List<Theme> themes = new ArrayList<>();
		try {
			themes = freeMetricsManager.getThemes();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.THEMES_NOT_FOUND);
		}

		List<ThemeDTO> items = this.convertListToDTO(themes, Theme.class, ThemeDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	private List<ThemeDTO> getThemesByGroup(int groupId) {
		Group group = getGroupById(groupId);

		List<Theme> themes = new ArrayList<>();
		try {
			themes = freeMetricsManager.getThemesByGroup(group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.THEMES_NOT_FOUND);
		}

		return this.convertListToDTO(themes, Theme.class, ThemeDTO.class);
	}

	public String getThemesByGroupAsJson(JSONArray parameters) throws JSONException {
		int groupId = parameters.getInt(0);
		
		List<ThemeDTO> items = getThemesByGroup(groupId);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getThemesByUser(JSONArray parameters) throws Exception {
		String login = parameters.getString(0);
		
		List<Group> groups = getGroups(login);
		Set<ThemeDTO> hset = new HashSet<ThemeDTO>();
		for (Group group : groups) {
			List<ThemeDTO> themes = getThemesByGroup(group.getId());
			for (ThemeDTO theme : themes) {
				hset.add(theme);
			}
		}

		List<ThemeDTO> items = new ArrayList<ThemeDTO>(hset);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getThemesByObservatory(JSONArray parameters) throws JSONException {
		int observatoryID = parameters.getInt(0);
		
		Observatory obs = null;
		try {
			obs = freeMetricsManager.getObservatoryById(observatoryID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.OBSERVATORY_NOT_FOUND);
		}

		List<ThemeDTO> items = this.convertListToDTO(obs.getThemes(), Theme.class, ThemeDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKpiListByTheme(JSONArray parameters) throws JSONException {
		int themeID = parameters.getInt(0);
		
		ThemeDTO theme = null;
		try {
			theme = new ThemeDTO(freeMetricsManager.getThemeById(themeID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.THEME_NOT_FOUND);
		}

		List<MetricDTO> items = theme.getMetrics();
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public List<MetricDTO> getKpiListByObservatory(int observatoryID) {
		Observatory obs = null;
		try {
			obs = freeMetricsManager.getObservatoryById(observatoryID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.OBSERVATORY_NOT_FOUND);
		}
		if (obs == null) {
			throw new VanillaApiException(VanillaApiError.OBSERVATORY_NOT_FOUND);
		}

		List<ThemeDTO> themes = this.convertListToDTO(obs.getThemes(), Theme.class, ThemeDTO.class);
		Set<MetricDTO> hset = new HashSet<MetricDTO>();
		for (ThemeDTO theme : themes) {
			hset.addAll(theme.getMetrics());
		}

		return new ArrayList<MetricDTO>(hset);
	}

	public String getKpiListByObservatoryAsJson(JSONArray parameters) throws JSONException {
		int observatoryID = parameters.getInt(0);
		
		List<MetricDTO> items = getKpiListByObservatory(observatoryID);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKpiListByGroup(JSONArray parameters) throws JSONException {
		int groupId = parameters.getInt(0);
		
		List<ObservatoryDTO> observatories = this.getObservatoriesByGroup(groupId);
		Set<MetricDTO> hset = new HashSet<MetricDTO>();
		for (ObservatoryDTO obs : observatories) {
			hset.addAll(this.getKpiListByObservatory(obs.getId()));
		}
		List<MetricDTO> items = new ArrayList<MetricDTO>(hset);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKpiListByUser(JSONArray parameters) throws Exception {
		String login = parameters.getString(0);
		
		List<ObservatoryDTO> observatories = this.getObservatoriesByUser(login);
		Set<MetricDTO> hset = new HashSet<MetricDTO>();
		for (ObservatoryDTO obs : observatories) {
			hset.addAll(this.getKpiListByObservatory(obs.getId()));
		}
		List<MetricDTO> items = new ArrayList<MetricDTO>(hset);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKPIValue(JSONArray parameters) throws JSONException {
		int kpiID = parameters.getInt(0);
		Date date = convertDate(parameters.getString(1));
		int groupId = parameters.getInt(2);
		
		Group group = this.getGroupById(groupId);

		MetricValue value = null;
		try {
			value = freeMetricsManager.getValueByMetricAndDate(date, kpiID, group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			// throw new
			// VanillaApiException(VanillaApiError.UNABLE_GET_KPI_VALUE);
		}

		if (value == null) {
			List<MetricValue> values = null;
			try {
				values = freeMetricsManager.getValueAndPreviousByMetricAndDate(date, kpiID, group.getId());
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.UNABLE_GET_KPI_VALUE);
			}
			if (values.isEmpty()) {
				return null;
			}
			List<MetricValueDTO> list = this.convertListToDTO(values, MetricValue.class, MetricValueDTO.class);
			MetricValueDTO items = list.get(list.size() - 1);
			try {
				return buildJson(items);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
			}
		}

		MetricValueDTO items = new MetricValueDTO(value);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKPIValues(JSONArray parameters) throws JSONException {
		int kpiID = parameters.getInt(0);
		Date date = convertDate(parameters.getString(1));
		int groupId = parameters.getInt(2);
		
		Group group = this.getGroupById(groupId);

		List<MetricValue> values = null;
		try {
			values = freeMetricsManager.getValueAndPreviousByMetricAndDate(date, kpiID, group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_KPI_VALUE);
		}
		List<MetricValueDTO> items = this.convertListToDTO(values, MetricValue.class, MetricValueDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKPIAxis(JSONArray parameters) throws JSONException {
		int kpiID = parameters.getInt(0);

		Metric kpi = null;
		try {
			kpi = freeMetricsManager.getMetric(kpiID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.KPI_NOT_FOUND);
		}
		List<Axis> axis = kpi.getLinkedAxis();

		List<AxisDTO> items = this.convertListToDTO(axis, Axis.class, AxisDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getAxisMembers(JSONArray parameters) {
		Axis axis = null;
		try {
			int axisID = parameters.getInt(0);
			axis = freeMetricsManager.getAxis(axisID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		AxisDTO axDTO = new AxisDTO(axis);

		List<LevelDTO> items = axDTO.getChildren();
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	private Date convertDate(String date) {
		long timestampMillis = Long.parseLong(date);
		Timestamp ts = new Timestamp(timestampMillis);
		return new Date(ts.getTime());
	}

	public String getKpiAxisValues(JSONArray parameters) throws Exception {

		int kpiID = parameters.getInt(0);
		int AxisID = parameters.getInt(1);
		Date date = convertDate(parameters.getString(2));
		int groupId = parameters.getInt(3);

		Group group = this.getGroupById(groupId);
		HashMap<Metric, List<MetricValue>> map = null;
		try {
			map = freeMetricsManager.getValuesAndPreviousByDateAndAxisAndMetric(date, AxisID, kpiID, group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_VALUE);
		}

		List<MetricValue> values = map.entrySet().iterator().next().getValue();

		List<MetricValueDTO> items = this.convertListToDTO(values, MetricValue.class, MetricValueDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public String getKpiAxisValue(JSONArray parameters) throws JSONException {

		int kpiID = parameters.getInt(0);
		int axisID = parameters.getInt(1);
		Date date = convertDate(parameters.getString(2));
		int groupId = parameters.getInt(3);

		Group group = this.getGroupById(groupId);
		Metric kpi = null;
		try {
			kpi = freeMetricsManager.getMetric(kpiID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.KPI_NOT_FOUND);
		}
		if (kpi.getLinkedAxis().stream().filter(axis -> axis.getId() == axisID).collect(Collectors.toList()).size() < 0) {
			throw new VanillaApiException(VanillaApiError.AXIS_NOT_LINKED);
		}

		HashMap<Metric, List<MetricValue>> map = null;

		try {
			map = freeMetricsManager.getValuesByDateAndAxisAndMetric(date, axisID, kpiID, group.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<MetricValue> values = map.entrySet().iterator().next().getValue();
		// Si pas de valeur pour la date donnée :
		if (map == null || map.isEmpty() || values.isEmpty()) {
			HashMap<Metric, List<MetricValue>> map2 = null;
			try {
				map2 = freeMetricsManager.getValuesAndPreviousByDateAndAxisAndMetric(date, axisID, kpiID, group.getId());
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.UNABLE_GET_VALUE);
			}

			List<MetricValue> values2 = map2.entrySet().iterator().next().getValue();
			Date date2 = values2.get(values2.size() - 1).getDate();

			List<MetricValueDTO> list = new ArrayList<>();

			for (int i = values2.size() - 1; i >= 0; i--) {
				if (!values2.get(i).getDate().toString().equals(date2.toString())) {
					break;
				}
				list.add(new MetricValueDTO(values2.get(i)));
			}

			try {
				return buildJson(list);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
			}
		}

		List<MetricValueDTO> items = this.convertListToDTO(values, MetricValue.class, MetricValueDTO.class);
		try {
			return buildJson(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.ERROT_NOT_FOUND);
		}
	}

	public Group getGroupById(int groupId) {
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		return group;
	}

	public <S, T> List<T> convertListToDTO(List<S> list, Class<S> baseClass, Class<T> DTOClass) {
		List<T> converted = new ArrayList<T>();

		for (S element : list) {

			try {
				converted.add(DTOClass.getConstructor(baseClass).newInstance(element));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}

		}
		return converted;
	}

}
