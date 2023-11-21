package bpm.freemetrics.api.features.alerts;

import java.util.Calendar;
import java.util.Date;

public class AlertCondition {

	private int id = 0;

	private Integer acMetricswithalertId;
	private String  acDefinition;
	private Date    acStartDate;
	private Date    acEndDate;
	private Date    acCreationDate;

	public AlertCondition() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the acMetricswithalertId
	 */
	public Integer getAcMetricswithalertId() {
		return acMetricswithalertId;
	}

	/**
	 * @param acMetricswithalertId the acMetricswithalertId to set
	 */
	public void setAcMetricswithalertId(Integer acMetricswithalertId) {
		this.acMetricswithalertId = acMetricswithalertId;
	}

	/**
	 * @return the acDefinition
	 */
	public String getAcDefinition() {
		return acDefinition;
	}

	/**
	 * @param acDefinition the acDefinition to set
	 */
	public void setAcDefinition(String acDefinition) {
		this.acDefinition = acDefinition;
	}

	/**
	 * @return the acStartDate
	 */
	public Date getAcStartDate() {
		return acStartDate;
	}

	/**
	 * @param acStartDate the acStartDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAcStartDate(Date acStartDate) {

		if(acStartDate!= null && acStartDate.getSeconds() == 0){
			acStartDate.setHours(Calendar.getInstance().HOUR);
			acStartDate.setMinutes(Calendar.getInstance().MINUTE);
			acStartDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.acStartDate = acStartDate;
	}

	/**
	 * @return the acEndDate
	 */
	public Date getAcEndDate() {
		return acEndDate;
	}

	/**
	 * @param acEndDate the acEndDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAcEndDate(Date acEndDate) {

		if(acEndDate!= null && acEndDate.getSeconds() == 0){
			acEndDate.setHours(Calendar.getInstance().HOUR);
			acEndDate.setMinutes(Calendar.getInstance().MINUTE);
			acEndDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.acEndDate = acEndDate;
	}

	/**
	 * @return the acCreationDate
	 */
	public Date getAcCreationDate() {
		return acCreationDate;
	}

	/**
	 * @param acCreationDate the acCreationDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAcCreationDate(Date acCreationDate) {

		if(acCreationDate!= null && acCreationDate.getSeconds() == 0){
			acCreationDate.setHours(Calendar.getInstance().HOUR);
			acCreationDate.setMinutes(Calendar.getInstance().MINUTE);
			acCreationDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.acCreationDate = acCreationDate;
	}

}
