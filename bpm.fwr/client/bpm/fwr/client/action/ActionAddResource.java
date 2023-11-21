package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportPanel.DropTargetType;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionAddResource extends Action{
	
	private ReportPanel panelParent;
	private DraggableResourceHTML resource;
	private ReportWidget widgetParent;

	public ActionAddResource(ActionType type, ReportPanel panelParent, DraggableResourceHTML resource, 
			ReportWidget widgetParent) {
		super(type);
		this.panelParent = panelParent;
		this.resource = resource;
		this.widgetParent = widgetParent;
	}

	@Override
	public void executeRedo() {
		if(type == ActionType.ADD_FILTER){
			panelParent.manageWidget(resource.getResource(), DropTargetType.FILTER, ActionType.REDO, false);
		}
		else if(type == ActionType.ADD_PROMPT){
			panelParent.manageWidget(resource.getResource(), DropTargetType.PROMPT, ActionType.REDO, false);
		}
		else if(type == ActionType.ADD_RELATION) {
			panelParent.manageWidget(resource.getResource(), DropTargetType.RELATION, ActionType.REDO, false);
		}
	}

	@Override
	public void executeUndo() {
		panelParent.removeWidget(widgetParent, resource, true, ActionType.UNDO, true);
	}

}
