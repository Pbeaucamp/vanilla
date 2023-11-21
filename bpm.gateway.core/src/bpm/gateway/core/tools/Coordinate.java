package bpm.gateway.core.tools;

public class Coordinate {

	private double latitude;
	private double longitude;
	private double score;
	
	public Coordinate(double latitude, double longitude, double score) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.score = score;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getScore() {
		return score;
	}
}
