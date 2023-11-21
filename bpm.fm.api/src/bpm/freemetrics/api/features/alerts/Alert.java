package bpm.freemetrics.api.features.alerts;

import java.util.Calendar;
import java.util.Date;

public class Alert {

	private int id = 0;

	private Integer  alUserId;
	private Integer  alGroupId;
	private Integer  alAlarmId;
	private Integer  alActionId;
	private Boolean  alIsPublic;
	private String   name;
	private String   alDescription;
	private Integer  alUpdateType;
	private Date     alStartDate;
	private Date     alEndDate;
	private Date     alCreationDate;
	private Boolean  alIsActive;
	private String   alMessage;
	private String   alCheckMethod;
	private String   alCheckIntervalCode;
	private Integer  alCheckIntervalSecond;
	private Date     alLastCheckDate;
	private Date     alNextCheckDate;

	/**
	 * @return the alUserId
	 */
	public Integer getAlUserId() {
		return alUserId;
	}

	/**
	 * @param alUserId the alUserId to set
	 */
	public void setAlUserId(Integer alUserId) {
		this.alUserId = alUserId;
	}

	/**
	 * @return the alGroupId
	 */
	public Integer getAlGroupId() {
		return alGroupId;
	}

	/**
	 * @param alGroupId the alGroupId to set
	 */
	public void setAlGroupId(Integer alGroupId) {
		this.alGroupId = alGroupId;
	}

	/**
	 * @return the alAlarmId
	 */
	public Integer getAlAlarmId() {
		return alAlarmId;
	}

	/**
	 * @param alAlarmId the alAlarmId to set
	 */
	public void setAlAlarmId(Integer alAlarmId) {
		this.alAlarmId = alAlarmId;
	}

	/**
	 * @return the alActionId
	 */
	public Integer getAlActionId() {
		return alActionId;
	}

	/**
	 * @param alActionId the alActionId to set
	 */
	public void setAlActionId(Integer alActionId) {
		this.alActionId = alActionId;
	}

	/**
	 * @return the alIsPublic
	 */
	public Boolean getAlIsPublic() {
		return alIsPublic;
	}

	/**
	 * @param alIsPublic the alIsPublic to set
	 */
	public void setAlIsPublic(Boolean alIsPublic) {
		this.alIsPublic = alIsPublic;
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
	 * @return the alDescription
	 */
	public String getAlDescription() {
		return alDescription;
	}

	/**
	 * @param alDescription the alDescription to set
	 */
	public void setAlDescription(String alDescription) {
		this.alDescription = alDescription;
	}

	/**
	 * @return the alUpdateType
	 */
	public Integer getAlUpdateType() {
		return alUpdateType;
	}

	/**
	 * @param alUpdateType the alUpdateType to set
	 */
	public void setAlUpdateType(Integer alUpdateType) {
		this.alUpdateType = alUpdateType;
	}

	/**
	 * @return the alStartDate
	 */
	public Date getAlStartDate() {
		return alStartDate;
	}

	/**
	 * @param alStartDate the alStartDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAlStartDate(Date alStartDate) {

		if(alStartDate != null && alStartDate.getSeconds() == 0){
			alStartDate.setHours(Calendar.getInstance().HOUR);
			alStartDate.setMinutes(Calendar.getInstance().MINUTE);
			alStartDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.alStartDate = alStartDate;
	}

	/**
	 * @return the alEndDate
	 */
	public Date getAlEndDate() {
		return alEndDate;
	}

	/**
	 * @param alEndDate the alEndDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAlEndDate(Date alEndDate) {

		if(alEndDate!= null  && alEndDate != null && alEndDate.getSeconds() == 0){
			alEndDate.setHours(Calendar.getInstance().HOUR);
			alEndDate.setMinutes(Calendar.getInstance().MINUTE);
			alEndDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.alEndDate = alEndDate;
	}

	/**
	 * @return the alCreationDate
	 */
	public Date getAlCreationDate() {
		return alCreationDate;
	}

	/**
	 * @param alCreationDate the alCreationDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAlCreationDate(Date alCreationDate) {

		if(alCreationDate!= null && alCreationDate.getSeconds() == 0){
			alCreationDate.setHours(Calendar.getInstance().HOUR);
			alCreationDate.setMinutes(Calendar.getInstance().MINUTE);
			alCreationDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.alCreationDate = alCreationDate;
	}

	/**
	 * @return the alIsActive
	 */
	public Boolean getAlIsActive() {
		return alIsActive;
	}

	/**
	 * @param alIsActive the alIsActive to set
	 */
	public void setAlIsActive(Boolean alIsActive) {
		this.alIsActive = alIsActive;
	}

	/**
	 * @return the alMessage
	 */
	public String getAlMessage() {
		return alMessage;
	}

	/**
	 * @param alMessage the alMessage to set
	 */
	public void setAlMessage(String alMessage) {
		this.alMessage = alMessage;
	}

	/**
	 * @return the alCheckMethod
	 */
	public String getAlCheckMethod() {
		return alCheckMethod;
	}

	/**
	 * @param alCheckMethod the alCheckMethod to set
	 */
	public void setAlCheckMethod(String alCheckMethod) {
		this.alCheckMethod = alCheckMethod;
	}

	/**
	 * @return the alCheckIntervalCode
	 */
	public String getAlCheckIntervalCode() {
		return alCheckIntervalCode;
	}

	/**
	 * @param alCheckIntervalCode the alCheckIntervalCode to set
	 */
	public void setAlCheckIntervalCode(String alCheckIntervalCode) {
		this.alCheckIntervalCode = alCheckIntervalCode;
	}

	/**
	 * @return the alCheckIntervalSecond
	 */
	public Integer getAlCheckIntervalSecond() {
		return alCheckIntervalSecond;
	}

	/**
	 * @param alCheckIntervalSecond the alCheckIntervalSecond to set
	 */
	public void setAlCheckIntervalSecond(Integer alCheckIntervalSecond) {
		this.alCheckIntervalSecond = alCheckIntervalSecond;
	}

	/**
	 * @return the alLastCheckDate
	 */
	public Date getAlLastCheckDate() {
		return alLastCheckDate;
	}

	/**
	 * @param alLastCheckDate the alLastCheckDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAlLastCheckDate(Date alLastCheckDate) {


		if(alLastCheckDate!= null  && alLastCheckDate.getSeconds() == 0){
			alLastCheckDate.setHours(Calendar.getInstance().HOUR);
			alLastCheckDate.setMinutes(Calendar.getInstance().MINUTE);
			alLastCheckDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.alLastCheckDate = alLastCheckDate;
	}

	/**
	 * @return the alNextCheckDate
	 */
	public Date getAlNextCheckDate() {
		return alNextCheckDate;
	}

	/**
	 * @param alNextCheckDate the alNextCheckDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setAlNextCheckDate(Date alNextCheckDate) {


		if(alNextCheckDate != null  && alNextCheckDate.getSeconds() == 0){
			alNextCheckDate.setHours(Calendar.getInstance().HOUR);
			alNextCheckDate.setMinutes(Calendar.getInstance().MINUTE);
			alNextCheckDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.alNextCheckDate = alNextCheckDate;
	}

	public Alert() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
