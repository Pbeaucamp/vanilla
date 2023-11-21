package bpm.birt.osm.core.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.birt.osm.core.model.OsmValue;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapVanilla;

public class GeographicDataHelper {
	
	public static HashMap<String, OsmValue> getGeographicData(MapDataSet serie, MapVanilla map, boolean first) throws Exception {
		
		HashMap<String, OsmValue> values = new HashMap<String, OsmValue>();
		
		List<MapDataSet> children = new ArrayList<MapDataSet>();
		for(MapDataSet mapDs : map.getDataSetList()) {
			if(mapDs.getParentId() != null && mapDs.getParentId().intValue() == serie.getId()) {
				children.add(mapDs);
			}
		}
		
		MapDataSource datasource = serie.getDataSource();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(datasource.getUrl(), datasource.getLogin(), datasource.getMdp(), datasource.getDriver());
			stmt = connection.createStatement();
			rs = stmt.executeQuery(serie.getQuery());
			
			while(rs.next()) {
				String zoneId = rs.getString(serie.getIdZone());
				String latitude = rs.getString(serie.getLatitude());
				String longitude = rs.getString(serie.getLongitude());
				String label = zoneId;
				try {
					label = rs.getString(serie.getZoneLabel());
					if(label == null) {
						label = zoneId;
					}
				} catch(Exception e) {
				}
				
				String parentId = null;
				if(serie.getParentId() != null) {
					parentId = rs.getString(serie.getParent());
				}
				OsmValue val = values.get(zoneId);
				if(val == null) {
					val = new OsmValue();
					val.setZoneId(zoneId);
					val.setZoneLabel(label);
					val.setParentId(parentId);
					
					//trick to get the lastLevelIds in upper levels
					if(children.isEmpty()) {
						val.getLastLevelIds().add(zoneId);
					}
					values.put(zoneId, val);
				}
				val.getLatitudes().add(latitude);
				val.getLongitudes().add(longitude);
				
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(connection != null) {
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
		}
		
		
		if(!children.isEmpty()) {
			for(MapDataSet s : children) {
				HashMap<String, OsmValue> childRes = getGeographicData(s, map, false);
				
				//browse to match parent/child
				for(OsmValue val : childRes.values()) {
					OsmValue parentVal = values.get(val.getParentId());
					if(parentVal != null) {
						parentVal.getLastLevelIds().addAll(val.getLastLevelIds());
					}
				}
			}
		}
		
		
		return values;
	}
	
	
}
