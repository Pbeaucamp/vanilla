package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class DataSort implements Serializable {

	private static final long serialVersionUID = 1L;

	private String column;
	private boolean ascending;
	
	public DataSort() {
	}
	
	public DataSort(String column, boolean ascending) {
		this.column = column;
		this.ascending = ascending;
	}
	
	public String getColumn() {
		return column;
	}
	
	public boolean isAscending() {
		return ascending;
	}

}
