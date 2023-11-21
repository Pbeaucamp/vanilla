package bpm.smart.web.client.panels.properties.handler;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class RefreshWorkflowHandler implements ClickHandler {

	private WorkspacePanel creationPanel;
	private Activity stopActivityName;

	public RefreshWorkflowHandler(WorkspacePanel panel, Activity stopActivityName) {
		this.creationPanel = panel;
		this.stopActivityName = stopActivityName;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Workflow w = creationPanel.buildWorkflow(false);
		WorkflowsService.Connect.getInstance().runIncompleteWorkflow(w, null, stopActivityName.getName(), new GwtCallbackWrapper<WorkflowInstance>(null, false) {

			@Override
			public void onSuccess(WorkflowInstance result) {
				MessageHelper.openMessageDialog("Success", "Workflow refreshed with success");
			}
		}.getAsyncCallback());
		
	}

}
