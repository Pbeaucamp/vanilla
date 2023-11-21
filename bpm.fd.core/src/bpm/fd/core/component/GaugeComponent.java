package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class GaugeComponent extends DashboardComponent implements IComponentData, IComponentOption {

	private static final long serialVersionUID = 1L;
	private Dataset dataset;
	
	//data
	private Integer valueField;
	
	private boolean minMaxFromField = false;
	private boolean thresholdFromField = false;
	
	private Float min = 0f;
	private Float max = 0f;
	private Float minThreshold = 0f;
	private Float maxThreshold = 0f;
	
	private boolean targetFromField = false;
	private Float target = 0f;
	
	private Integer tolerance = 0;
	
	//options
	private int bgAlpha = 0;

	private boolean showValues = true;
	private String bgColor = null;

	private String colorBadValue = "FF654F", colorMediumValue = "F6BD0F", colorGoodValue = "8BBA00";

	private int innerRadius = 70;
	private int outerRadius = 100;
	private int startAngle = 180;
	private int stopAngle = 0;
	private boolean bulb = false;

	public Integer getValueField() {
		return valueField;
	}

	public void setValueField(Integer valueField) {
		this.valueField = valueField;
	}

	public boolean isMinMaxFromField() {
		return minMaxFromField;
	}

	public void setMinMaxFromField(boolean minMaxFromField) {
		this.minMaxFromField = minMaxFromField;
	}

	public boolean isThresholdFromField() {
		return thresholdFromField;
	}

	public void setThresholdFromField(boolean thresholdFromField) {
		this.thresholdFromField = thresholdFromField;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public Float getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(Float minThreshold) {
		this.minThreshold = minThreshold;
	}

	public Float getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(Float maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	public boolean isTargetFromField() {
		return targetFromField;
	}

	public void setTargetFromField(boolean targetFromField) {
		this.targetFromField = targetFromField;
	}

	public Float getTarget() {
		return target;
	}

	public void setTarget(Float target) {
		this.target = target;
	}

	public Integer getTolerance() {
		return tolerance;
	}

	public void setTolerance(Integer tolerance) {
		this.tolerance = tolerance;
	}

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public int getBgAlpha() {
		return bgAlpha;
	}

	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getColorBadValue() {
		return colorBadValue;
	}

	public void setColorBadValue(String colorBadValue) {
		this.colorBadValue = colorBadValue;
	}

	public String getColorMediumValue() {
		return colorMediumValue;
	}

	public void setColorMediumValue(String colorMediumValue) {
		this.colorMediumValue = colorMediumValue;
	}

	public String getColorGoodValue() {
		return colorGoodValue;
	}

	public void setColorGoodValue(String colorGoodValue) {
		this.colorGoodValue = colorGoodValue;
	}

	public int getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(int innerRadius) {
		this.innerRadius = innerRadius;
	}

	public int getOuterRadius() {
		return outerRadius;
	}

	public void setOuterRadius(int outerRadius) {
		this.outerRadius = outerRadius;
	}

	public int getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}

	public int getStopAngle() {
		return stopAngle;
	}

	public void setStopAngle(int stopAngle) {
		this.stopAngle = stopAngle;
	}

	public boolean isBulb() {
		return bulb;
	}

	public void setBulb(boolean bulb) {
		this.bulb = bulb;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.GAUGE;
	}

	@Override
	protected void clearData() {
		this.dataset = null;
		this.valueField = null;
	}
}
