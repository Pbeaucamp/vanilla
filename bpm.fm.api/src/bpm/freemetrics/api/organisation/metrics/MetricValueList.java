package bpm.freemetrics.api.organisation.metrics;

import java.util.Calendar;
import java.util.Date;


public class MetricValueList {

	private int id = 0;

	private Integer metricId;
	private Integer applicationId;
	private Date    dateFrom = Calendar.getInstance().getTime();
	private Date    dateTo   = Calendar.getInstance().getTime();
	private Float   min;
	private Float   max;
	private Float   realValue;
	private Float   target;
	
	public MetricValueList() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the metricId
	 */
	public Integer getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId the metricId to set
	 */
	public void setMetricId(Integer metricId) {
		this.metricId = metricId;
	}

	/**
	 * @return the applicationId
	 */
	public Integer getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		Date now = new Date();
		if(dateFrom != null && dateFrom.getSeconds() == 0){
			dateFrom.setHours(now.getHours());
			dateFrom.setMinutes(now.getMinutes());
			dateFrom.setSeconds(now.getSeconds());
		}
		
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		Date now = new Date();
		
		if(dateTo != null && dateTo.getSeconds() == 0){
			dateTo.setHours(now.getHours());
			dateTo.setMinutes(now.getMinutes());
			dateTo.setSeconds(now.getSeconds());
		}
		
		this.dateTo = dateTo;
	}

	/**
	 * @return the min
	 */
	public Float getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Float min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public Float getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Float max) {
		this.max = max;
	}

	/**
	 * @return the realValue
	 */
	public Float getRealValue() {
		return realValue;
	}

	/**
	 * @param realValue the realValue to set
	 */
	public void setRealValue(Float realValue) {
		this.realValue = realValue;
	}

	/**
	 * @return the target
	 */
	public Float getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Float target) {
		this.target = target;
	}
}
