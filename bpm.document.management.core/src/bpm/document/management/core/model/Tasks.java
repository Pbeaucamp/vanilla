package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tasks implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TaskStatus {
		DELEGATED(0), 
		VALIDATION(1),
		RESPONSE(2),
		OK(3),
		ENDED(4),
		REASSIGNEDVAL(5),
		REASSIGNEDREP(6),
		VALIDATIONRESPONSE(7),
		REREAD(8),
		SIGN(9),
		CREATEDOC(10),
		VALIDATION_MARKET(11),
		VALIDATION_DEMAT(12),
		EVENT(13);
		
		private int type;

		private static Map<Integer, TaskStatus> map = new HashMap<Integer, TaskStatus>();
		static {
			for (TaskStatus type : TaskStatus.values()) {
				map.put(type.getType(), type);
			}
		}

		private TaskStatus(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TaskStatus valueOf(int type) {
			return map.get(type);
		}
	}
	
	private int taskId = 0;

	private String userEmail = "";
	private String taskTitle = "";
	private String taskGiverEmail = "";
	private String taskTitleDesc = "";

	private int docId = 0;

	private Date taskDateClosed = new Date();
	private Date taskDateDone = new Date();
	private Date taskDatePostponed = new Date();
	private Date taskCommentDate = new Date();
	private Date taskDateNotFeasible = new Date();
	private Date remindDate;
	private Date dueDate = new Date();
	private Date creationDate=new Date();

	private String taskStatus = "";
	private String taskComment = "";
	private String groupTree = "";

	//Add for mail
	private TaskStatus status = TaskStatus.VALIDATION;
	
	private Validation validation;
	private Reply response;
	
	private Integer validationId = 0;
	private Integer replyId = 0;
	
	private Boolean active = true;
	
	private Integer indexTask;
	
	private IObject document;
	
	private Boolean suspended = false;
	
	private Validation validationResponse;
	private Integer validationResponseId;
	
	private int typeTaskId;
	
	private int idWorkflowInstance = 0;
	
	
	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

	public Reply getResponse() {
		return response;
	}

	public void setResponse(Reply response) {
		this.response = response;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getTaskGiverEmail() {
		return taskGiverEmail;
	}

	public void setTaskGiverEmail(String taskGiverEmail) {
		this.taskGiverEmail = taskGiverEmail;
	}

	public String getTaskTitleDesc() {
		return taskTitleDesc;
	}

	public void setTaskTitleDesc(String taskTitleDesc) {
		this.taskTitleDesc = taskTitleDesc;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getTaskDateClosed() {
		return taskDateClosed;
	}

	public void setTaskDateClosed(Date taskDateClosed) {
		this.taskDateClosed = taskDateClosed;
	}

	public Date getTaskDateDone() {
		return taskDateDone;
	}

	public void setTaskDateDone(Date taskDateDone) {
		this.taskDateDone = taskDateDone;
	}

	public Date getTaskDatePostponed() {
		return taskDatePostponed;
	}

	public void setTaskDatePostponed(Date taskDatePostponed) {
		this.taskDatePostponed = taskDatePostponed;
	}

	public String getTaskComment() {
		return taskComment;
	}

	public void setTaskComment(String taskComment) {
		this.taskComment = taskComment;
	}

	public Date getTaskCommentDate() {
		return taskCommentDate;
	}

	public void setTaskCommentDate(Date taskCommentDate) {
		this.taskCommentDate = taskCommentDate;
	}

	public String getGroupTree() {
		return groupTree;
	}

	public void setGroupTree(String groupTree) {
		this.groupTree = groupTree;
	}

	public Date getTaskDateNotFeasible() {
		return taskDateNotFeasible;
	}

	public void setTaskDateNotFeasible(Date taskDateNotFeasible) {
		this.taskDateNotFeasible = taskDateNotFeasible;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(Date remindDate) {
		this.remindDate = remindDate;
	}

	public TaskStatus getStatusType() {
		return status;
	}
	
	public int getStatus() {
		if(status == null) {
			return -1;
		}
		return status.getType();
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	public void setStatus(int status) {
		this.status = TaskStatus.valueOf(status);
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getIndexTask() {
		return indexTask;
	}

	public void setIndexTask(Integer indexTask) {
		this.indexTask = indexTask;
	}

	public Integer getValidationId() {
		return validationId;
	}

	public void setValidationId(Integer validationId) {
		this.validationId = validationId;
	}

	public Integer getReplyId() {
		return replyId;
	}

	public void setReplyId(Integer replyId) {
		this.replyId = replyId;
	}

	/**
	 * Careful, this not set by default.
	 * Can be null.
	 * @return
	 */
	public IObject getDocument() {
		return document;
	}

	public void setDocument(IObject document) {
		this.document = document;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	public Validation getValidationResponse() {
		return validationResponse;
	}

	public void setValidationResponse(Validation validationResponse) {
		this.validationResponse = validationResponse;
	}

	public Integer getValidationResponseId() {
		return validationResponseId;
	}

	public void setValidationResponseId(Integer validationResponseId) {
		this.validationResponseId = validationResponseId;
	}

	public int getTypeTaskId() {
		return typeTaskId;
	}

	public void setTypeTaskId(int typeTaskId) {
		this.typeTaskId = typeTaskId;
	}

	public int getIdWorkflowInstance() {
		return idWorkflowInstance;
	}

	public void setIdWorkflowInstance(int idWorkflowInstance) {
		this.idWorkflowInstance = idWorkflowInstance;
	}


}
