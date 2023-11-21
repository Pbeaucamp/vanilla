package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.Date;

public class WorkflowLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String activityId;
	private int owner;
	private String message;
	private String type;
	private Date date;
	private Workflow workflow;

	public WorkflowLog() {
	}

	public WorkflowLog(String message, String type, Date date) {
		super();
		this.message = message;
		this.type = type;
		this.date = date;
	}
	
	public WorkflowLog(String activityId, int owner, String message, String type, Date date) {
		super();
		this.activityId = activityId;
		this.owner = owner;
		this.message = message;
		this.type = type;
		this.date = date;
	}

	public WorkflowLog(String activityId, int owner, String message, String type, Date date, Workflow workflow) {
		super();
		this.activityId = activityId;
		this.owner = owner;
		this.message = message;
		this.type = type;
		this.date = date;
		this.workflow = workflow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

}
