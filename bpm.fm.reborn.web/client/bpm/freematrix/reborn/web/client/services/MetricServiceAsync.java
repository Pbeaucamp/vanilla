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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetricServiceAsync {

	void getMetricValue(Date date, int group, int theme, AsyncCallback<HashMap<Metric, MetricValue>> callback);

	void getValuesByDateAndAxis(Date date, Axis axis, int group, int theme, AsyncCallback<HashMap<Metric, List<MetricValue>>> callback);

	void getValueAndPreviousByMetricAndDate(Date date, int metricId, int group, int theme, AsyncCallback<List<MetricValue>> callback);

	void getComments(Date date, int metricId, AsyncCallback<List<Comment>> callback);

	void addComment(Comment comment, AsyncCallback<Void> callback);

	void getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId, AsyncCallback<List<MetricValue>> callback);

	void getRaisedAlerts(boolean withResolved, AsyncCallback<List<AlertRaised>> callback);

	void getAlertComment(int raisedAlertId, AsyncCallback<List<CommentAlert>> callback);

	void addAlertComment(CommentAlert comment, AlertRaised raisedId, boolean resolve, String login, AsyncCallback<Void> asyncCallback);

	void getMetrics(AsyncCallback<List<Metric>> asyncCallback);

	void generateCubeUrl(MetricLinkedItem linkedItem, AsyncCallback<String> asyncCallback);

	void getMapValues(Date filterDate, int id, Level object, int groupId, AsyncCallback<List<MapZoneValue>> asyncCallback);

	void getGaugeValues(MetricValue value, int groupId, AsyncCallback<HashMap<String, MetricValue>> callback);
	
	public void exportToCkan(String resourceName, CkanPackage pack, Date date, int metricId, int group, int theme, AsyncCallback<Void> callback);
	
	public void getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId, AsyncCallback<List<MetricValue>> callback);
}
