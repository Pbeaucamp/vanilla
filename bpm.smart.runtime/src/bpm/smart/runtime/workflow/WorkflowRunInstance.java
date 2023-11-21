package bpm.smart.runtime.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;

import bpm.smart.runtime.SmartManagerService;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.beans.WorkflowModel;

public class WorkflowRunInstance {

	private ResourceManager resourceManager;
	private Workflow workflow;
	
	private List<Variable> currentVariables;
	private List<Parameter> currentParameters;
	
	private InstanceThread instanceThread;
	
	private WorkflowInstance workflowProgressInformation;
	
	private Locale currentLocale;

	private SmartManagerService smartManagerService;
	
	private boolean isIncomplete = false;
	private String stopActivityName;
	
	public WorkflowRunInstance(Workflow workflow, Locale locale, SmartManagerService smartManagerService, boolean incomplete, String stopActivityName, ResourceManager resourceManager) {
		this.workflow = workflow;
		this.currentLocale = locale;
		this.smartManagerService = smartManagerService;
		isIncomplete = incomplete;
		this.stopActivityName = stopActivityName;
		this.resourceManager = resourceManager;
	}
	
	/**
	 * Start a workflow. The method will create a Thread. You can get the progression by
	 * calling getProgress().
	 * @param parameters
	 * @return The progress information object
	 */
	public WorkflowInstance startWorkflow(List<Parameter> parameters, boolean async) {
		workflow.setWorkflowModel((WorkflowModel) new XStream().fromXML(workflow.getModel()));

		String uuid = UUID.randomUUID().toString();
		
		workflowProgressInformation = new WorkflowInstance(uuid, workflow);

		fillCurrentVariables();
		
		currentParameters = parameters;
		
		if(async) {
			instanceThread = new InstanceThread(this);
			instanceThread.start();
		}
		else {
			startWorkflow(this);
		}
		
		return workflowProgressInformation;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public List<Variable> getCurrentVariables() {
		return currentVariables;
	}

	public List<Parameter> getCurrentParameters() {
		return currentParameters;
	}
	
	public WorkflowInstance getProgress() {
		return workflowProgressInformation;
	}
	
	public Locale getCurrentLocale() {
		return currentLocale;
	}
	
	private void fillCurrentVariables() {
		currentVariables = new ArrayList<Variable>();
		for(Activity act : workflow.getWorkflowModel().getActivities()) {
			
			List<Variable> variables = act.getVariables(null);
			addNonExistingVariables(variables);
			
		}
	}

	private void addNonExistingVariables(List<Variable> variables) {
		if(variables != null) {
			LOOK:for(Variable var : variables) {
				for(Variable currentVar : currentVariables) {
					if(var.getId() == currentVar.getId()) {
						continue LOOK;
					}
				}
				currentVariables.add(var);
			}
		}
		
	}

	private class InstanceThread extends Thread {
		
		private WorkflowRunInstance instance;
		
		public InstanceThread(WorkflowRunInstance instance) {
			this.instance = instance;
		}
		
		@Override
		public void run() {
			startWorkflow(instance);
		}
	}
	
	private void startWorkflow(WorkflowRunInstance instance) {
		try {
			
			workflowProgressInformation.setResult(Result.RUNNING);
			
			ActivityRunnerFactory.getActiviyRunner(workflow.orderActivities(), instance, resourceManager).startActivity();
			
			workflowProgressInformation.setEndDate(new Date());
			if(workflowProgressInformation.getResult() == Result.RUNNING) {
				workflowProgressInformation.setResult(Result.SUCCESS);
			}
			
			smartManagerService.getComponent().getWorkflowManager().endWorkflow(workflowProgressInformation.getUuid(), workflow.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			workflowProgressInformation.setResult(Result.ERROR);
			smartManagerService.getComponent().getWorkflowManager().endWorkflow(workflowProgressInformation.getUuid(), workflow.getId());
		}
	}

	public SmartManagerService getManager() {
		return smartManagerService;
	}

	public boolean isIncomplete() {
		return isIncomplete;
	}

	public String getStopActivityName() {
		return stopActivityName;
	}
}
