package bpm.fm.runtime.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.fm.api.model.Level;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapHelper;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class MapValueCalculator extends ValueCalculator {

	public MapValueCalculator(FreeMetricsManagerComponent component) {
		super(component);
	}

	public List<MapZoneValue> getMapValues(bpm.fm.api.model.Metric metric, Level level, List<MetricValue> values) throws Exception {
		
//		MetricMap map = null;
//		for(MetricMap m : metric.getMaps()) {
//			if(m.getLevelId() == level.getId()) {
//				map = m;
//				break;
//			}
//		}
		RemoteMapDefinitionService remotemapservice = new RemoteMapDefinitionService();
		remotemapservice.setVanillaRuntimeUrl(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
		
		MapDataSet dtS = remotemapservice.getMapDataSetById(level.getMapDatasetId()).get(0);
		
		HashMap<String, MapZoneValue> result = getMapZones(dtS);
		
		List<MapZoneValue> resultVals = new ArrayList<MapZoneValue>();
		
		int levelIndex = level.getParent().getChildren().indexOf(level);
		
		//map zones/values
		for(String zone : result.keySet()) {
			for(MetricValue value : values) {
				LevelMember mem = value.getAxis().get(levelIndex);
				if(zone.equals(mem.getGeoId())) {
					result.get(zone).addValue(value);
				}
			}
		}
		for(MapZoneValue zone : result.values()) {
			if(zone.getValues() != null){
				resultVals.add(zone);
			}
		}
		
		return resultVals;
	}

	private HashMap<String, MapZoneValue> getMapZones(MapDataSet dtS) throws Exception {
		//generate query
//		StringBuilder buf = new StringBuilder();
		
//		buf.append("Select ");
//		buf.append(map.getColumnZone());
//		buf.append(",\n");
//		buf.append(map.getColumnLatitude());
//		buf.append(",\n");
//		buf.append(map.getColumnLongitude());
//		buf.append("\n From ");
//		buf.append(map.getTableName());
		RemoteMapDefinitionService mapRemote = new RemoteMapDefinitionService();
		mapRemote.configure(component.getVanillaApi().getVanillaUrl());
		Map<String, MapZone> zones = MapHelper.getMapZone(dtS, component.getVanillaApi(), mapRemote);
		
		//execute query
//		String query = dtS.getQuery();
		
		HashMap<String, MapZoneValue> zoneValues = new HashMap<String, MapZoneValue>();
		
		for(String key : zones.keySet()) {
			MapZoneValue val = new MapZoneValue(zones.get(key));
			zoneValues.put(key, val);
		}
		
//		RemoteMapDefinitionService remotemapservice = new RemoteMapDefinitionService();
//		remotemapservice.setVanillaRuntimeUrl(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
//		
//		MapDataSource ds = remotemapservice.getMapDataSourceById(dtS.getIdDataSource()).get(0) ;
//		
//		//DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
//		VanillaJdbcConnection valueConnection = null;
//		try {
//			valueConnection = ConnectionManager.getInstance().getJdbcConnection(ds.getUrl(), ds.getLogin(), ds.getMdp(), ds.getDriver());
//			
//			VanillaPreparedStatement statement = valueConnection.prepareQuery(query);
//			
//			ResultSet resultSet = statement.executeQuery();
//			
//			while(resultSet.next()) {
//				
//				String zone = resultSet.getString(dtS.getIdZone());
//				String latitude = resultSet.getString(dtS.getLatitude());
//				String longitude = resultSet.getString(dtS.getLongitude());
//				
//				MapZoneValue val = null;
//				if(zoneValues.get(zone) != null) {
//					val = zoneValues.get(zone);
//				}
//				else {
//					val = new MapZoneValue();
//					val.setMarker(dtS.getMarkerUrl());
//					val.setMinSize(dtS.getMarkerSizeMin());
//					val.setMaxSize(dtS.getMarkerSizeMax());
//					val.setMapType(dtS.getType());
//					val.setGeoId(zone);
//					zoneValues.put(zone, val);
//				}
//				
//				val.addPoint(latitude, longitude);
//				
//			}
//			
//			resultSet.close();
//			statement.close();
//			
//			
//			ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
//		} catch (Exception e) {
//			if(valueConnection != null) {
//				ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
//			}
//			e.printStackTrace();
//			throw e;
//		}
		
		return zoneValues;
	}

}
