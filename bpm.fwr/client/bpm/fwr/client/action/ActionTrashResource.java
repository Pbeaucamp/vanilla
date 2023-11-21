package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportPanel.DropTargetType;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionTrashResource extends Action{
	
	private ReportPanel panelParent;
	private DraggableResourceHTML resource;
	private ReportWidget widget;

	public ActionTrashResource(ActionType type, ReportPanel panelParent, DraggableResourceHTML resource, ReportWidget widget) {
		super(type);
		this.panelParent = panelParent;
		this.resource = resource;
		this.widget = widget;
	}

	@Override
	public void executeRedo() {
		panelParent.removeWidget(widget, resource, true, ActionType.REDO, true);
	}

	@Override
	public void executeUndo() {
		if(type == ActionType.TRASH_FILTER){
			panelParent.manageWidget(resource.getResource(), DropTargetType.FILTER, ActionType.UNDO, false);
		}
		else if(type == ActionType.TRASH_PROMPT){
			panelParent.manageWidget(resource.getResource(), DropTargetType.PROMPT, ActionType.UNDO, false);
		}
		else if(type == ActionType.TRASH_RELATION){
			panelParent.manageWidget(resource.getResource(), DropTargetType.RELATION, ActionType.UNDO, false);
		}
	}

}
