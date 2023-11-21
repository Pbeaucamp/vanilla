package bpm.vanillahub.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.runtime.run.RunManager;
import bpm.vanillahub.runtime.run.WorkflowRunner;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class WorkflowManager extends AbstractManager implements IHubWorkflowManager {

	public WorkflowManager(ComponentVanillaHub component) {
		super(component);
	}

	@Override
	protected void init() throws Exception {
	}

	@Override
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws Exception {
		return getComponent().getWorkflowDao().manageWorkflow(currentWorkflow, modify);
	}

	@Override
	public void removeWorkflow(Workflow currentWorkflow) throws Exception {
		getComponent().getWorkflowDao().delete(currentWorkflow);
	}

	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws Exception {
		return getComponent().getWorkflowDao().duplicate(workflowId, name);
	}
	
	@Override
	public List<Workflow> getWorkflows() throws Exception {
		return getWorkflows(null, 0, 1000, true, null).getItems();
	}
	
	@Override
	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort dataSort) throws Exception {
		DataWithCount<Workflow> workflows = getComponent().getWorkflowDao().getWorkflows(query, firstResult, length, true, dataSort);
		for (Workflow workflow : workflows.getItems()) {
			List<WorkflowInstance> instances = getComponent().getProgressManager().getRunningInstances(workflow);
			workflow.setRunningRuns(instances);
		}
		return workflows;
	}
	
	@Override
	public Workflow getWorkflow(int workflowId, boolean lightWeight) throws Exception {
		Workflow workflow = getComponent().getWorkflowDao().getWorkflow(workflowId, lightWeight);
		if (workflow != null) {
			List<WorkflowInstance> instances = getComponent().getProgressManager().getRunningInstances(workflow);
			workflow.setRunningRuns(instances);
		}
		return workflow;
	}

	@Override
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws Exception {
		RunManager manager = new RunManager(getComponent().getLogger(), getComponent().getWorkflowDao(), getComponent().getFileManager(), getComponent().getResourceDao());
		return manager.getParameters(getLocale(), workflow);
	}

	@Override
	public String initWorkflow(Workflow workflow) throws Exception {
		String uuid = UUID.randomUUID().toString();

		WorkflowProgress workflowProgress = new WorkflowProgress();
		getComponent().getProgressManager().addWorkflow(workflow.getId(), uuid, workflowProgress);

		return uuid;
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws Exception {
		List<ListOfValues> lovs = getComponent().getResourceDao().getListOfValues();

		new WorkflowRunner(getComponent().getLogger(), getComponent().getWorkflowDao(), getComponent().getFileManager(), getComponent().getResourceDao(), workflow, 
				getComponent().getProgressManager(), uuid, getLocale(), launcher.getName(), parameters, lovs, launcher.getId()).start();
		return null;
	}

	@Override
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws Exception {
		List<WorkflowInstance> instances = getComponent().getWorkflowDao().getInstances(workflow.getId(), false);
		return instances != null ? instances : new ArrayList<WorkflowInstance>();
	}

	@Override
	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception {
		return getComponent().getWorkflowDao().getInstanceLogs(instance.getId());
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws Exception {
		return getComponent().getProgressManager().getRunningWorkflow(workflow, uuid);
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws Exception {
		getComponent().getProgressManager().stopWorkflow(workflowId, uuid);
	}

	@Override
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<WorkflowInstance> getWorkflowRunningInstances(int workflowId) throws Exception {
		throw new Exception("Not implemented");
	}
}
