package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.widgets.CrossTabWidget;

public class ActionAddColumnCrosstab extends Action{

	private CrossTabWidget widgetParent;
	private DraggableHTMLPanel columnCrosstabPanel;
	private int indexInWidget;
	
	public ActionAddColumnCrosstab(ActionType type, CrossTabWidget widgetParent, DraggableHTMLPanel columnCrosstabPanel, 
			int indexInWidget) {
		super(type);
		this.widgetParent = widgetParent;
		this.columnCrosstabPanel = columnCrosstabPanel;
		this.indexInWidget = indexInWidget;
	}

	@Override
	public void executeRedo() {
		if(type == ActionType.ADD_COLUMN_CELL){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.CELLS, indexInWidget, true, 
					ActionType.REDO);
		}
		else if(type == ActionType.ADD_COLUMN_COL){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.COLS, indexInWidget, true, 
					ActionType.REDO);
		}
		else if(type == ActionType.ADD_COLUMN_ROW){
			widgetParent.manageColumns(columnCrosstabPanel.getColumn().getColumn(), ColumnType.ROWS, indexInWidget, true, 
					ActionType.REDO);
		}
	}

	@Override
	public void executeUndo() {
		widgetParent.removeWidget(columnCrosstabPanel, true, ActionType.UNDO, true);
	}

	
}
