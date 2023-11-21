package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author manu
 *
 * Adds support for bi object access requests.
 * 
 * This class support request for External Document (RUN)
 * and for the Document coming from a Document Manager's search (VIEW_DOC_SEARCH)
 * 
 *  
 */
public class AccessRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Only RUN for now
	 */
	public enum RequestOp {
		RUN ("Run", 0),
		VIEW_DOC_SEARCH ("ViewDocSearch", 1);
		
		private String operationName;
		private int operationId;
		
		private RequestOp(String operationName, int operationId) {
			this.operationId = operationId;
			this.operationName = operationName;
		}
		
		public int getOperationId() {
			return operationId;
		}
		
		public String getOperationName() {
			return operationName;
		}
	}
	
	/**
	 * Accepted and refused 
	 */
	public enum RequestAnswer {
		ACCEPTED ("Accepted", 0),
		REFUSED ("Refused", 1),
		PENDING ("Pending", 2);
		
		private String answerName;
		private int answerId;
		
		private RequestAnswer(String answerName, int answerId) {
			this.answerId = answerId;
			this.answerName = answerName;
		}
		
		public int getAnswerId() {
			return answerId;
		}
		
		public String getAnswerName() {
			return answerName;
		}
	}
	
	private int id;
	
	//id for object
	private int repositoryId; 
	private int itemId;
	
	//id for group demand + userId for tracking
	private int groupId;
	private int userId;
	
	//request information
	private Date requestDate;
	private int requestUserId;//user for id request, the owner
	//private int requestGroupId;
	private String requestInfo;
	private RequestOp requestOp;
	
	//request answer
	private Date answerDate;
	private String answerInfo;
	private RequestAnswer answerOp;
	private int answerUserId; //who answered
	
	private boolean userUpdated; //if new/change for users
	private boolean adminUpdated; //if new/change for admin
	
	//for hibernate and enums
	private int requestOpId;
	private int answerOpId;
	//end members
	
	private String userName;
	private String groupName;
	private String itemName;
	private String requestUserName;
	
	public AccessRequest() { }
	
	public AccessRequest(int repositoryId, int itemId, int groupId,
			int userId, Date requestDate, int requestUserId, /*int requestGroupId,*/ String requestInfo,
			RequestOp requestOp, Date answerDate, String answerInfo,
			RequestAnswer answerOp, int answerUserId, boolean userUpdated, 
			boolean adminUpdated) {
		
		this.repositoryId = repositoryId;
		this.itemId = itemId;
		this.groupId = groupId;
		this.userId = userId;
		this.requestUserId = requestUserId;
		this.requestDate = requestDate;
		this.requestInfo = requestInfo;
		
		setRequestOp(requestOp);
		//this.requestOp = requestOp;
		
		this.answerDate = answerDate;
		this.answerInfo = answerInfo;
		//this.answerOp = answerOp;
		setAnswerOp(answerOp);
		this.answerUserId = answerUserId;
		
		this.userUpdated = userUpdated;
		this.adminUpdated = adminUpdated;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Date getRequestDate() {
		return requestDate;
	}
	
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	
	public String getRequestInfo() {
		return requestInfo;
	}
	
	public void setRequestInfo(String requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	public int getRequestUserId() {
		return requestUserId;
	}
	
	public void setRequestUserId(int requestUserId) {
		this.requestUserId = requestUserId;
	}
	
	public RequestOp getRequestOp() {
		return requestOp;
	}
	
	public void setRequestOp(RequestOp requestOp) {
		this.requestOp = requestOp;
		this.requestOpId = requestOp.getOperationId();
	}
	
	public Date getAnswerDate() {
		return answerDate;
	}
	
	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}
	
	public String getAnswerInfo() {
		return answerInfo;
	}
	
	public void setAnswerInfo(String answerInfo) {
		this.answerInfo = answerInfo;
	}
	
	public RequestAnswer getAnswerOp() {
		return answerOp;
	}
	
	public void setAnswerOp(RequestAnswer answerOp) {
		this.answerOp = answerOp;
		this.answerOpId = answerOp.getAnswerId();
	}
	
	public int getAnswerUserId() {
		return answerUserId;
	}
	
	public void setAnswerUserId(int answerUserId) {
		this.answerUserId = answerUserId;
	}

	public boolean isUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(boolean userUpdated) {
		this.userUpdated = userUpdated;
	}

	public boolean isAdminUpdated() {
		return adminUpdated;
	}

	public void setAdminUpdated(boolean adminUpdated) {
		this.adminUpdated = adminUpdated;
	}
	
	/**
	 * DO NOT USE! only for hibernate
	 * @return
	 */
	public int getRequestOpId() {
		return requestOpId;
	}
	
	/**
	 * DO NOT USE! only for hibernate
	 * @return
	 */
	public void setRequestOpId(int requestOpId) {
		if (requestOpId == 0) {
			//this.requestOpId = requestOpId;
			setRequestOp(RequestOp.RUN);
		}
		else if (requestOpId == 1) {
			//this.requestOpId = requestOpId;
			setRequestOp(RequestOp.VIEW_DOC_SEARCH);
		}
		else {
			throw new UnsupportedOperationException("Operation with opId " + requestOpId + " is not supported");
		}
	}
	
	/**
	 * DO NOT USE! only for hibernate
	 * @return
	 */
	public int getAnswerOpId() {
		return answerOpId;
	}
	
	/**
	 * DO NOT USE! only for hibernate
	 * @return
	 */
	public void setAnswerOpId(int answerOpId) {
		if (answerOpId == 0) {
			//this.answerOpId = answerOpId;
			setAnswerOp(RequestAnswer.ACCEPTED);
		}
		else if (answerOpId == 1) {
			//this.answerOpId = answerOpId;
			setAnswerOp(RequestAnswer.REFUSED);
		}
		else if (answerOpId == 2) {
			//this.answerOpId = answerOpId;
			setAnswerOp(RequestAnswer.PENDING);
		}
		else {
			throw new UnsupportedOperationException("Operation with opId " + requestOpId + " is not supported");
		}
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRequestUserName() {
		return requestUserName;
	}

	public void setRequestUserName(String requestUserName) {
		this.requestUserName = requestUserName;
	}
}
