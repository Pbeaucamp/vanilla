package bpm.freemetrics.api.features.favorites;

import java.util.Date;

/**
 * @author Belgarde
 *
 */
public class UserPreference {

	private int id = 0;

	private Integer mpApplicationId;
	private Integer mpUserId;
	private Date mpDateFrom;
	private Date mpDateTo;
	private String	mpAlertMethod;
	private String	mpAlertReference;
	private String	mpComment;
	private Date	mpCreationDate;

	public UserPreference() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the mpApplicationId
	 */
	public Integer getMpApplicationId() {
		return mpApplicationId;
	}

	/**
	 * @param mpApplicationId the mpApplicationId to set
	 */
	public void setMpApplicationId(Integer mpApplicationId) {
		this.mpApplicationId = mpApplicationId;
	}

	/**
	 * @return the mpUserId
	 */
	public Integer getMpUserId() {
		return mpUserId;
	}

	/**
	 * @param mpUserId the mpUserId to set
	 */
	public void setMpUserId(Integer mpUserId) {
		this.mpUserId = mpUserId;
	}

	/**
	 * @return the mpDateFrom
	 */
	public Date getMpDateFrom() {
		return mpDateFrom;
	}

	/**
	 * @param mpDateFrom the mpDateFrom to set
	 */
	public void setMpDateFrom(Date mpDateFrom) {
		this.mpDateFrom = mpDateFrom;
	}

	/**
	 * @return the mpDateTo
	 */
	public Date getMpDateTo() {
		return mpDateTo;
	}

	/**
	 * @param mpDateTo the mpDateTo to set
	 */
	public void setMpDateTo(Date mpDateTo) {
		this.mpDateTo = mpDateTo;
	}

	/**
	 * @return the mpAlertMethod
	 */
	public String getMpAlertMethod() {
		return mpAlertMethod;
	}

	/**
	 * @param mpAlertMethod the mpAlertMethod to set
	 */
	public void setMpAlertMethod(String mpAlertMethod) {
		this.mpAlertMethod = mpAlertMethod;
	}

	/**
	 * @return the mpAlertReference
	 */
	public String getMpAlertReference() {
		return mpAlertReference;
	}

	/**
	 * @param mpAlertReference the mpAlertReference to set
	 */
	public void setMpAlertReference(String mpAlertReference) {
		this.mpAlertReference = mpAlertReference;
	}

	/**
	 * @return the mpComment
	 */
	public String getMpComment() {
		return mpComment;
	}

	/**
	 * @param mpComment the mpComment to set
	 */
	public void setMpComment(String mpComment) {
		this.mpComment = mpComment;
	}

	/**
	 * @return the mpCreationDate
	 */
	public Date getMpCreationDate() {
		return mpCreationDate;
	}

	/**
	 * @param mpCreationDate the mpCreationDate to set
	 */
	public void setMpCreationDate(Date mpCreationDate) {
		this.mpCreationDate = mpCreationDate;
	}

}
