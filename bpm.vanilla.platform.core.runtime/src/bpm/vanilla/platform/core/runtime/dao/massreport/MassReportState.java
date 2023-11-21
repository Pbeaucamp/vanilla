package bpm.vanilla.platform.core.runtime.dao.massreport;

import java.util.Date;

/**
 * been to store a massReport state
 * 
 * each tasked to run a report will create on row of this object
 * once the tasked has been run and handled, the generated boolean is set to true
 * 
 * 
 * @author ludo
 *
 */
public class MassReportState {
	private int massReportItemId;
	private Date date;
	private String activityInstanceUuid;
	private int generated = 0;
	private int reportItemId;
	private long id;
	
	
	/**
	 * @return the reportItemId
	 */
	protected int getReportItemId() {
		return reportItemId;
	}
	/**
	 * @param reportItemId the reportItemId to set
	 */
	protected void setReportItemId(int reportItemId) {
		this.reportItemId = reportItemId;
	}
	/**
	 * @return the activityInstanceUuid
	 */
	protected String getActivityInstanceUuid() {
		return activityInstanceUuid;
	}
	/**
	 * @param activityInstanceUuid the activityInstanceUuid to set
	 */
	protected void setActivityInstanceUuid(String activityInstanceUuid) {
		this.activityInstanceUuid = activityInstanceUuid;
	}

	/**
	 * @return the massReportItemId
	 */
	protected int getMassReportItemId() {
		return massReportItemId;
	}
	/**
	 * @param massReportItemId the massReportItemId to set
	 */
	protected void setMassReportItemId(int massReportItemId) {
		this.massReportItemId = massReportItemId;
	}
	/**
	 * @return the date
	 */
	protected Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	protected void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the generated
	 */
	protected int getGenerated() {
		return generated;
	}
	/**
	 * @param generated the generated to set
	 */
	protected void setGenerated(int generated) {
		this.generated = generated;
	}
	/**
	 * @return the id
	 */
	protected long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	protected void setId(long id) {
		this.id = id;
	}
	
	
	
}
