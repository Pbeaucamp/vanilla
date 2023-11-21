package bpm.gwt.commons.shared.fmdt.metadata;

import bpm.vanilla.platform.core.beans.chart.SavedChart;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataChart implements IsSerializable {
	
	public String name;
	private MetadataQuery parent;
	
	private SavedChart chart;
	
	public MetadataChart() { }

	public MetadataChart(String name, SavedChart chart) {
		this.name = name;
		this.chart = chart;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SavedChart getChart() {
		return chart;
	}
	
	public void setChart(SavedChart chart) {
		this.chart = chart;
	}
	
	public MetadataQuery getParent() {
		return parent;
	}
	
	public void setParent(MetadataQuery parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
