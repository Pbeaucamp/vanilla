package bpm.workflow.commons.beans;

import java.util.List;

import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Parameter;

public interface IWorkflowManager {

	public List<Workflow> getWorkflows() throws Exception;

	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort dataSort) throws Exception;

	public Workflow getWorkflow(int workflowId, boolean lightWeight) throws Exception;
	
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws Exception;

	public void removeWorkflow(Workflow currentWorkflow) throws Exception;

	public List<Parameter> getWorkflowParameters(Workflow workflow) throws Exception;

	public String initWorkflow(Workflow workflow) throws Exception;
	
	

	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws Exception;
	
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws Exception;

	public void stopWorkflow(int workflowId, String uuid) throws Exception;
	
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws Exception;
	
	
	
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws Exception;
	
	public List<WorkflowInstance> getWorkflowRunningInstances(int workflowId) throws Exception;

	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception;

	public Workflow duplicateWorkflow(int workflowId, String name) throws Exception;
}
