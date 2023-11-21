package bpm.aklabox.workflow.core.model;

import java.io.Serializable;

public class Owner implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int workflowId;
	private String owner;
	private boolean monitor;
	private boolean controll;

	public Owner() {
	}

	public Owner(int workflowId, String owner, boolean monitor, boolean controll) {
		super();
		this.workflowId = workflowId;
		this.owner = owner;
		this.monitor = monitor;
		this.controll = controll;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public boolean isControll() {
		return controll;
	}

	public void setControll(boolean controll) {
		this.controll = controll;
	}

}
