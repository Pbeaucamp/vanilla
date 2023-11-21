package bpm.fmloader.client.dto;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommentDTO implements IsSerializable {

	private int id;
	private int valueId;
	private String comment;
	private int metricId;
	private int applicationId;
	private Date commentDate;
	private int userId;
	private int locked;
	
	public CommentDTO() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValueId() {
		return valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public int getLocked() {
		return locked;
	}
		
}
