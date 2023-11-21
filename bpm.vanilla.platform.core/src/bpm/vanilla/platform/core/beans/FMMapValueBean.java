package bpm.vanilla.platform.core.beans;

import java.util.List;

public class FMMapValueBean {

	public class MapZonePoint {
		
		private String latitude;
		private String longitude;
		
		public MapZonePoint(){}
		
		public MapZonePoint(String lat, String lon) {
			this.latitude = lat;
			this.longitude = lon;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
	}
	
	private int id;
	private double value;
	private int assocId;
	private int applicationId;
	private int metricId;
	private String zoneId;
	private List<MapZonePoint> zonePoints;
	private String colorHexa;
	
	public FMMapValueBean(){}
	
	public FMMapValueBean(int id, double value, int assocId, int applicationId, int metricId, String zoneId) {
		this.id = id;
		this.value = value;
		this.assocId = assocId;
		this.applicationId = applicationId;
		this.metricId = metricId;
		this.zoneId = zoneId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public int getAssocId() {
		return assocId;
	}
	
	public void setAssocId(int assocId) {
		this.assocId = assocId;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getZoneId() {
		return zoneId;
	}
	
	public List<MapZonePoint> getPoints() {
		return zonePoints;
	}
	
	public void setPoints(List<MapZonePoint> points) {
		this.zonePoints = points;
	}

	public void setColorHexa(String colorHexa) {
		this.colorHexa = colorHexa;
	}

	public String getColorHexa() {
		return colorHexa;
	}
	
	@Override
	public String toString() {
		return value + "";
	}
}
