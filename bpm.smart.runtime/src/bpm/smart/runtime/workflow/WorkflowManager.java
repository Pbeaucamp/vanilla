package bpm.smart.runtime.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;

import bpm.smart.runtime.SmartManagerComponent;
import bpm.smart.runtime.SmartManagerService;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.i18n.Labels;

public class WorkflowManager {

	private ConcurrentHashMap<Integer, ConcurrentHashMap<String, WorkflowRunInstance>> runningProcess = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, WorkflowRunInstance>>();

	private SmartManagerComponent smartManagerComponent;
	
	private XStream xstream = new XStream();

	public WorkflowManager(SmartManagerComponent smartManagerComponent) {
		this.smartManagerComponent = smartManagerComponent;
	}

	public WorkflowInstance runWorkflow(Workflow workflow, List<Parameter> parameters, Locale locale, SmartManagerService smartManagerService, ResourceManager resourceManager) {

		WorkflowRunInstance run = new WorkflowRunInstance(workflow, locale, smartManagerService, false, null, resourceManager);
		WorkflowInstance info = run.startWorkflow(parameters, true);
		int workflowId = workflow.getId();
		if (runningProcess.get(workflowId) == null) {
			runningProcess.put(workflowId, new ConcurrentHashMap<String, WorkflowRunInstance>());
		}
		runningProcess.get(workflowId).put(info.getUuid(), run);

		return info;
	}
	
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName, Locale locale, SmartManagerService smartManagerService, ResourceManager resourceManager) {
		WorkflowRunInstance run = new WorkflowRunInstance(workflow, locale, smartManagerService, true, stopActivityName, resourceManager);
		return run.startWorkflow(parameters, false);
	}

	@SuppressWarnings("unchecked")
	public WorkflowInstance getWorkflowProgress(String runUuid, int workflowId) throws Exception {
		if (runningProcess.get(workflowId) != null && runningProcess.get(workflowId).get(runUuid) != null) {
			WorkflowInstance instance = runningProcess.get(workflowId).get(runUuid).getProgress();
			checkToHistorizeInstance(instance);
//			Logger.getLogger(this.getClass().getName()).warning(instance.getActivityLogs().toString());
			return instance;
		}
		else {
			try {
				List<WorkflowInstance> instances = smartManagerComponent.getSmartDao().find("From WorkflowInstance where workflowId = " + workflowId + " and uuid = '" + runUuid + "'");
				if (instances != null && !instances.isEmpty()) {
					WorkflowInstance instance = instances.get(0);
					if(instance.getModelLogs() != null) {
						try {
							instance.setActivityLogs((List<ActivityLog>) xstream.fromXML(instance.getModelLogs()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Workflow w = (Workflow) smartManagerComponent.getSmartDao().find("From Workflow where id = " + instance.getWorkflowId()).get(0);
					instance.setWorkflow(w.getId(), w.getName());
					return instance;
				}
				else {
					throw new Exception(Labels.ProcessNotFoundInRunning);
				}
			} catch (Exception e) {
				throw new Exception(Labels.ProcessNotFoundInRunning, e);
			}
		}

	}

	private boolean checkToHistorizeInstance(WorkflowInstance instance) {
		if (instance.isFinish()) {
			instance.setModelLogs(xstream.toXML(instance.getActivityLogs()));
			smartManagerComponent.getSmartDao().save(instance);
			runningProcess.get(instance.getWorkflowId()).remove(instance.getUuid());
			return true;
		}
		return false;
	}

	public void endWorkflow(String uuid, int workflowId) {
		try {
			WorkflowInstance instance = runningProcess.get(workflowId).get(uuid).getProgress();
			checkToHistorizeInstance(instance);
		} catch (Exception e) {
		}
	}

	public List<WorkflowInstance> getRunningInstances(int workflowId) {
		ConcurrentHashMap<String, WorkflowRunInstance> instances = runningProcess.get(workflowId);
		List<WorkflowInstance> runningInstances = new ArrayList<WorkflowInstance>();
		if (instances != null) {
			for (WorkflowRunInstance runInstance : instances.values()) {
				if(!checkToHistorizeInstance(runInstance.getProgress())) {
					runningInstances.add(runInstance.getProgress());
				}
			}
		}

		Collections.sort(runningInstances, new Comparator<WorkflowInstance>() {
			@Override
			public int compare(WorkflowInstance o1, WorkflowInstance o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});

		return runningInstances;
	}

}
