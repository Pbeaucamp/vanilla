package org.fasd.olap.aggregation;

import org.fasd.datasource.DataObjectItem;

public class ClassicAggregation implements IMeasureAggregation {

	private DataObjectItem origin;
	private String aggregator;
	
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String getFaXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("	<classic-aggregation>\n");
		
		buf.append("				<aggregator>" + aggregator + "</aggregator>\n");
		if(origin != null) {
			buf.append("				<originId>" + origin.getId() + "</originId>\n");
		}
		else {
			buf.append("				<originId>" + originId + "</originId>\n");
		}
		buf.append("				<level>" + level + "</level>\n");
		
		buf.append("			</classic-aggregation>\n");
		
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
