package bpm.vanilla.map.core.design;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapZone implements Serializable {
	private List<String> latitudes = new ArrayList<String>();
	private List<String> longitudes = new ArrayList<String>();

	private String geoId;
	
	private String marker;
	private int minSize;
	private int maxSize;
	
	private String mapType = "polygon";
	
	private Map<Integer, ZoneMetadataMapping> metadataMappings = new HashMap<Integer, ZoneMetadataMapping>();
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	public MapZone(){}
	
	public void addPoint(String latitude, String longitude) {
		latitudes.add(latitude);
		longitudes.add(longitude);
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getMapType() {
		return mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public List<String> getLatitudes() {
		return latitudes;
	}

	public void setLatitudes(List<String> latitudes) {
		this.latitudes = latitudes;
	}

	public List<String> getLongitudes() {
		return longitudes;
	}

	public void setLongitudes(List<String> longitudes) {
		this.longitudes = longitudes;
	}

	public String getGeoId() {
		return geoId;
	}

	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<Integer, ZoneMetadataMapping> getMetadataMappings() {
		return metadataMappings;
	}

	public void setMetadataMappings(Map<Integer, ZoneMetadataMapping> metadataMappings) {
		this.metadataMappings = metadataMappings;
	}
	
	
}
