package bpm.vanilla.platform.core.beans;

import java.util.List;

public class FMMetricBean {

	private int id;
	private String name;
	private boolean isIndicator;
	private String periodicity;
	
	private List<String> possibleYears;
	
	public FMMetricBean(){}
	
	public FMMetricBean(int id, String name, boolean isIndicator, String periodicity) {
		this.id = id;
		this.name = name;
		this.isIndicator = isIndicator;
		this.periodicity = periodicity;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isIndicator() {
		return isIndicator;
	}
	
	public void setIndicator(boolean isIndicator) {
		this.isIndicator = isIndicator;
	}
	
	public String getPeriodicity() {
		return periodicity;
	}
	
	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
	
	public void setPossibleYears(List<String> years) {
		this.possibleYears = years;
	}
	
	public List<String> getPossibleYears() {
		return possibleYears;
	}
}
