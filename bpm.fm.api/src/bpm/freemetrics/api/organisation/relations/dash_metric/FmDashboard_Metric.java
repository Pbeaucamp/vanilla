package bpm.freemetrics.api.organisation.relations.dash_metric;

import java.util.Date;

public class FmDashboard_Metric {
	
	private int id;
	
	private Integer dmApplicationId;
	private Integer dmDashboardId;
	private Integer dmMetricsId;
	private Date    dmCreationDate;
	
	

	public FmDashboard_Metric() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the dmApplicationId
	 */
	public Integer getDmApplicationId() {
		return dmApplicationId;
	}

	/**
	 * @param dmApplicationId the dmApplicationId to set
	 */
	public void setDmApplicationId(Integer dmApplicationId) {
		this.dmApplicationId = dmApplicationId;
	}

	/**
	 * @return the dmDashboardId
	 */
	public Integer getDmDashboardId() {
		return dmDashboardId;
	}

	/**
	 * @param dmDashboardId the dmDashboardId to set
	 */
	public void setDmDashboardId(Integer dmDashboardId) {
		this.dmDashboardId = dmDashboardId;
	}

	/**
	 * @return the dmMetricsId
	 */
	public Integer getDmMetricsId() {
		return dmMetricsId;
	}

	/**
	 * @param dmMetricsId the dmMetricsId to set
	 */
	public void setDmMetricsId(Integer dmMetricsId) {
		this.dmMetricsId = dmMetricsId;
	}

	/**
	 * @return the dmCreationDate
	 */
	public Date getDmCreationDate() {
		return dmCreationDate;
	}

	/**
	 * @param dmCreationDate the dmCreationDate to set
	 */
	public void setDmCreationDate(Date dmCreationDate) {
		this.dmCreationDate = dmCreationDate;
	}


}
