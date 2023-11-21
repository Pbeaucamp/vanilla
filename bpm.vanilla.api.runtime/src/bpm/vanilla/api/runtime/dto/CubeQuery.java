package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.fa.api.olap.OLAPChart;

public class CubeQuery {

	private boolean swapAxes;
	private boolean active;
	private boolean showEmpty;
	private List<String> drills;
	private List<String> treeviewExpand;
	private List<String> filters;
	private List<String> cols;
	private List<String> rows;
	private List<String> measures;
	private List<CubePercentMeasure> percentMeasures;
	private String base64img;
	private OLAPChart chart;

	public boolean isSwapAxes() {
		return swapAxes;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isShowEmpty() {
		return showEmpty;
	}

	public List<String> getDrills() {
		return drills;
	}

	public List<String> getTreeviewExpand() {
		return treeviewExpand;
	}

	public List<String> getFilters() {
		return filters;
	}

	public List<String> getCols() {
		return cols;
	}

	public List<String> getRows() {
		return rows;
	}

	public List<String> getMeasures() {
		return measures;
	}

	public List<CubePercentMeasure> getPercentMeasures() {
		return percentMeasures;
	}

	public String getBase64img() {
		return base64img;
	}
	

	public OLAPChart getChart() {
		return chart;
	}
	

	public void setSwapAxes(boolean swapAxes) {
		this.swapAxes = swapAxes;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setShowEmpty(boolean showEmpty) {
		this.showEmpty = showEmpty;
	}

	public void setDrills(List<String> drills) {
		this.drills = drills;
	}

	public void setTreeviewExpand(List<String> treeviewExpand) {
		this.treeviewExpand = treeviewExpand;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public void setCols(List<String> cols) {
		this.cols = cols;
	}

	public void setRows(List<String> rows) {
		this.rows = rows;
	}

	public void setMeasures(List<String> measures) {
		this.measures = measures;
	}

	public void setPercentMeasures(List<CubePercentMeasure> percentMeasures) {
		this.percentMeasures = percentMeasures;
	}

	public void setBase64img(String base64img) {
		this.base64img = base64img;
	}

	public void setChart(OLAPChart chart) {
		this.chart = chart;
	}
	
	

}
