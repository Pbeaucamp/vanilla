package bpm.fwr.api.beans.components;

import java.io.Serializable;
import java.util.HashMap;

public class OptionsFusionMap implements Serializable {
	
	private static final long serialVersionUID = -3021982751419789863L;
	
	private boolean showLabels = true;
	private boolean showShadow = true;
	private boolean showTooltips = true;
	private boolean showLegend = true;
	
	private boolean legendOnRight = true; //Can be on bottom otherwise
	
	private int fontSize = 9;
	
	private String fontColor = "";
	private String backgroundColor = "";
	
	public OptionsFusionMap() { }
	
	public boolean isShowLabels() {
		return showLabels;
	}

	public boolean isShowShadow() {
		return showShadow;
	}

	public boolean isShowTooltips() {
		return showTooltips;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public boolean isLegendOnRight() {
		return legendOnRight;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getFontColor() {
		return fontColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public void setShowShadow(boolean showShadow) {
		this.showShadow = showShadow;
	}
	
	public void setShowTooltips(boolean showTooltips) {
		this.showTooltips = showTooltips;
	}
	
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	
	public void setLegendOnRight(boolean legendOnRight) {
		this.legendOnRight = legendOnRight;
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public HashMap<String, String> getProperties(){
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("showLabels", showLabels ? "1" : "0");
		props.put("showShadow", showShadow ? "1" : "0");
		props.put("showToolTip", showTooltips ? "1" : "0");
		props.put("showLegend", showLegend ? "1" : "0");
		props.put("legendPosition", legendOnRight ? "RIGHT" : "BOTTOM");
		props.put("baseFontSize", fontSize+"");
		props.put("baseFontColor", fontColor);
		props.put("bgColor", backgroundColor);
		return props;
	}
}
