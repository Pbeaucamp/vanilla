package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comments implements Serializable {

	public enum CommentStatus {
		ALL(0),
		DOCUMENT_VALIDATE(1),
		DOCUMENT_UNVALIDATE(2);
		
		private int type;

		private static Map<Integer, CommentStatus> map = new HashMap<Integer, CommentStatus>();
		static {
			for (CommentStatus type : CommentStatus.values()) {
				map.put(type.getType(), type);
			}
		}
		
		private CommentStatus(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}

		public static CommentStatus valueOf(int type) {
			return map.get(type);
		}
	}

	public enum AdminStatus {
		VALIDATED(0),
		NO_STATUS(1),
		UNVALIDATED(2);
		
		private int type;

		private static Map<Integer, AdminStatus> map = new HashMap<Integer, AdminStatus>();
		static {
			for (AdminStatus type : AdminStatus.values()) {
				map.put(type.getType(), type);
			}
		}
		
		private AdminStatus(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}

		public static AdminStatus valueOf(int type) {
			return map.get(type);
		}
	}
	
	private static final long serialVersionUID = 1L;

	private int commentId = 0;
	private String user = "";
	private String message = "";
	private int docId = 0;
	private Date commentDate = new Date();
	private int documentReferenceId;
	
	//placement com sur doc
	private int page = -1;
	private double x;
	private double y;

	//Used for simple document
	private CommentStatus status = CommentStatus.ALL;
	
	//Used with tasks
	private int taskId;
	private Boolean approved;
	
	private AdminStatus adminApproved = AdminStatus.VALIDATED;

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	
	public CommentStatus getCommentStatus() {
		return status;
	}
	
	public int getStatus() {
		return status.getType();
	}
	
	public void setCommentStatus(CommentStatus status) {
		this.status = status;
	}
	
	public void setStatus(int status) {
		this.status = CommentStatus.valueOf(status);
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public AdminStatus getAdminStatus() {
		return adminApproved;
	}
	
	public void setAdminStatus(AdminStatus adminApproved) {
		this.adminApproved = adminApproved;
	}
	
	public int getAdminApproved() {
		return adminApproved.getType();
	}
	
	public void setAdminApproved(int status) {
		this.adminApproved = AdminStatus.valueOf(status);
	}

	public int getDocumentReferenceId() {
		return documentReferenceId;
	}

	public void setDocumentReferenceId(int documentReferenceId) {
		this.documentReferenceId = documentReferenceId;
	}
}
