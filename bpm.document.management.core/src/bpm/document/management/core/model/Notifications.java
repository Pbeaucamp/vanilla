package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notifications implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum NotificationType {
		CLASSIC(0), 
		DEMAT_SELECT_VALIDATORS(1), 
		DEMAT_VALIDATE_DOCUMENT(2),
		DEMAT_SUSPEND_DOCUMENT(3);

		private int type;

		private static Map<Integer, NotificationType> map = new HashMap<Integer, NotificationType>();
		static {
			for (NotificationType type : NotificationType.values()) {
				map.put(type.getType(), type);
			}
		}

		private NotificationType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static NotificationType valueOf(int type) {
			return map.get(type);
		}
	}

	private int notificationId;

	private int userId;
	private int notifiedBy;
	private int docId;

	private Date creationDate;
	private Date notificationDate = new Date();

	private String message = "";

	private Boolean isTrigger = false;
	private String fileType = "";
	private NotificationType type = NotificationType.CLASSIC;
	
	private Boolean deleted = false;

	public Notifications() {
	}

	public Notifications(Date notificationDate, String message, int userId, int docId, Boolean isTrigger, int notifiedBy, String fileType) {
		this.notificationDate = notificationDate;
		this.message = message;
		this.userId = userId;
		this.docId = docId;
		this.isTrigger = isTrigger;
		this.notifiedBy = notifiedBy;
		this.fileType = fileType;
	}

	public Notifications(NotificationType type, int userId, int objectId) {
		this.type = type;
		this.userId = userId;
		this.docId = objectId;
		this.creationDate = new Date();
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Boolean getIsTrigger() {
		return isTrigger;
	}

	public void setIsTrigger(Boolean isTrigger) {
		this.isTrigger = isTrigger;
	}

	public int getNotifiedBy() {
		return notifiedBy;
	}

	public void setNotifiedBy(int notifiedBy) {
		this.notifiedBy = notifiedBy;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public NotificationType getNotificationType() {
		return type;
	}

	public int getType() {
		return type.getType();
	}

	public void setNotificationType(NotificationType type) {
		this.type = type;
	}

	public void setType(int type) {
		this.type = NotificationType.valueOf(type);
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
}
