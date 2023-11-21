package bpm.fd.core.component;

import java.io.Serializable;

public class ChartOption implements Serializable {

	private static final long serialVersionUID = 1L;

	private String lineSerieName = "";

	// generic
	private int bgAlpha = 0;
	private int bgSWFAlpha = 0;

	private String subCaption = "";
	private boolean showLabel = true;
	private boolean showValues = true;
	private String bgColor = "FFFFFF";

	private boolean showBorder = true;
	private String borderColor;
	private int borderThickness = 1;

	private boolean multiLineLabels = false;

	private String baseFontColor = "000000";
	private int baseFontSize = 10;

	private boolean dynamicLegend = false;
	private int labelSize = 0;

	private boolean exportEnable = false;

	// pie
	private int slicingDistance = 0;
	private int pieSliceDepth = 0;
	private int pieRadius = 0;

	// non pie
	private boolean rotateLabels = false;
	private boolean slantLabels = false;
	private boolean rotateValues = false;
	private boolean placeValuesInside = false;
	private boolean rotateYAxisName = false;
	private String PYAxisName = "", SYAxisName = "", xAxisName = "";

	// number format
	private boolean formatNumber = false;
	private boolean formatNumberScale = false;
	private String numberPrefix = "";
	private String numberSuffix = "";
	private String decimalSeparator = ".";
	private String thousandSeparator = ",";

	private int decimals = 2;
	private boolean forceDecimal = false;

	public String getLineSerieName() {
		return lineSerieName;
	}

	public void setLineSerieName(String lineSerieName) {
		this.lineSerieName = lineSerieName;
	}

	public int getBgAlpha() {
		return bgAlpha;
	}

	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}

	public int getBgSWFAlpha() {
		return bgSWFAlpha;
	}

	public void setBgSWFAlpha(int bgSWFAlpha) {
		this.bgSWFAlpha = bgSWFAlpha;
	}

	public String getSubCaption() {
		return subCaption;
	}

	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
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

	public boolean isShowBorder() {
		return showBorder;
	}

	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public int getBorderThickness() {
		return borderThickness;
	}

	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
	}

	public boolean isMultiLineLabels() {
		return multiLineLabels;
	}

	public void setMultiLineLabels(boolean multiLineLabels) {
		this.multiLineLabels = multiLineLabels;
	}

	public String getBaseFontColor() {
		return baseFontColor;
	}

	public void setBaseFontColor(String baseFontColor) {
		this.baseFontColor = baseFontColor;
	}

	public int getBaseFontSize() {
		return baseFontSize;
	}

	public void setBaseFontSize(int baseFontSize) {
		this.baseFontSize = baseFontSize;
	}

	public boolean isDynamicLegend() {
		return dynamicLegend;
	}

	public void setDynamicLegend(boolean dynamicLegend) {
		this.dynamicLegend = dynamicLegend;
	}

	public int getLabelSize() {
		return labelSize;
	}

	public void setLabelSize(int labelSize) {
		this.labelSize = labelSize;
	}

	public boolean isExportEnable() {
		return exportEnable;
	}

	public void setExportEnable(boolean exportEnable) {
		this.exportEnable = exportEnable;
	}

	public int getSlicingDistance() {
		return slicingDistance;
	}

	public void setSlicingDistance(int slicingDistance) {
		this.slicingDistance = slicingDistance;
	}

	public int getPieSliceDepth() {
		return pieSliceDepth;
	}

	public void setPieSliceDepth(int pieSliceDepth) {
		this.pieSliceDepth = pieSliceDepth;
	}

	public int getPieRadius() {
		return pieRadius;
	}

	public void setPieRadius(int pieRadius) {
		this.pieRadius = pieRadius;
	}

	public boolean isRotateLabels() {
		return rotateLabels;
	}

	public void setRotateLabels(boolean rotateLabels) {
		this.rotateLabels = rotateLabels;
	}

	public boolean isSlantLabels() {
		return slantLabels;
	}

	public void setSlantLabels(boolean slantLabels) {
		this.slantLabels = slantLabels;
	}

	public boolean isRotateValues() {
		return rotateValues;
	}

	public void setRotateValues(boolean rotateValues) {
		this.rotateValues = rotateValues;
	}

	public boolean isPlaceValuesInside() {
		return placeValuesInside;
	}

	public void setPlaceValuesInside(boolean placeValuesInside) {
		this.placeValuesInside = placeValuesInside;
	}

	public boolean isRotateYAxisName() {
		return rotateYAxisName;
	}

	public void setRotateYAxisName(boolean rotateYAxisName) {
		this.rotateYAxisName = rotateYAxisName;
	}

	public String getPYAxisName() {
		return PYAxisName;
	}

	public void setPYAxisName(String pYAxisName) {
		PYAxisName = pYAxisName;
	}

	public String getSYAxisName() {
		return SYAxisName;
	}

	public void setSYAxisName(String sYAxisName) {
		SYAxisName = sYAxisName;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public boolean isFormatNumber() {
		return formatNumber;
	}

	public void setFormatNumber(boolean formatNumber) {
		this.formatNumber = formatNumber;
	}

	public boolean isFormatNumberScale() {
		return formatNumberScale;
	}

	public void setFormatNumberScale(boolean formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	public String getNumberPrefix() {
		return numberPrefix;
	}

	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

	public String getNumberSuffix() {
		return numberSuffix;
	}

	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public String getThousandSeparator() {
		return thousandSeparator;
	}

	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public boolean isForceDecimal() {
		return forceDecimal;
	}

	public void setForceDecimal(boolean forceDecimal) {
		this.forceDecimal = forceDecimal;
	}

}
