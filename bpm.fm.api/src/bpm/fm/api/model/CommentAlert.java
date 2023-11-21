package bpm.fm.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.User;

@Entity
@Table (name = "fm_alert_comment")
public class CommentAlert implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "raised_id")
	private int raisedId;
	
	@Column (name = "comment_date")
	private Date date;
	
	@Column (name = "resolution_comment")
	private boolean resolutionComment = false;
	
	@Column (name = "comment_text")
	private String comment;
	
	@Column (name = "user_id")
	private int userId;

	@Transient
	private User user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRaisedId() {
		return raisedId;
	}

	public void setRaisedId(int raisedId) {
		this.raisedId = raisedId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isResolutionComment() {
		return resolutionComment;
	}

	public void setResolutionComment(boolean resolutionComment) {
		this.resolutionComment = resolutionComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
