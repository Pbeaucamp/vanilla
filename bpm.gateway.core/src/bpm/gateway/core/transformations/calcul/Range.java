package bpm.gateway.core.transformations.calcul;

public class Range {
	private String firstValue;
	private String secondValue;
	private String output;
	private int intervalType;
	
	public String getFirstValue() {
		return firstValue;
	}
	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}
	public String getSecondValue() {
		return secondValue;
	}
	public void setSecondValue(String firstSecond) {
		this.secondValue = firstSecond;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public int getIntervalType() {
		return intervalType;
	}
	public void setIntervalType(int intervalType) {
		this.intervalType = intervalType;
	}
	public void setIntervalType(String intervalType) {
		try{
			this.intervalType = Integer.parseInt(intervalType);
		}finally{
			
		}
		
	}
	
	
	
	
}
