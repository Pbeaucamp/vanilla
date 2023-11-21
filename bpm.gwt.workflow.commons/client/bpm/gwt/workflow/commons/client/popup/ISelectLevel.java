package bpm.gwt.workflow.commons.client.popup;

import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.WorkflowInstance;

public interface ISelectLevel {
	
	public void selectLevel(WorkflowInstance instance, Level level);
}
