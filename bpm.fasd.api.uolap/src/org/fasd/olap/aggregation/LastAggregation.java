package org.fasd.olap.aggregation;

import org.fasd.datasource.DataObjectItem;

public class LastAggregation implements IMeasureAggregation {

	private DataObjectItem origin;
	private String aggregator;
	
	private String relatedDimension;
	private String level;
	
	private String originId;
	
	public DataObjectItem getOrigin() {
		return origin;
	}
	
	public void setOrigin(DataObjectItem origin) {
		this.origin = origin;
		this.originId = origin.getId();
	}
	
	public String getAggregator() {
		return aggregator;
	}
	
	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}
	
	public String getRelatedDimension() {
		return relatedDimension;
	}
	
	public void setRelatedDimension(String relatedDimension) {
		this.relatedDimension = relatedDimension;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String getFaXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("	<last-aggregation>\n");
		
		buf.append("				<aggregator>" + aggregator + "</aggregator>\n");
		if(origin != null) {
			buf.append("				<originId>" + origin.getId() + "</originId>\n");
		}
		else {
			buf.append("				<originId>" + originId + "</originId>\n");
		}
		buf.append("				<relatedDimension>" + relatedDimension + "</relatedDimension>\n");
		buf.append("				<level>" + level + "</level>\n");
		
		buf.append("			</last-aggregation>\n");
		
		return buf.toString();
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public String getOriginId() {
		return originId;
	}

	@Override
	public String getTreeLabel() {
		return aggregator;
	}
	
	
}
