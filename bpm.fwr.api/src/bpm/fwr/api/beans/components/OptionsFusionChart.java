package bpm.fwr.api.beans.components;

import java.io.Serializable;
import java.util.HashMap;

public class OptionsFusionChart implements Serializable {

	private static final long serialVersionUID = 192542862221532685L;

	private String chartTitle;
	private String operation;
	private boolean is3D;
	private boolean isGlassEnabled;
	private boolean isStacked;
	
	private int height;
	private int width;

	private boolean showLabels = true;
	private boolean showValues = true;
	private boolean showBorders = true;
	private boolean showLegend = true;

	private boolean legendOnRight = true; // Can be on bottom otherwise

	private String xAxisName = "";
	private String yAxisName = "";

	private String backgroundColor = "";

	public OptionsFusionChart() {
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public boolean isLegendOnRight() {
		return legendOnRight;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public void setLegendOnRight(boolean legendOnRight) {
		this.legendOnRight = legendOnRight;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowBorders(boolean showBorders) {
		this.showBorders = showBorders;
	}

	public boolean isShowBorders() {
		return showBorders;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public String getyAxisName() {
		return yAxisName;
	}

	public boolean is3D() {
		return is3D;
	}

	public void set3D(boolean is3d) {
		is3D = is3d;
	}

	public boolean isGlassEnabled() {
		return isGlassEnabled;
	}

	public void setGlassEnabled(boolean isGlassEnabled) {
		this.isGlassEnabled = isGlassEnabled;
	}

	public boolean isStacked() {
		return isStacked;
	}

	public void setStacked(boolean isStacked) {
		this.isStacked = isStacked;
	}

	public HashMap<String, String> getProperties() {
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("showLabels", showLabels ? "1" : "0");
		props.put("showValues", showValues ? "1" : "0");
		props.put("showBorder", showBorders ? "1" : "0");
		props.put("showLegend", showLegend ? "1" : "0");
		props.put("legendPosition", legendOnRight ? "RIGHT" : "BOTTOM");
		props.put("xAxisName", xAxisName);
		props.put("yAxisName", yAxisName);
		props.put("bgColor", backgroundColor);
		return props;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setChartOperation(String operation) {
		this.operation = operation;
	}
	
	public String getChartOperation() {
		return operation;
	}

//	public String getChartOperationQuery() {
//		return operation.toLowerCase().replace("somme", "sum").replace("nombre", "count");
//	}
}
