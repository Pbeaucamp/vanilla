package bpm.united.olap.api.model.aggregation;

import bpm.united.olap.api.datasource.DataObjectItem;

public class LastAggregation implements ILevelAggregation {

	private String aggregator;
	private String level;
	private DataObjectItem origin;
	private String relatedDimension;
	
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

	public String getRelatedDimension() {
		return relatedDimension;
	}

	public void setRelatedDimension(String relatedDimension) {
		this.relatedDimension = relatedDimension;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
