package bpm.fwr.api.beans.dataset;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GroupAgregation implements Serializable {

	public static final String AGGREGATE_ON_TOTAL =  "aggregateOnTotal";
	
	private String label;
	private String customLabel;
	private String column;
	private String type;
	private boolean isFooter;
	private int index;
	
	public GroupAgregation() {
		
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFooter() {
		return isFooter;
	}

	public void setFooter(boolean isFooter) {
		this.isFooter = isFooter;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public void setFooter(String isFooter) {
		if(isFooter.equalsIgnoreCase("true")) {
			this.isFooter = true;
		}
		else {
			this.isFooter = false;
		}
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setIndex(String index) {
		this.index = Integer.parseInt(index);
	}

	public int getIndex() {
		return index;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public String getCustomLabel() {
		if(customLabel == null || customLabel.equals("") || customLabel.equalsIgnoreCase("null") ||
				customLabel.equalsIgnoreCase("null")){
			return null;
		}
		return customLabel;
	}
}
