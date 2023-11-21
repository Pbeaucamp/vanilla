package bpm.freemetrics.api.organisation.metrics;

import java.util.Date;

public class MetricInteraction {

	private int id;
	
	private int miApplicationId;

	private int miMetricTopId;

	private int miMetricDownId;

	private Date miCreationDate;

    
    public MetricInteraction() {
    	super();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the miApplicationId
	 */
	public int getMiApplicationId() {
		return miApplicationId;
	}

	/**
	 * @param miApplicationId the miApplicationId to set
	 */
	public void setMiApplicationId(int miApplicationId) {
		this.miApplicationId = miApplicationId;
	}

	/**
	 * @return the miMetricTopId
	 */
	public int getMiMetricTopId() {
		return miMetricTopId;
	}

	/**
	 * @param miMetricTopId the miMetricTopId to set
	 */
	public void setMiMetricTopId(int miMetricTopId) {
		this.miMetricTopId = miMetricTopId;
	}

	/**
	 * @return the miMetricDownId
	 */
	public int getMiMetricDownId() {
		return miMetricDownId;
	}

	/**
	 * @param miMetricDownId the miMetricDownId to set
	 */
	public void setMiMetricDownId(int miMetricDownId) {
		this.miMetricDownId = miMetricDownId;
	}

	/**
	 * @return the miCreationDate
	 */
	public Date getMiCreationDate() {
		return miCreationDate;
	}

	/**
	 * @param miCreationDate the miCreationDate to set
	 */
	public void setMiCreationDate(Date miCreationDate) {
		this.miCreationDate = miCreationDate;
	}

}
