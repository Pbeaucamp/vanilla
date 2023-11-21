package bpm.map.viewer.web.client.services;

import java.util.Date;
import java.util.List;

import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MapViewerServiceAsync {
	
	void getMaps(AsyncCallback<List<ComplexMap>> callback);
	
	void addOrEditMap(ComplexMap map, AsyncCallback<Void> callback);
	
	void deleteMap(ComplexMap selectedMap, AsyncCallback<Void> callback);

	void getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId, AsyncCallback<List<MetricValue>> callback);

	void getMapValues(Date startDate, Date endDate, int id, Level object, int groupId, AsyncCallback<List<MapZoneValue>> asyncCallback);
	
	void getMultiMapValues(Date startDate, Date endDate, List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, List<LevelMember> filterLevelMembers, int groupId, AsyncCallback<List<ComplexObjectViewer>> asyncCallback);
	
	void getAxisInfo(int idAxis, AsyncCallback<AxisInfo> asyncCallback);
}
