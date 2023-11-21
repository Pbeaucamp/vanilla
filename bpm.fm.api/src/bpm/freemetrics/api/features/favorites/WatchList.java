package bpm.freemetrics.api.features.favorites;

import java.util.Date;

/**
 * @author Belgarde
 *
 */
public class WatchList {

	private int id = 0;

	private int mwApplicationId;

	private int mwUserId;

	private int mwMetricsId;

	private Date mwWatchFromDate;

	private Date mwWatchToDate;

	private String mwComment;

	private Date mwCreationDate;

	public WatchList() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the mwApplicationId
	 */
	public int getMwApplicationId() {
		return mwApplicationId;
	}

	/**
	 * @param mwApplicationId the mwApplicationId to set
	 */
	public void setMwApplicationId(int mwApplicationId) {
		this.mwApplicationId = mwApplicationId;
	}

	/**
	 * @return the mwUserId
	 */
	public int getMwUserId() {
		return mwUserId;
	}

	/**
	 * @param mwUserId the mwUserId to set
	 */
	public void setMwUserId(int mwUserId) {
		this.mwUserId = mwUserId;
	}

	/**
	 * @return the mwMetricsId
	 */
	public int getMwMetricsId() {
		return mwMetricsId;
	}

	/**
	 * @param mwMetricsId the mwMetricsId to set
	 */
	public void setMwMetricsId(int mwMetricsId) {
		this.mwMetricsId = mwMetricsId;
	}

	/**
	 * @return the mwWatchFromDate
	 */
	public Date getMwWatchFromDate() {
		return mwWatchFromDate;
	}

	/**
	 * @param mwWatchFromDate the mwWatchFromDate to set
	 */
	public void setMwWatchFromDate(Date mwWatchFromDate) {
		this.mwWatchFromDate = mwWatchFromDate;
	}

	/**
	 * @return the mwWatchToDate
	 */
	public Date getMwWatchToDate() {
		return mwWatchToDate;
	}

	/**
	 * @param mwWatchToDate the mwWatchToDate to set
	 */
	public void setMwWatchToDate(Date mwWatchToDate) {
		this.mwWatchToDate = mwWatchToDate;
	}

	/**
	 * @return the mwComment
	 */
	public String getMwComment() {
		return mwComment;
	}

	/**
	 * @param mwComment the mwComment to set
	 */
	public void setMwComment(String mwComment) {
		this.mwComment = mwComment;
	}

	/**
	 * @return the mwCreationDate
	 */
	public Date getMwCreationDate() {
		return mwCreationDate;
	}

	/**
	 * @param mwCreationDate the mwCreationDate to set
	 */
	public void setMwCreationDate(Date mwCreationDate) {
		this.mwCreationDate = mwCreationDate;
	}

	
}
