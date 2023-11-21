package bpm.fwr.client.action;

import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionAddWidget extends Action{

	private ReportSheet reportSheetParent;
	private ReportWidget widget;
	private int indexInReport;
	
	public ActionAddWidget(ActionType type, ReportSheet reportSheetParent, ReportWidget widget, int indexInReport) {
		super(type);
		this.reportSheetParent = reportSheetParent;
		this.widget = widget;
		this.indexInReport = indexInReport;
	}
	
	@Override
	public void executeRedo(){
		reportSheetParent.restoreReportWidget(type, widget, indexInReport, true);
	}

	@Override
	public void executeUndo() {
		reportSheetParent.deleteReportWidget(widget, indexInReport, false);
	}

}
