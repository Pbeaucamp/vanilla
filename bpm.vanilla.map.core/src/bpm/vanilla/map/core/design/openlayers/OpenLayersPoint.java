package bpm.vanilla.map.core.design.openlayers;

public class OpenLayersPoint {
	
	private String latitude;
	private String longitude;
	
	public OpenLayersPoint(){}
	
	public OpenLayersPoint(String lat, String lon) {
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