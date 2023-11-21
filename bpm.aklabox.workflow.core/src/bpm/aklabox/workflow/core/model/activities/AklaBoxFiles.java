package bpm.aklabox.workflow.core.model.activities;

import java.io.Serializable;

public class AklaBoxFiles implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int fileId;
//	private int aklaBoxServerId;
	private String activityId;
	private int versionNumber;
	private int workflowId;
	private String type;

	public AklaBoxFiles() {

	}

	public AklaBoxFiles(int fileId, String activityId/*, int aklaBoxServerId*/, int versionNumber, int workflowId, String type) {
		super();
		this.fileId = fileId;
		this.activityId = activityId;
//		this.aklaBoxServerId = aklaBoxServerId;
		this.versionNumber = versionNumber;
		this.workflowId = workflowId;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

//	public int getAklaBoxServerId() {
//		return aklaBoxServerId;
//	}
//
//	public void setAklaBoxServerId(int aklaBoxServerId) {
//		this.aklaBoxServerId = aklaBoxServerId;
//	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
