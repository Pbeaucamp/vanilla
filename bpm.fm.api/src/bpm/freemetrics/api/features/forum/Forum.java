package bpm.freemetrics.api.features.forum;

import java.util.Date;

public class Forum {

	private int id = 0;
	 private Integer mfApplicationId;
     private Integer mfMetricsId;
     private Integer mfUserId;
     private Integer mfMetricsValueId;
     private Date mfCommentDate;
     private String mfComment;
     private Integer mfRefComment;
     private Integer mfLocked;
    
 
	/**
	 * @return the mfApplicationId
	 */
	public Integer getMfApplicationId() {
		return mfApplicationId;
	}

	/**
	 * @param mfApplicationId the mfApplicationId to set
	 */
	public void setMfApplicationId(Integer mfApplicationId) {
		this.mfApplicationId = mfApplicationId;
	}

	/**
	 * @return the mfMetricsId
	 */
	public Integer getMfMetricsId() {
		return mfMetricsId;
	}

	/**
	 * @param mfMetricsId the mfMetricsId to set
	 */
	public void setMfMetricsId(Integer mfMetricsId) {
		this.mfMetricsId = mfMetricsId;
	}

	/**
	 * @return the mfUserId
	 */
	public Integer getMfUserId() {
		return mfUserId;
	}

	/**
	 * @param mfUserId the mfUserId to set
	 */
	public void setMfUserId(Integer mfUserId) {
		this.mfUserId = mfUserId;
	}

	/**
	 * @return the mfMetricsValueId
	 */
	public Integer getMfMetricsValueId() {
		return mfMetricsValueId;
	}

	/**
	 * @param mfMetricsValueId the mfMetricsValueId to set
	 */
	public void setMfMetricsValueId(Integer mfMetricsValueId) {
		this.mfMetricsValueId = mfMetricsValueId;
	}

	/**
	 * @return the mfCommentDate
	 */
	public Date getMfCommentDate() {
		return mfCommentDate;
	}

	/**
	 * @param mfCommentDate the mfCommentDate to set
	 */
	public void setMfCommentDate(Date mfCommentDate) {
		this.mfCommentDate = mfCommentDate;
	}

	/**
	 * @return the mfComment
	 */
	public String getMfComment() {
		return mfComment;
	}

	/**
	 * @param mfComment the mfComment to set
	 */
	public void setMfComment(String mfComment) {
		this.mfComment = mfComment;
	}

	/**
	 * @return the mfRefComment
	 */
	public Integer getMfRefComment() {
		return mfRefComment;
	}

	/**
	 * @param mfRefComment the mfRefComment to set
	 */
	public void setMfRefComment(Integer mfRefComment) {
		this.mfRefComment = mfRefComment;
	}

	public Forum() {
    	super();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMfLocked(Integer mfLocked) {
		this.mfLocked = mfLocked;
	}

	/**
	 * @return the mfLocked
	 */
	public Integer getMfLocked() {
		return mfLocked;
	}
}
