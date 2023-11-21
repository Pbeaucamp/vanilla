package bpm.fa.api.olap;

import java.io.Serializable;

public class OLAPCalcMeasure implements Serializable {
	private String uniq;
	private String name;
	
	/*
	 * needed for parsing
	 */
	public OLAPCalcMeasure() {
		
	}
	
	public OLAPCalcMeasure(String unique, String name) {
		this.uniq = unique;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniq() {
		return uniq;
	}

	public void setUniq(String uniq) {
		this.uniq = uniq;
	}
}
