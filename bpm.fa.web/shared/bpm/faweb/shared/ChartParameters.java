package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ChartParameters implements IsSerializable{
	private String chartTitle;
	
	private String chartType;
	
	//public String chartType;
	public String defaultRenderer;
	
	private List<String> selectedGroup = new ArrayList<String>();
	private List<String> selectedData = new ArrayList<String>();
	private List<String> selectedChartFilters = new ArrayList<String>();
	private String measure = "";
	
	public ChartParameters() { }

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(String defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public List<String> getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(List<String> selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public List<String> getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(List<String> selectedData) {
		this.selectedData = selectedData;
	}

	public List<String> getSelectedChartFilters() {
		return selectedChartFilters;
	}

	public void setSelectedChartFilters(List<String> selectedChartFilters) {
		this.selectedChartFilters = selectedChartFilters;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}
}
