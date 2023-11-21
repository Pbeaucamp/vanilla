package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.MapZone;

public class MapZoneValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mapType = "polygon";
	
	private List<MetricValue> values;
	private List<String> latitudes = new ArrayList<String>();
	private List<String> longitudes = new ArrayList<String>();

	private String geoId;
	
	private String marker;
	private int minSize;
	private int maxSize;
	
	public MapZoneValue() {}
	
	public MapZoneValue(MapZone mapZone) {
		mapType = mapZone.getMapType();
		latitudes = mapZone.getLatitudes();
		longitudes = mapZone.getLongitudes();
		geoId = mapZone.getGeoId();
		marker = mapZone.getMarker();
		minSize = mapZone.getMinSize();
		maxSize = mapZone.getMaxSize();
	}

	public List<MetricValue> getValues() {
		return values;
	}

	public void setValues(List<MetricValue> values) {
		this.values = values;
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

	public void addLatitude(String latitude) {
		latitudes.add(latitude);
	}
	
	public void addLongitude(String longitude) {
		longitudes.add(longitude);
	}
	
	public void addPoint(String latitude, String longitude) {
		latitudes.add(latitude);
		longitudes.add(longitude);
	}

	public String getGeoId() {
		return geoId;
	}

	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}

	public String getMapType() {
		return mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
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
	
	public void addValue(MetricValue value) {
		if(values == null) values = new ArrayList<MetricValue>();
		values.add(value);
	}
}
