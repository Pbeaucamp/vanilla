package bpm.freemetrics.api.features.favorites;

import java.util.Date;

/**
 * @author Belgarde
 *
 */
public class UserAlertePreference {

	private int id = 0;

	private int mpaApplicationId;
	private int mpaUserId;
	private int mpaAlertId;
	private boolean mpaAlwaysSendAlert;
	private Date mpaDateFrom;
	private Date mpaDateTo;
	private String mpaAlertMethod;
	private String mpaAlertReference;
	private String mpaAlertMessage;
	private String mpaComment;
	private Date mpaCreationDate;
	
	public UserAlertePreference() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the mpaApplicationId
	 */
	public int getMpaApplicationId() {
		return mpaApplicationId;
	}

	/**
	 * @param mpaApplicationId the mpaApplicationId to set
	 */
	public void setMpaApplicationId(int mpaApplicationId) {
		this.mpaApplicationId = mpaApplicationId;
	}

	/**
	 * @return the mpaUserId
	 */
	public int getMpaUserId() {
		return mpaUserId;
	}

	/**
	 * @param mpaUserId the mpaUserId to set
	 */
	public void setMpaUserId(int mpaUserId) {
		this.mpaUserId = mpaUserId;
	}

	/**
	 * @return the mpaAlertId
	 */
	public int getMpaAlertId() {
		return mpaAlertId;
	}

	/**
	 * @param mpaAlertId the mpaAlertId to set
	 */
	public void setMpaAlertId(int mpaAlertId) {
		this.mpaAlertId = mpaAlertId;
	}

	/**
	 * @return the mpaAlwaysSendAlert
	 */
	public boolean isMpaAlwaysSendAlert() {
		return mpaAlwaysSendAlert;
	}

	/**
	 * @param mpaAlwaysSendAlert the mpaAlwaysSendAlert to set
	 */
	public void setMpaAlwaysSendAlert(boolean mpaAlwaysSendAlert) {
		this.mpaAlwaysSendAlert = mpaAlwaysSendAlert;
	}

	/**
	 * @return the mpaDateFrom
	 */
	public Date getMpaDateFrom() {
		return mpaDateFrom;
	}

	/**
	 * @param mpaDateFrom the mpaDateFrom to set
	 */
	public void setMpaDateFrom(Date mpaDateFrom) {
		this.mpaDateFrom = mpaDateFrom;
	}

	/**
	 * @return the mpaDateTo
	 */
	public Date getMpaDateTo() {
		return mpaDateTo;
	}

	/**
	 * @param mpaDateTo the mpaDateTo to set
	 */
	public void setMpaDateTo(Date mpaDateTo) {
		this.mpaDateTo = mpaDateTo;
	}

	/**
	 * @return the mpaAlertMethod
	 */
	public String getMpaAlertMethod() {
		return mpaAlertMethod;
	}

	/**
	 * @param mpaAlertMethod the mpaAlertMethod to set
	 */
	public void setMpaAlertMethod(String mpaAlertMethod) {
		this.mpaAlertMethod = mpaAlertMethod;
	}

	/**
	 * @return the mpaAlertReference
	 */
	public String getMpaAlertReference() {
		return mpaAlertReference;
	}

	/**
	 * @param mpaAlertReference the mpaAlertReference to set
	 */
	public void setMpaAlertReference(String mpaAlertReference) {
		this.mpaAlertReference = mpaAlertReference;
	}

	/**
	 * @return the mpaAlertMessage
	 */
	public String getMpaAlertMessage() {
		return mpaAlertMessage;
	}

	/**
	 * @param mpaAlertMessage the mpaAlertMessage to set
	 */
	public void setMpaAlertMessage(String mpaAlertMessage) {
		this.mpaAlertMessage = mpaAlertMessage;
	}

	/**
	 * @return the mpaComment
	 */
	public String getMpaComment() {
		return mpaComment;
	}

	/**
	 * @param mpaComment the mpaComment to set
	 */
	public void setMpaComment(String mpaComment) {
		this.mpaComment = mpaComment;
	}

	/**
	 * @return the mpaCreationDate
	 */
	public Date getMpaCreationDate() {
		return mpaCreationDate;
	}

	/**
	 * @param mpaCreationDate the mpaCreationDate to set
	 */
	public void setMpaCreationDate(Date mpaCreationDate) {
		this.mpaCreationDate = mpaCreationDate;
	}


}
