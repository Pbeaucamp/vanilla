package bpm.vanillahub.runtime;

import bpm.vanillahub.runtime.run.ResultActivity;
import bpm.vanillahub.runtime.run.RunManager;
import bpm.workflow.commons.beans.WorkflowInstance;


public class WorkflowProgress {

	private RunManager runManager;
	private ResultActivity progress;
	
	private WorkflowInstance instance;

	private int workflowNumber;
	private int totalWorkflow;

	public WorkflowProgress() {
	}

	public void setRunManager(RunManager runManager) {
		this.runManager = runManager;
	}

	public void stopWorkflow() {
		if (runManager != null) {
			runManager.stopWorkflow();
		}
	}

	public void updateProgress(ResultActivity progress) {
		this.progress = progress;
	}

	public ResultActivity getProgress() {
		return progress;
	}
	
	public void setInstance(WorkflowInstance instance) {
		this.instance = instance;
	}
	
	public WorkflowInstance getInstance() {
		return instance;
	}
	
	public void setWorkflowNumber(int workflowNumber) {
		this.workflowNumber = workflowNumber;
	}
	
	public int getWorkflowNumber() {
		return workflowNumber;
	}
	
	public void setTotalWorkflow(int totalWorkflow) {
		this.totalWorkflow = totalWorkflow;
	}
	
	public int getTotalWorkflow() {
		return totalWorkflow;
	}

	public boolean isStopByUser() {
		return runManager != null ? runManager.isStopByUser() : false;
	}
}
