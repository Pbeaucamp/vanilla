package bpm.gateway.core.transformations.googleanalytics;

public interface ITime extends IMetricsDimensions {
	
	public String getBeginDate();
	
	public void setBeginDate(String beginDate);
	
	public String getEndDate();

	public void setEndDate(String endDate);
}
