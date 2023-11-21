package bpm.freemetrics.api.features.favorites;

import java.util.Date;

/**
 * @author Belgarde
 *
 */
public class UserAction {

	private int id = 0;

	private int moApplicationId;
	private int moMetricsId;
	private int moGroupId;
	private int moUserId;
	private String moActionName;
	private String moActionType;
	private Date moActionEvaluationCalendar;
	private float moActionTargetValue;
	private Date moActionStartingDate;
	private Date moActionEncodingDate;
	private String moActionComment;
	private String moActionResult;
	private Date moActionCreationDate;

	
	public UserAction() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the moApplicationId
	 */
	public int getMoApplicationId() {
		return moApplicationId;
	}

	/**
	 * @param moApplicationId the moApplicationId to set
	 */
	public void setMoApplicationId(int moApplicationId) {
		this.moApplicationId = moApplicationId;
	}

	/**
	 * @return the moMetricsId
	 */
	public int getMoMetricsId() {
		return moMetricsId;
	}

	/**
	 * @param moMetricsId the moMetricsId to set
	 */
	public void setMoMetricsId(int moMetricsId) {
		this.moMetricsId = moMetricsId;
	}

	/**
	 * @return the moGroupId
	 */
	public int getMoGroupId() {
		return moGroupId;
	}

	/**
	 * @param moGroupId the moGroupId to set
	 */
	public void setMoGroupId(int moGroupId) {
		this.moGroupId = moGroupId;
	}

	/**
	 * @return the moUserId
	 */
	public int getMoUserId() {
		return moUserId;
	}

	/**
	 * @param moUserId the moUserId to set
	 */
	public void setMoUserId(int moUserId) {
		this.moUserId = moUserId;
	}

	/**
	 * @return the moActionName
	 */
	public String getMoActionName() {
		return moActionName;
	}

	/**
	 * @param moActionName the moActionName to set
	 */
	public void setMoActionName(String moActionName) {
		this.moActionName = moActionName;
	}

	/**
	 * @return the moActionType
	 */
	public String getMoActionType() {
		return moActionType;
	}

	/**
	 * @param moActionType the moActionType to set
	 */
	public void setMoActionType(String moActionType) {
		this.moActionType = moActionType;
	}

	/**
	 * @return the moActionEvaluationCalendar
	 */
	public Date getMoActionEvaluationCalendar() {
		return moActionEvaluationCalendar;
	}

	/**
	 * @param moActionEvaluationCalendar the moActionEvaluationCalendar to set
	 */
	public void setMoActionEvaluationCalendar(Date moActionEvaluationCalendar) {
		this.moActionEvaluationCalendar = moActionEvaluationCalendar;
	}

	/**
	 * @return the moActionTargetValue
	 */
	public float getMoActionTargetValue() {
		return moActionTargetValue;
	}

	/**
	 * @param moActionTargetValue the moActionTargetValue to set
	 */
	public void setMoActionTargetValue(float moActionTargetValue) {
		this.moActionTargetValue = moActionTargetValue;
	}

	/**
	 * @return the moActionStartingDate
	 */
	public Date getMoActionStartingDate() {
		return moActionStartingDate;
	}

	/**
	 * @param moActionStartingDate the moActionStartingDate to set
	 */
	public void setMoActionStartingDate(Date moActionStartingDate) {
		this.moActionStartingDate = moActionStartingDate;
	}

	/**
	 * @return the moActionEncodingDate
	 */
	public Date getMoActionEncodingDate() {
		return moActionEncodingDate;
	}

	/**
	 * @param moActionEncodingDate the moActionEncodingDate to set
	 */
	public void setMoActionEncodingDate(Date moActionEncodingDate) {
		this.moActionEncodingDate = moActionEncodingDate;
	}

	/**
	 * @return the moActionComment
	 */
	public String getMoActionComment() {
		return moActionComment;
	}

	/**
	 * @param moActionComment the moActionComment to set
	 */
	public void setMoActionComment(String moActionComment) {
		this.moActionComment = moActionComment;
	}

	/**
	 * @return the moActionResult
	 */
	public String getMoActionResult() {
		return moActionResult;
	}

	/**
	 * @param moActionResult the moActionResult to set
	 */
	public void setMoActionResult(String moActionResult) {
		this.moActionResult = moActionResult;
	}

	/**
	 * @return the moActionCreationDate
	 */
	public Date getMoActionCreationDate() {
		return moActionCreationDate;
	}

	/**
	 * @param moActionCreationDate the moActionCreationDate to set
	 */
	public void setMoActionCreationDate(Date moActionCreationDate) {
		this.moActionCreationDate = moActionCreationDate;
	}

	
}
