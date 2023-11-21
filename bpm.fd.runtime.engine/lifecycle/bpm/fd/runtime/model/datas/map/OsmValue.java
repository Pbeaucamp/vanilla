package bpm.fd.runtime.model.datas.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class OsmValue {

	private Double value;
	private String zoneId;
	private String zoneLabel;

	private String longitude;
	private String latitude;

	private List<String> longitudes;
	private List<String> latitudes;
	
	private HashSet<String> lastLevelIds;
	private String parentId;
	
	private String operator = "sum";
	
	private List<Double> values;
	
	private HashMap<Integer, String> otherFields = new HashMap<Integer, String>();

	public Double getValue() {
		if(value == null) {
			return null;
		}
		if(operator.equals("avg")) {
			return value / (double) values.size();
		}
		return value;
	}

	public void setValue(double value) {
		if(operator == null || operator.isEmpty()) {
			this.value = value;
		}
		else if(operator.equals("sum")) {
			if(this.value == null) {
				this.value = value;
			}
//			this.value = this.value + value;
		}
		else if(operator.equals("count")) {
			if(this.value == null) {
				this.value = value;
			}
			this.value++;
		}
		else if(operator.equals("avg")) {
			if(values == null) {
				values = new ArrayList<Double>();
			}
			values.add(value);
			if(this.value == null) {
				this.value = value;
			}
			this.value = this.value + value;
		}
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public List<String> getLongitudes() {
		if(this.longitudes == null) {
			this.longitudes = new ArrayList<String>();
		}
		return longitudes;
	}

	public void setLongitudes(List<String> longitudes) {
		this.longitudes = longitudes;
	}

	public List<String> getLatitudes() {
		if(this.latitudes == null) {
			this.latitudes = new ArrayList<String>();
		}
		return latitudes;
	}

	public void setLatitudes(List<String> latitudes) {
		this.latitudes = latitudes;
	}

	public String getZoneLabel() {
		return zoneLabel;
	}

	public void setZoneLabel(String zoneLabel) {
		this.zoneLabel = zoneLabel;
	}

	public HashSet<String> getLastLevelIds() {
		if(lastLevelIds == null) {
			lastLevelIds = new HashSet<String>();
		}
		return lastLevelIds;
	}

	public void setLastLevelIds(HashSet<String> lastLevelIds) {
		this.lastLevelIds = lastLevelIds;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public HashMap<Integer, String> getOtherFields() {
		return otherFields;
	}

	public void setOtherFields(HashMap<Integer, String> otherFields) {
		this.otherFields = otherFields;
	}
	
}
