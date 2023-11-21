package bpm.fwr.client.action;

import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.widgets.CrossTabWidget;
import bpm.fwr.client.widgets.ListWidget;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionMoveColumnInReportWidget extends Action{

	private ReportWidget widgetParent;
	private int oldIndex;
	private int newIndex;
	
	public ActionMoveColumnInReportWidget(ActionType type, ReportWidget widgetParent, int oldIndex, int newIndex) {
		super(type);
		this.widgetParent = widgetParent;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	public void executeRedo() {
		if(type == ActionType.MOVE_COLUMN_GROUP){
			((ListWidget)widgetParent).switchWidget(ColumnType.GROUP, oldIndex, newIndex);
			widgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(this);
		}
		else if(type == ActionType.MOVE_COLUMN_DETAIL){
			((ListWidget)widgetParent).switchWidget(ColumnType.DETAIL, oldIndex, newIndex);
			widgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(this);
		}
		else if(type == ActionType.MOVE_COLUMN_CELL){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.CELLS, oldIndex, newIndex);
			widgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(this);
		}
		else if(type == ActionType.MOVE_COLUMN_COL){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.COLS, oldIndex, newIndex);
			widgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(this);
		}
		else if(type == ActionType.MOVE_COLUMN_ROW){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.ROWS, oldIndex, newIndex);
			widgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(this);
		}
	}

	@Override
	public void executeUndo() {
		if(type == ActionType.MOVE_COLUMN_GROUP){
			((ListWidget)widgetParent).switchWidget(ColumnType.GROUP, newIndex, oldIndex);
		}
		else if(type == ActionType.MOVE_COLUMN_DETAIL){
			((ListWidget)widgetParent).switchWidget(ColumnType.DETAIL, newIndex, oldIndex);
		}
		else if(type == ActionType.MOVE_COLUMN_CELL){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.CELLS, newIndex, oldIndex);
		}
		else if(type == ActionType.MOVE_COLUMN_COL){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.COLS, newIndex, oldIndex);
		}
		else if(type == ActionType.MOVE_COLUMN_ROW){
			((CrossTabWidget)widgetParent).switchWidget(ColumnType.ROWS, newIndex, oldIndex);
		}
	}
	
}
