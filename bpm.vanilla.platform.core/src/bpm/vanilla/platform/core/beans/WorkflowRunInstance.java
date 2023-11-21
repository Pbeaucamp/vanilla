package bpm.vanilla.platform.core.beans;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;

/**
 * Class to hold a Workflow run instance
 * @author ludo
 *
 */
public class WorkflowRunInstance {
	private IObjectIdentifier workflowId;
	private String processInstanceUuid;
	private List<IObjectIdentifier> reportsIdentifier = new ArrayList<IObjectIdentifier>();
	private WorkflowInstanceState state;
		
		
	
	/**
	 * @return the state
	 */
	public WorkflowInstanceState getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(WorkflowInstanceState state) {
		this.state = state;
	}


	public void addReportIdentifier(IObjectIdentifier id){
		reportsIdentifier.add(id);
	}
	
	
	/**
	 * @return the reportsIdentifier
	 */
	public List<IObjectIdentifier> getReportsIdentifier() {
		return reportsIdentifier;
	}


	/**
	 * @return the workflowId
	 */
	public IObjectIdentifier getWorkflowId() {
		return workflowId;
	}
	/**
	 * @param workflowId the workflowId to set
	 */
	public void setWorkflowId(IObjectIdentifier workflowId) {
		this.workflowId = workflowId;
	}
	/**
	 * @return the processInstanceUuid
	 */
	public String getProcessInstanceUuid() {
		return processInstanceUuid;
	}
	/**
	 * @param processInstanceUuid the processInstanceUuid to set
	 */
	public void setProcessInstanceUuid(String processInstanceUuid) {
		this.processInstanceUuid = processInstanceUuid;
	}
	
	
}
