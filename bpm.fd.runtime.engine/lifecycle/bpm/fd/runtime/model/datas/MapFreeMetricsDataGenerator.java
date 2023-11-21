package bpm.fd.runtime.model.datas;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.FMMapValueBean;
import bpm.vanilla.platform.core.beans.FMMetricBean;
import bpm.vanilla.platform.core.beans.FMMapValueBean.MapZonePoint;

public class MapFreeMetricsDataGenerator {
	
	private FMContext fmContext;
	private RemoteFreeMetricsManager remote;
	private HashMap<String, List<MapZonePoint>> mapZonePoints;
	private HashMap<String, String> mapZoneColors;
	private List<String> years;
	
	public MapFreeMetricsDataGenerator(String user, String password, String vanillaUrl, FMContext fmContext) {
		this.fmContext = fmContext;
		remote = new RemoteFreeMetricsManager(vanillaUrl, user, password); 
	}
	
	public HashMap<String, String> generateMapData(int mapId, int metricId, String dateString) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		
		List<FMMapValueBean> values = getDataFromServeur(mapId, metricId, dateString);
			
		for(FMMapValueBean bean : values) {
			result.put(bean.getZoneId(), String.valueOf(bean.getValue()));
		}

		
		return result;
	}
	
	public HashMap<String, String> generateMapData(int mapId, int metricId, String dateString, ColorRange[] colorRanges) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		mapZonePoints = new HashMap<String, List<MapZonePoint>>();
		mapZoneColors = new HashMap<String, String>();
		
		List<FMMapValueBean> values = getDataFromServeur(mapId, metricId, dateString);
		
		for(FMMapValueBean bean : values) {
			result.put(bean.getZoneId(), String.valueOf(bean.getValue()));
			mapZonePoints.put(bean.getZoneId(), bean.getPoints());
			
			for(ColorRange range : colorRanges) {
				if(range.getMin().doubleValue() <= bean.getValue() && range.getMax().doubleValue() >= bean.getValue()) {
					mapZoneColors.put(bean.getZoneId(), range.getHex());
				}
			}
		}
		
		return result;
	}
	
	public List<FMMapValueBean> getDataFromServeur(int mapId, int metricId, String dateString) throws Exception {
		if(dateString.length() < 5) {
			dateString += "-01";
		}
		if(dateString.length() < 8) {
			dateString += "-01";
		}
		
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		Date date = form.parse(dateString);
		
		try {
			List<FMMapValueBean> values = remote.getFreeMetricsValues(metricId, mapId, date, fmContext);
			return values;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error while retriving map data : ", e);
		}
	}
	
	public List<FMMetricBean> getMetrics(int mapId) throws Exception {
		List<FMMetricBean> beans = remote.getMetrics(mapId, fmContext);
		years = beans.get(0).getPossibleYears();
		return beans;
	}
	
	public String getPossibleYears() {
		StringBuffer buf = new StringBuffer();
		buf.append("new Array(");
		Collections.sort(years);
		boolean first = true;
		for(String y : years) {
			if(first) {
				buf.append("\""+y+"\"");
				first = false;
			}
			else {
				buf.append(","+"\""+y+"\"");
			}
		}
		buf.append(")");
		return buf.toString();
	}
	
	public String getPolygonString(String zoneId) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("							 var style" + zoneId + " = {fillColor: \"#" + mapZoneColors.get(zoneId) + "\"};\n");
		
		buf.append("							 points = new Array();\n");
		
		int i = 0;
		for(MapZonePoint point : mapZonePoints.get(zoneId)) {
			buf.append("							 var point" + zoneId + i + " = new OpenLayers.Geometry.Point(\"" + point.getLongitude() + "\", \"" + point.getLatitude() + "\");\n");
			buf.append("							 points.push(point" + zoneId + i + ");\n");
			i++;
		}
		
		buf.append("							 linear_ring = new OpenLayers.Geometry.LinearRing(points);\n");
		buf.append("							 polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linear_ring]), null, style"+zoneId+");\n");
		buf.append("							 polygonLayer.addFeatures([polygonFeature]);\n");
		return buf.toString();
	}
}
