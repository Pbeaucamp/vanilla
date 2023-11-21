package bpm.fwr.client.action;

import bpm.fwr.client.widgets.GridWidget;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionAddWidgetToGrid extends Action{

	private GridWidget gridWidget;
	private ReportWidget widget;
	private int col, row;
	
	public ActionAddWidgetToGrid(ActionType type, GridWidget gridWidget, ReportWidget widget, int col, int row) {
		super(type);
		this.gridWidget = gridWidget;
		this.widget = widget;
		this.col = col;
		this.row = row;
	}
	
	@Override
	public void executeRedo(){
		gridWidget.restoreReportWidget(type, widget, col, row, true);
	}

	@Override
	public void executeUndo() {
		gridWidget.deleteReportWidget(widget, col, row, false);
	}

}
