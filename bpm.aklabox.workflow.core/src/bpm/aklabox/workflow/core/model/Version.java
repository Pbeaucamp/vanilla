package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.Date;

public class Version implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int workflowId;
	private int versionNumber;
	private String comment;
	private Date versionDate = new Date();

	public Version(int workflowId, int versionNumber, String comment) {
		super();
		this.workflowId = workflowId;
		this.versionNumber = versionNumber;
		this.comment = comment;
	}

	public Version() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
}
