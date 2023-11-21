package bpm.gwt.workflow.commons.client.popup;

import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public interface ISelectInstance {
	
	public void selectInstance(Workflow workflow, WorkflowInstance instance);
}
