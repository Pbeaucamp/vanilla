package bpm.gateway.core.transformations.calcul;

public class GeoCondition {
	public GeoCondition() {
		super();
	}

	public String targetName = "";
	public String inputKml = "";
	public String placeMarkName = "";
	public String geoShape = "";

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getInputKml() {
		return inputKml;
	}

	public void setInputKml(String inputKml) {
		this.inputKml = inputKml;
	}

	public String getPlaceMarkName() {
		return placeMarkName;
	}

	public void setPlaceMarkName(String placeMarkName) {
		this.placeMarkName = placeMarkName;
	}

	public String getGeoShape() {
		return geoShape;
	}

	public void setGeoShape(String geoShape) {
		this.geoShape = geoShape;
	}

}