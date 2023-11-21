package bpm.gwt.workflow.commons.client.actions;

import java.util.List;

import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.actions.ConnectionManager.WorkspaceLink;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.CustomController;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.workflow.commons.beans.TypeActivity;

public class BoxAction extends Action {

	private WorkspacePanel creationPanel;
	private CustomController customController;

	private BoxItem item;
	private int positionLeft;
	private int positionTop;

	private TypeAction type;

	private List<WorkspaceLink> links;

	public BoxAction(IWorkflowAppManager appManager, WorkspacePanel creationPanel, TypeActivity typeTool, int positionLeft, int positionTop) {
		super(appManager.getLabel(typeTool));
		this.creationPanel = creationPanel;
		this.item = new BoxItem(appManager, creationPanel, typeTool, name);
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;
		this.type = TypeAction.ADD;
	}

	public BoxAction(IWorkflowAppManager appManager, WorkspacePanel creationPanel, CustomController customController, BoxItem item, int positionLeft, int positionTop) {
		super(item.getName());
		this.creationPanel = creationPanel;
		this.customController = customController;
		this.item = item;
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;
		this.type = TypeAction.REMOVE;
	}

	@Override
	public void doAction() {
		if (type == TypeAction.ADD) {
			creationPanel.addBox(item, positionLeft, positionTop);
		}
		else if (type == TypeAction.REMOVE) {
			creationPanel.removeBox(item, false);
			links = customController.removeConnections(item);
		}
	}

	@Override
	public void undoAction() {
		if (type == TypeAction.ADD) {
			creationPanel.removeBox(item, false);
		}
		else if (type == TypeAction.REMOVE) {
			creationPanel.addBox(item, positionLeft, positionTop);
			customController.restoreConnections(links);
		}
	}
}
