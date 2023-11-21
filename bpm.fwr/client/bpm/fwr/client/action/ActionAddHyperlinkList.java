package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.widgets.ListWidget;

public class ActionAddHyperlinkList extends Action{

	private ListWidget widgetParent;
	private DraggableHTMLPanel columnListPanel;
	private int indexInWidget;
	
	public ActionAddHyperlinkList(ActionType type, ListWidget widgetParent, DraggableHTMLPanel columnListPanel, int indexInWidget) {
		super(type);
		this.widgetParent = widgetParent;
		this.columnListPanel = columnListPanel;
		this.indexInWidget = indexInWidget;
	}

	@Override
	public void executeRedo() {
		if(type == ActionType.ADD_COLUMN_GROUP){
			widgetParent.manageColumns(columnListPanel.getColumn().getColumn(), ColumnType.GROUP, indexInWidget, true, 
					ActionType.REDO);
		}
		else if(type == ActionType.ADD_COLUMN_DETAIL){
			widgetParent.manageColumns(columnListPanel.getColumn().getColumn(), ColumnType.DETAIL, indexInWidget, true, 
					ActionType.REDO);
		}
	}

	@Override
	public void executeUndo() {
		widgetParent.removeWidget(columnListPanel, true, ActionType.UNDO, true);
	}
	
}
