package bpm.fwr.client.action;

import bpm.fwr.api.beans.HyperlinkColumn;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.widgets.ListWidget;

public class ActionTrashColumnList extends Action {

	private ListWidget widgetParent;
	private DraggableHTMLPanel columnListPanel;
	private int indexInWidget;

	public ActionTrashColumnList(ActionType type, ListWidget widgetParent, DraggableHTMLPanel columnListPanel, int indexInWidget) {
		super(type);
		this.widgetParent = widgetParent;
		this.columnListPanel = columnListPanel;
		this.indexInWidget = indexInWidget;
	}

	@Override
	public void executeRedo() {
		widgetParent.removeWidget(columnListPanel, true, ActionType.REDO, true);
	}

	@Override
	public void executeUndo() {
		if (type == ActionType.TRASH_COLUMN_GROUP) {
			widgetParent.manageColumns(columnListPanel.getColumn().getColumn(), ColumnType.GROUP, indexInWidget, true, ActionType.UNDO);
		}
		else if (type == ActionType.TRASH_COLUMN_DETAIL) {
			if (columnListPanel.getColumn().getColumn() instanceof HyperlinkColumn) {
				widgetParent.manageHyperlink((HyperlinkColumn) columnListPanel.getColumn().getColumn(), indexInWidget, true, false, null, ActionType.UNDO);
			}
			else {
				widgetParent.manageColumns(columnListPanel.getColumn().getColumn(), ColumnType.DETAIL, indexInWidget, true, ActionType.UNDO);
			}
		}
	}

}
