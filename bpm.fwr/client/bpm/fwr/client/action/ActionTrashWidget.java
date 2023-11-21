package bpm.fwr.client.action;

import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.widgets.ReportWidget;

public class ActionTrashWidget extends Action{

	private ReportSheet reportSheetParent;
	private ReportWidget widget;
	private int indexInReport;
	
	public ActionTrashWidget(ActionType type, ReportSheet reportSheetParent, ReportWidget widget, int indexInReport) {
		super(type);
		this.reportSheetParent = reportSheetParent;
		this.widget = widget;
		this.indexInReport = indexInReport;
	}

	@Override
	public void executeRedo() {
		reportSheetParent.deleteReportWidget(widget, indexInReport, true);
	}

	@Override
	public void executeUndo() {
		reportSheetParent.restoreReportWidget(type, widget, indexInReport, false);
	}

}
