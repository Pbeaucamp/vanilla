package bpm.gwt.workflow.commons.client;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.Workflow;

public interface IWorkflowManager {
	
	public boolean canRunWorkflow();

	public void runWorkflow(Workflow item);

	public void runWorkflow(Workflow workflow, List<Parameter> parameters);
	
	public boolean canShowHistoricsWorkflow();

	public void showHistoricsWorkflow(Workflow item);

	public boolean canScheduleWorkflow();

	public void scheduleWorkflow(Workflow item);

	public boolean canUpdateWorkflow();

	public void updateWorkflow(Workflow workflow);

	public boolean canDeleteWorkflow();

	public void deleteWorkflow(Workflow workflow);

	public void duplicateWorkflow(Workflow workflow, String name);
}
