package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class TasksComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int commentId=0;
	private String message="";
	private String taskUserEmail="";
	private int taskId=0;
	private Date commentDate=new Date();
	
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTaskUserEmail() {
		return taskUserEmail;
	}
	public void setTaskUserEmail(String user) {
		this.taskUserEmail = user;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date taskCommentDate) {
		this.commentDate = taskCommentDate;
	}
}
