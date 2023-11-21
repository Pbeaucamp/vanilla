package bpm.united.olap.api.model.aggregation;

import bpm.united.olap.api.datasource.DataObjectItem;

public class ClassicAggregation implements ILevelAggregation {

	private String level;
	private String aggregator;
	private DataObjectItem origin;
	
	@Override
	public String getLevel() {
		return level;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}

	public DataObjectItem getOrigin() {
		return origin;
	}

	public void setOrigin(DataObjectItem origin) {
		this.origin = origin;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
