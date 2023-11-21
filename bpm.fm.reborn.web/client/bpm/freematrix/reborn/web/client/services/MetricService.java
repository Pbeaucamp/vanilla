package bpm.freematrix.reborn.web.client.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;

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
	
	public HashMap<Metric, MetricValue> getMetricValue(Date date, int group, int theme) throws Exception;
	
	public HashMap<Metric, List<MetricValue>> getValuesByDateAndAxis(Date date, Axis axis, int group, int theme) throws Exception;
	
	public List<MetricValue> getValueAndPreviousByMetricAndDate(Date date,int metricId, int group, int theme) throws Exception;
	
	public List<Comment> getComments(Date date, int metricId) throws Exception;
	
	public void addComment(Comment comment) throws Exception;
	
	public List<MetricValue> getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId) throws Exception;
	
	public List<AlertRaised> getRaisedAlerts(boolean withResolved) throws Exception;
	
	public List<CommentAlert> getAlertComment(int raisedAlertId) throws Exception;
	
	public void addAlertComment(CommentAlert comment, AlertRaised raisedId, boolean resolve, String login) throws Exception;

	public List<Metric> getMetrics() throws Exception;

	public String generateCubeUrl(MetricLinkedItem linkedItem) throws Exception;

	public List<MapZoneValue> getMapValues(Date filterDate, int id, Level object, int groupId) throws Exception;
	
	public HashMap<String, MetricValue> getGaugeValues(MetricValue value, int groupId) throws Exception;
	
	public void exportToCkan(String resourceName, CkanPackage pack, Date date, int metricId, int group, int theme) throws Exception;
	
	public List<MetricValue> getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId) throws Exception;
} 
