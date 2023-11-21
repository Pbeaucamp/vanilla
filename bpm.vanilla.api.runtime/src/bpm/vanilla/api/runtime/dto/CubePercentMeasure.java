package bpm.vanilla.api.runtime.dto;

public class CubePercentMeasure {

	private String measureName;
	private boolean showMeasure;

	public String getMeasureName() {
		return measureName;
	}

	public boolean isShowMeasure() {
		return showMeasure;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	public void setShowMeasure(boolean showMeasure) {
		this.showMeasure = showMeasure;
	}

}
