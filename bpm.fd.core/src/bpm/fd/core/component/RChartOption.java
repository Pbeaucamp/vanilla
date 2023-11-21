package bpm.fd.core.component;

import java.io.Serializable;

public class RChartOption implements Serializable {
	//Generic options
	//Parametres communs à tous les types de graphe
	// generic
	private boolean showLabel = true;
	private boolean dynamicR = false;

	//Bar
	private boolean seperationBar = true ;
	
	//Histogramme
	private boolean density = true ;
	private int bins = 0;

	// non pie
	private String yAxisName = "", xAxisName = "" ;
	private static final long serialVersionUID = 1L;
	
	public boolean isShowLabel() {
		return this.showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public boolean isDynamicR() {
		return this.dynamicR;
	}

	public void setDynamicR(boolean dynamic) {
		this.dynamicR = dynamic;
	}

	public String getyAxisName() {
		return this.yAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public String getxAxisName() {
		return this.xAxisName;
	}

	public void setxAxisName(String xAxis) {
		this.xAxisName = xAxis;
	}

	public boolean isSeperationBar() {
		return this.seperationBar;
	}

	public void setSeperationBar(boolean seperationBar) {
		this.seperationBar = seperationBar;
	}

	public boolean isDensity() {
		return density;
	}

	public void setDensity(boolean density) {
		this.density = density;
	}

	public int getBins() {
		return bins;
	}

	public void setBins(int bins) {
		this.bins = bins;
	}
}
