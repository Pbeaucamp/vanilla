package bpm.fwr.client.action;

import bpm.fwr.client.widgets.GridWidget;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionTrashWidgetToGrid extends Action{

	private GridWidget gridWidget;
	private ReportWidget widget;
	private int col, row;
	
	public ActionTrashWidgetToGrid(ActionType type, GridWidget gridWidget, ReportWidget widget, int col, int row) {
		super(type);
		this.gridWidget = gridWidget;
		this.widget = widget;
		this.col = col;
		this.row = row;
	}

	@Override
	public void executeRedo() {
		gridWidget.deleteReportWidget(widget, col, row, true);
	}

	@Override
	public void executeUndo() {
		gridWidget.restoreReportWidget(type, widget, col, row, false);
	}

}
