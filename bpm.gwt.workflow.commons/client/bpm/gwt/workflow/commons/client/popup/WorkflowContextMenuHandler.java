package bpm.gwt.workflow.commons.client.popup;

import bpm.gwt.workflow.commons.client.IWorkflowManager;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class WorkflowContextMenuHandler implements ContextMenuHandler {
	
	private IWorkflowManager workflowManager;
	private Workflow workflow;
	
	public WorkflowContextMenuHandler(IWorkflowManager workflowManager, Workflow workflow) {
		super();
		this.workflowManager = workflowManager;
		this.workflow = workflow;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		WorkflowMenu itemMenu = new WorkflowMenu(workflowManager, workflow);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
	}
}
