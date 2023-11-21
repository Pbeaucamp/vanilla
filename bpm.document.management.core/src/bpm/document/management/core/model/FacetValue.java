package bpm.document.management.core.model;

import java.io.Serializable;

public class FacetValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	private long count;
	private boolean isSelected;
	
	public FacetValue() {
	}

	public FacetValue(String value, long count, boolean isSelected) {
		this.value = value;
		this.count = count;
		this.isSelected = isSelected;
	}

	public String getValue() {
		return value;
	}

	public long getCount() {
		return count;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
}