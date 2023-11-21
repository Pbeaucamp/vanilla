package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportPanel.DropTargetType;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class FilterPromptDropController extends SimpleDropController {
	private static final String CSS_FILTER_PROMPT_ENGAGE = "filterPromptEngage";
	
	private ReportPanel reportPanel;
	private AbsolutePanel dropPanel;
	private DropTargetType type;
	
	public FilterPromptDropController(ReportPanel reportPanel, AbsolutePanel dropTarget, DropTargetType type) {
		super(dropTarget);
		this.reportPanel = reportPanel;
		this.dropPanel = dropTarget;
		this.type = type;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			if(widget instanceof DraggableResourceHTML){
				reportPanel.manageWidget(((DraggableResourceHTML)widget).getResource(), type, ActionType.DEFAULT, true);
			}
			else if(widget instanceof DraggableColumn){
				reportPanel.manageWidget(((DraggableColumn)widget).getColumn(), type, ActionType.DEFAULT);
			}
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		dropPanel.addStyleName(CSS_FILTER_PROMPT_ENGAGE);
	}
	
	@Override
	public void onLeave(DragContext context) {
		dropPanel.removeStyleName(CSS_FILTER_PROMPT_ENGAGE);
	    super.onLeave(context);
	}
}
