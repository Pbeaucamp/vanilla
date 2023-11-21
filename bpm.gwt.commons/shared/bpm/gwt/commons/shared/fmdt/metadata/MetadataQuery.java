package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataQuery implements IsSerializable {
	
	public String name;
	private MetadataQueries parent;
	
	private List<MetadataChart> charts;
	
	public MetadataQuery() { }

	public MetadataQuery(String name, List<MetadataChart> charts) {
		this.name = name;
		setCharts(charts);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<MetadataChart> getCharts() {
		return charts;
	}
	
	public void setCharts(List<MetadataChart> charts) {
		this.charts = charts;
		if (charts != null) {
			for (MetadataChart chart : charts) {
				chart.setParent(this);
			}
		}
	}
	
	public MetadataQueries getParent() {
		return parent;
	}
	
	public void setParent(MetadataQueries parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
