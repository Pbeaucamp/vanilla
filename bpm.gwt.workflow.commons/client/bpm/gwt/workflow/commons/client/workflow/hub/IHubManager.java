package bpm.gwt.workflow.commons.client.workflow.hub;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.workflow.commons.beans.Workflow;

public interface IHubManager {

	public boolean canEditWorkflow();
	
	public void displayWorkflow(Workflow workflow);

	public IResourceManager getResourceManager();
}
