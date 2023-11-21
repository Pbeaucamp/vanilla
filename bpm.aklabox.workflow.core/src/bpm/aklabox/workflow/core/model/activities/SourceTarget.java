package bpm.aklabox.workflow.core.model.activities;

import java.io.Serializable;
import java.util.Date;

public class SourceTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String sourceId = "";
	private String targetId = "";
	private int workflowId;
	private long versionNumber = new Date().getTime();

	public SourceTarget() {
		super();
	}

	@Override
	public String toString() {
		return "SourceTarget [id=" + id + ", sourceId=" + sourceId + ", targetId=" + targetId + "]";
	}

	public SourceTarget(String sourceId, String targetId, int workflowId) {
		super();
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.workflowId = workflowId;
	}

	public SourceTarget(SourceTarget sourceTarget) {
		super();
		this.sourceId = sourceTarget.getSourceId();
		this.targetId = sourceTarget.getTargetId();
		this.workflowId = sourceTarget.getWorkflowId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public long getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(long versionNumber) {
		this.versionNumber = versionNumber;
	}
}
