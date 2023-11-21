package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.widgets.CrossTabWidget;

public class ActionTrashColumnCrosstab extends Action{

	private CrossTabWidget widgetParent;
	private DraggableHTMLPanel columnCrosstabPanel;
	private int indexInWidget;
	
	public ActionTrashColumnCrosstab(ActionType type, CrossTabWidget widgetParent, DraggableHTMLPanel columnCrosstabPanel, 
			int indexInWidget) {
		super(type);
		this.widgetParent = widgetParent;
		this.columnCrosstabPanel = columnCrosstabPanel;
		this.indexInWidget = indexInWidget;
	}

	@Override
	public void executeRedo() {
		widgetParent.removeWidget(columnCrosstabPanel, true, ActionType.REDO, true);
	}

	@Override
	public void executeUndo() {
		if(type == ActionType.TRASH_COLUMN_CELL){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.CELLS, indexInWidget, true, 
					ActionType.UNDO);
		}
		else if(type == ActionType.TRASH_COLUMN_COL){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.COLS, indexInWidget, true, 
					ActionType.UNDO);
		}
		else if(type == ActionType.TRASH_COLUMN_ROW){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.ROWS, indexInWidget, true, 
					ActionType.UNDO);
		}
	}

}
