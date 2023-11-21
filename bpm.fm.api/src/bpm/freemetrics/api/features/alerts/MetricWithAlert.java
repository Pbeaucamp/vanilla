package bpm.freemetrics.api.features.alerts;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MetricWithAlert {

	private int id = 0;
	private Integer maApplicationId;
	private Integer maMetricsId;
	private Integer maUserId;
	private String  maTest;
	private String  name;
	private String  maMailto;
	private String  maDescription;
	private Date    maBeginDate;
	private Date    maEndDate;
	private Date    maCreationDate;
	private Integer maAlertId = -1;

	private Set<AlertCondition> fmAlertconditions = new HashSet<AlertCondition>(0);

	public MetricWithAlert() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the maApplicationId
	 */
	public Integer getMaApplicationId() {
		return maApplicationId;
	}

	/**
	 * @param maApplicationId the maApplicationId to set
	 */
	public void setMaApplicationId(Integer maApplicationId) {
		this.maApplicationId = maApplicationId;
	}

	/**
	 * @return the maMetricsId
	 */
	public Integer getMaMetricsId() {
		return maMetricsId;
	}

	/**
	 * @param maMetricsId the maMetricsId to set
	 */
	public void setMaMetricsId(Integer maMetricsId) {
		this.maMetricsId = maMetricsId;
	}

	/**
	 * @return the maUserId
	 */
	public Integer getMaUserId() {
		return maUserId;
	}

	/**
	 * @param maUserId the maUserId to set
	 */
	public void setMaUserId(Integer maUserId) {
		this.maUserId = maUserId;
	}

	/**
	 * @return the maTest
	 */
	public String getMaTest() {
		return maTest;
	}

	/**
	 * @param maTest the maTest to set
	 */
	public void setMaTest(String maTest) {
		this.maTest = maTest;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the maMailto
	 */
	public String getMaMailto() {
		return maMailto;
	}

	/**
	 * @param maMailto the maMailto to set
	 */
	public void setMaMailto(String maMailto) {
		this.maMailto = maMailto;
	}

	/**
	 * @return the maDescription
	 */
	public String getMaDescription() {
		return maDescription;
	}

	/**
	 * @param maDescription the maDescription to set
	 */
	public void setMaDescription(String maDescription) {
		this.maDescription = maDescription;
	}

	/**
	 * @return the maBeginDate
	 */
	public Date getMaBeginDate() {
		return maBeginDate;
	}

	/**
	 * @param maBeginDate the maBeginDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setMaBeginDate(Date maBeginDate) {

		if(maBeginDate!= null && maBeginDate.getSeconds() == 0){
			maBeginDate.setHours(Calendar.getInstance().HOUR);
			maBeginDate.setMinutes(Calendar.getInstance().MINUTE);
			maBeginDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.maBeginDate = maBeginDate;
	}

	/**
	 * @return the maEndDate
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public Date getMaEndDate() {
		return maEndDate;
	}

	/**
	 * @param maEndDate the maEndDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setMaEndDate(Date maEndDate) {

		if(maEndDate!= null && maEndDate.getSeconds() == 0){
			maEndDate.setHours(Calendar.getInstance().HOUR);
			maEndDate.setMinutes(Calendar.getInstance().MINUTE);
			maEndDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.maEndDate = maEndDate;
	}

	/**
	 * @return the maCreationDate
	 */
	public Date getMaCreationDate() {
		return maCreationDate;
	}

	/**
	 * @param maCreationDate the maCreationDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setMaCreationDate(Date maCreationDate) {

		if(maCreationDate!= null && maCreationDate.getSeconds() == 0){
			maCreationDate.setHours(Calendar.getInstance().HOUR);
			maCreationDate.setMinutes(Calendar.getInstance().MINUTE);
			maCreationDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.maCreationDate = maCreationDate;
	}

	/**
	 * @return the maAlertId
	 */
	public Integer getMaAlertId() {
		return maAlertId;
	}

	/**
	 * @param maAlertId the maAlertId to set
	 */
	public void setMaAlertId(Integer maAlertId) {
		if(maAlertId != null)
			this.maAlertId = maAlertId;
	}

	public Set<AlertCondition> getFmAlertconditions() {
		return this.fmAlertconditions;
	}

	public void setFmAlertconditions(Set<AlertCondition> fmAlertconditions) {
		this.fmAlertconditions = fmAlertconditions;
	}

}
