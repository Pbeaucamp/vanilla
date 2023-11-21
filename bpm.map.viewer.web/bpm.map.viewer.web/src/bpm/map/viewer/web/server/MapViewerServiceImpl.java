package bpm.map.viewer.web.server;

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
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.map.viewer.web.client.services.MapViewerService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MapViewerServiceImpl extends RemoteServiceServlet implements MapViewerService {

	private static final long serialVersionUID = -660218264074823448L;

	private MapViewerSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), MapViewerSession.class);
	}

	@Override
	public List<ComplexMap> getMaps() throws ServiceException {
		MapViewerSession session = getSession();
		List<ComplexMap> lesMaps;
		
		try {
			 lesMaps = session.getManager().getAllComplexMaps();
			 return lesMaps;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of maps : " + e.getMessage());
		}
	}

	@Override
	public void addOrEditMap(ComplexMap map) throws ServiceException {
		MapViewerSession session = getSession();

		try {
			if (map.getId() > 0) {
				session.getManager().updateComplexMap(map);
			}
			else {
				session.getManager().saveComplexMap(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Map.", e);
		}
		 
		
	}
	
	@Override
	public void deleteMap(ComplexMap selectedMap) throws ServiceException {
		MapViewerSession session = getSession();

		try {
			//deleteMapDataSet(selectedMap.getDataSet());
			session.getManager().deleteComplexMap(selectedMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Map.", e);
		}
	}
	
	@Override
	public List<MetricValue> getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId) throws Exception {
		return getSession().getManager().getValuesByDateAndLevelAndMetric(date, level.getId(), metricId, groupId);
	}


	@Override
	public List<MapZoneValue> getMapValues(Date startDate, Date endDate, int id, Level object, int groupId) throws Exception {
		return getSession().getManager().getMapZoneValues(id, object.getId(), startDate, endDate, groupId);
	}
	
	@Override
	public List<ComplexObjectViewer> getMultiMapValues(Date startDate, Date endDate, List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, List<LevelMember> filterLevelMembers, int groupId) throws Exception {
		return getSession().getManager().getMultiMapZoneValues(metrics, levels, startDate, endDate, filterLevelMembers, groupId);
	}

	@Override
	public AxisInfo getAxisInfo(int idAxis) throws Exception {
		return getSession().getManager().getLoaderAxe(idAxis);
	}
	
}
