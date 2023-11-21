package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileType implements Serializable {

	private static final long serialVersionUID = 1L;

	private int typeId;
	private String label;

	private User defaultUser;

	private List<Form> forms;
	private List<Reply> replies;
	private int delay = 0;
	private int workflowId = 0;

	private long administrativeUseDuration;

	private Integer serviceRecipientId;
	private OrganigramElement serviceRecipient;
	
	private String appraisalRule;
	private String accessRule;
	private Date appraisalDate;
	private Date accessDate;
	private String appraisalAction;
	
	private boolean deleted;
	
	

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (typeId == ((FileType) obj).getTypeId()) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {

		if (forms == null) {
			forms = new ArrayList<Form>();
		}

		this.forms = new ArrayList<Form>(forms);
	}

	public List<Reply> getReplies() {
		return replies != null ? replies : new ArrayList<Reply>();
	}

	public void setReplies(List<Reply> replies) {
		if (replies == null) {
			replies = new ArrayList<Reply>();
		}

		this.replies = new ArrayList<Reply>(replies);
	}

	public void addReply(Reply reply) {
		if (replies == null) {
			replies = new ArrayList<Reply>();
		}
		this.replies.add(reply);
	}

	public User getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(User defaultUser) {
		this.defaultUser = defaultUser;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int idWorkflow) {
		this.workflowId = idWorkflow;
	}

	public long getAdministrativeUseDuration() {
		return administrativeUseDuration;
	}

	public void setAdministrativeUseDuration(long administrativeUseDuration) {
		this.administrativeUseDuration = administrativeUseDuration;
	}

	public Integer getServiceRecipientId() {
		if (serviceRecipientId == null) {
			serviceRecipientId = 0;
		}
		return serviceRecipient != null ? serviceRecipient.getId() : serviceRecipientId;
	}

	public void setServiceRecipientId(Integer serviceRecipientId) {
		this.serviceRecipientId = serviceRecipientId;
	}

	public OrganigramElement getServiceRecipient() {
		return serviceRecipient;
	}

	public void setServiceRecipient(OrganigramElement serviceRecipient) {
		this.serviceRecipient = serviceRecipient;
	}
	
	@Override
	public String toString() {
		return label;
	}

	public String getAppraisalRule() {
		return appraisalRule;
	}

	public void setAppraisalRule(String appraisalRule) {
		this.appraisalRule = appraisalRule;
	}

	public String getAccessRule() {
		return accessRule;
	}

	public void setAccessRule(String accessRule) {
		this.accessRule = accessRule;
	}

	public Date getAppraisalDate() {
		return appraisalDate;
	}

	public void setAppraisalDate(Date appraisalDate) {
		this.appraisalDate = appraisalDate;
	}

	public Date getAccessDate() {
		return accessDate;
	}

	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}

	public String getAppraisalAction() {
		return appraisalAction;
	}

	public void setAppraisalAction(String appraisalAction) {
		this.appraisalAction = appraisalAction;
	}

	
}
