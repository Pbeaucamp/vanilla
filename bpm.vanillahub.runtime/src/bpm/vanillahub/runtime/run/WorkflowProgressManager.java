package bpm.vanillahub.runtime.run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.utils.Utils;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class WorkflowProgressManager {

	private HashMap<Integer, HashMap<String, WorkflowProgress>> progressMap;

	public WorkflowProgressManager() {
		this.progressMap = new HashMap<>();
	}

	private synchronized HashMap<Integer, HashMap<String, WorkflowProgress>> getProgressMap() {
		return progressMap;
	}

	public void addWorkflow(int workflowId, String uuid, WorkflowProgress workflowProgress) {
		if (getProgressMap().get(workflowId) == null) {
			getProgressMap().put(workflowId, new HashMap<String, WorkflowProgress>());
		}
		getProgressMap().get(workflowId).put(uuid, workflowProgress);
	}

	public void stopWorkflow(int workflowId, String uuid) {
		if (getProgressMap().get(workflowId) != null && getProgressMap().get(workflowId).get(uuid) != null) {
			getProgressMap().get(workflowId).get(uuid).stopWorkflow();
		}
	}

	public List<WorkflowInstance> getRunningInstances(Workflow workflow) {
		List<WorkflowInstance> instances = new ArrayList<>();
		if (getProgressMap().get(workflow.getId()) != null) {
			HashMap<String, WorkflowProgress> progresses = getProgressMap().get(workflow.getId());
			if (instances != null) {
				for (String uuid : progresses.keySet()) {
					WorkflowProgress progress = progresses.get(uuid);
					if (progress != null && (progress.getProgress() == null || progress.getProgress().getResult() == Result.RUNNING)) {
						instances.add(buildInstance(workflow, uuid, progress));
					}
				}
			}
		}

		return instances;
	}

	public WorkflowProgress getRunningProgress(int workflowId, String uuid) {
		return getProgressMap().get(workflowId).get(uuid);
	}

	public WorkflowInstance getRunningWorkflow(Workflow workflow, String uuid) {
		WorkflowProgress progress = getProgressMap().get(workflow.getId()).get(uuid);
		return buildInstance(workflow, uuid, progress);
	}

	private WorkflowInstance buildInstance(Workflow workflow, String uuid, WorkflowProgress progress) {
		if (progress != null) {
			WorkflowInstance instance = progress.getInstance();
			if (instance == null) {
				instance = new WorkflowInstance(uuid, workflow);
			}

			if (progress.getProgress() != null) {
				List<ActivityLog> logs = Utils.getLogsAsList(progress.getProgress().getLogs());
				instance.setActivityLogs(logs);
			}
			instance.setTotalWorkflow(progress.getTotalWorkflow());
			instance.setWorkflowNumber(progress.getWorkflowNumber());
			return instance;
		}
		else {
			return null;
		}
	}
}
