package bpm.freematrix.reborn.web.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeMetric;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.CkanHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MetricServiceImpl extends RemoteServiceServlet implements MetricService {

	private static final long serialVersionUID = -660218264074823448L;

	private FreeMetricsSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FreeMetricsSession.class);
	}

	@Override
	public HashMap<Metric, MetricValue> getMetricValue(Date date, int group, int theme) throws Exception {
		try {
			Date start = new Date();
			List<Observatory> obs = getSession().getManager().getObservatoriesByGroup(group);
			System.out.println("Observatories : " + (new Date().getTime() - start.getTime()));
			start = new Date();
			HashMap<Metric, MetricValue> res = getSession().getManager().getValuesByDate(date, group);

			List<Metric> toRemove = new ArrayList<>();
			for (Metric m : res.keySet()) {
				if (!isAllowed(m, obs, theme)) {
					toRemove.add(m);
				}
			}
			for (Metric m : toRemove) {
				res.remove(m);
			}
			System.out.println("Get All Metrics Values : " + (new Date().getTime() - start.getTime()));
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		// return null;
	}

	private boolean isAllowed(Metric m, List<Observatory> obs, int theme) throws ServiceException, Exception {
		List<ThemeMetric> tms = getSession().getManager().getThemeMetricByMetric(m.getId());
		if (theme == -1) {
			for (ThemeMetric tm : tms) {
				
				for(Observatory ob : obs) {
					for(Theme t : ob.getThemes()) {
						if(t.getId() == tm.getThemeId()) {
							return true;
						}
					}
				}
			}
		}
		else {
			for (ThemeMetric tm : tms) {
				if (tm.getThemeId() == theme) {
					
					for(Observatory ob : obs) {
						for(Theme t : ob.getThemes()) {
							if(t.getId() == tm.getThemeId()) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxis(Date date, Axis axis, int group, int theme) throws Exception {
		try {
			
			List<Observatory> obs = getSession().getManager().getObservatoriesByGroup(group);
			
			HashMap<Metric, List<MetricValue>> res = getSession().getManager().getValuesByDateAndAxis(date, axis.getId(), group);

			List<Metric> toRemove = new ArrayList<>();
			for (Metric m : res.keySet()) {
				if (!isAllowed(m, obs, theme)) {
					toRemove.add(m);
				}
			}
			for (Metric m : toRemove) {
				res.remove(m);
			}

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<MetricValue> getValueAndPreviousByMetricAndDate(Date date, int metricId, int group, int theme) throws Exception {
		try {
			List<Observatory> obs = getSession().getManager().getObservatoriesByGroup(group);
			
			List<MetricValue> res = getSession().getManager().getValueAndPreviousByMetricAndDate(date, metricId, group);
			Metric metric = getSession().getManager().getMetric(metricId);

			List<MetricValue> toRemove = new ArrayList<>();
			for (MetricValue m : res) {
				if (!isAllowed(metric, obs, theme)) {
					toRemove.add(m);
				}
			}
			for (MetricValue m : toRemove) {
				res.remove(m);
			}

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<Comment> getComments(Date date, int metricId) throws Exception {
		return getSession().getManager().getCommentsByDateMetric(date, metricId);
	}

	@Override
	public void addComment(Comment comment) throws Exception {
		comment.setUserId(getSession().getUser().getId());
		getSession().getManager().addComment(comment);
	}

	@Override
	public List<MetricValue> getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId) throws Exception {
		return getSession().getManager().getValuesByDateAndLevelAndMetric(date, level.getId(), metricId, groupId);
	}

	@Override
	public List<AlertRaised> getRaisedAlerts(boolean withResolved) throws Exception {
		List<AlertRaised> raised = new ArrayList<AlertRaised>();
		raised.addAll(getSession().getManager().getRaisedAlert(withResolved));
		raised.addAll(getSession().getManager().getRaisedAlert(!withResolved));
		Collections.sort(raised, new Comparator<AlertRaised>() {
			@Override
			public int compare(AlertRaised o1, AlertRaised o2) {
				if(o1.getDate().before(o2.getDate())) {
					return 1;
				}
				else if(o1.getDate().after(o2.getDate())) {
					return -1;
				}
				return 0;
			}
			
		});
		return raised;
	}

	@Override
	public List<CommentAlert> getAlertComment(int raisedAlertId) throws Exception {
		return getSession().getManager().getAlertComments(raisedAlertId);
	}

	@Override
	public void addAlertComment(CommentAlert comment, AlertRaised raised, boolean resolve, String login) throws Exception {
		getSession().getManager().addAlertComment(raised, comment.getComment(), resolve, getSession().getUser().getId());
		
	}

	@Override
	public List<Metric> getMetrics() throws Exception {
		return getSession().getManager().getMetrics();
	}

	@Override
	public String generateCubeUrl(MetricLinkedItem linkedItem) throws Exception {
		CommonSession session = getSession();

		String faBaseUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);

		String url = "";

		try {
			url += faBaseUrl;
			String sessionId = session.getVanillaSessionId();
			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			url += "&bpm.vanilla.repositoryId=1";// + session.getCurrentRepository().getId();
			url += "&bpm.vanilla.fasd.id=" + linkedItem.getItemId();
			url += "&viewer=true";
//			if (cubeName != null) {
//				url += "&bpm.vanilla.cubename=" + URLEncoder.encode(cubeName, "UTF-8");
//			}

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			throw new ServiceException(msg);
		}
//		return null;
	}

	@Override
	public List<MapZoneValue> getMapValues(Date filterDate, int id, Level object, int groupId) throws Exception {
		return getSession().getManager().getMapZoneValues(id, object.getId(), filterDate, null, groupId);
	}

	@Override
	public HashMap<String, MetricValue> getGaugeValues(MetricValue value, int groupId) throws Exception {
		Metric metric = value.getMetric();
		Date date = value.getDate();
		
		Date previousDate = new Date(date.getTime());
		if(metric.getFactTable() instanceof FactTable) {
			if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY)) {
				previousDate.setYear(date.getYear() - 1);
			}
			else if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_DAILY)) {
				previousDate.setDate(date.getDate() - 1);
			}
			else { 
				previousDate.setMonth(date.getMonth() - 1);
			}
		}
		else {
			previousDate.setMonth(date.getMonth() - 1);
		}
		
		
		Date nextDate = new Date(date.getTime());
		if(metric.getFactTable() instanceof FactTable) {
			if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY)) {
				previousDate.setYear(date.getYear() + 1);
			}
			else if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_DAILY)) {
				previousDate.setDate(date.getDate() + 1);
			}
			else { 
				previousDate.setMonth(date.getMonth() + 1);
			}
		}
		else {
			nextDate.setMonth(date.getMonth() + 1);
		}
		
		HashMap<String, MetricValue> result = new HashMap<String, MetricValue>();
		result.put("current", value);
		
		MetricValue previous;
		try {
			previous = getSession().getManager().getValueByMetricAndDate(previousDate, metric.getId(), groupId);
			result.put("previous", previous);
		} catch (Exception e) {
		}
		MetricValue next;
		try {
			next = getSession().getManager().getValueByMetricAndDate(nextDate, metric.getId(), groupId);
			result.put("next", next);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	@Override
	public void exportToCkan(String resourceName, CkanPackage pack, Date date, int metricId, int group, int theme) throws Exception {
		try {
			List<MetricValue> res = getValueAndPreviousByMetricAndDate(date, metricId, group, theme);
			
			ByteArrayInputStream is = buildCsv(res, ";");

			CkanHelper ckanHelper = new CkanHelper();
			ckanHelper.uploadCkanFile(resourceName, pack, is);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private ByteArrayInputStream buildCsv(List<MetricValue> metricValues, String separator) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		List<String> columnNames = new ArrayList<>();
		columnNames.add("Name");
		columnNames.add("Date");
		columnNames.add("Value");
		columnNames.add("Objective");
		columnNames.add("Minimum");
		columnNames.add("Maximum");
		columnNames.add("Tolerance");
		columnNames.add("Tendancy");
		columnNames.add("Health");

		StringBuffer firstLine = new StringBuffer();
		for (int i = 0; i < columnNames.size(); i++) {
			if (i != 0) {
				firstLine.append(separator);
			}
			firstLine.append(columnNames.get(i));
		}
		firstLine.append("\n");

		try {
			os.write(firstLine.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (MetricValue value : metricValues) {
			StringBuffer line = new StringBuffer();
			line.append(value.getMetric().getName());
			line.append(separator);
			line.append(value.getDate());
			line.append(separator);
			line.append(value.getValue());
			line.append(separator);
			line.append(value.getObjective());
			line.append(separator);
			line.append(value.getMinimum());
			line.append(separator);
			line.append(value.getMaximum());
			line.append(separator);
			line.append(value.getTolerance());
			line.append(separator);
			line.append(value.getTendancy());
			line.append(separator);
			line.append(value.getHealth());
			line.append("\n");

			try {
				os.write(line.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayIs;
	}

	@Override
	public List<MetricValue> getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId) throws Exception {
		try {
			return getSession().getManager().getValuesByMetricAndDateInterval(metricId, startDate, endDate, groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
