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
import bpm.gwt.commons.client.services.exception.ServiceException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("MapViewerService")
public interface MapViewerService extends RemoteService {

	public static class Connect{
		private static MapViewerServiceAsync instance;
		public static MapViewerServiceAsync getInstance(){
			if(instance == null){
				instance = (MapViewerServiceAsync) GWT.create(MapViewerService.class);
			}
			return instance;
		}
	}
	
	public List<ComplexMap> getMaps() throws ServiceException;
	
	public List<MetricValue> getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId) throws Exception;
	
	public List<MapZoneValue> getMapValues(Date startDate, Date endDate, int id, Level object, int groupId) throws Exception;
	
	public List<ComplexObjectViewer> getMultiMapValues(Date startDate, Date endDate, List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, List<LevelMember> filterLevelMembers, int groupId) throws Exception;

	public void addOrEditMap(ComplexMap map) throws ServiceException;

	public void deleteMap(ComplexMap selectedMap) throws ServiceException;
	
	public AxisInfo getAxisInfo(int idAxis) throws Exception;
} 
