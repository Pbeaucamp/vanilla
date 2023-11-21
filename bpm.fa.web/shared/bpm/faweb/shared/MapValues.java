package bpm.faweb.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.MapVanilla;

public class MapValues implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String value;
	private List<String> latitudes = new ArrayList<String>();
	private List<String> longitudes = new ArrayList<String>();

	private String geoId;
	
	private MapVanilla map;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public MapVanilla getMap() {
		return map;
	}

	public void setMap(MapVanilla map) {
		this.map = map;
	}
	
}
