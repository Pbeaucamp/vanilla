package bpm.fwr.client.action;

import bpm.fwr.client.panels.ReportSheet;

public class ActionMoveReportWidget extends Action{

	private ReportSheet reportSheet;
	private int oldIndex;
	private int newIndex;
	
	public ActionMoveReportWidget(ActionType type, ReportSheet reportSheet, int oldIndex, int newIndex) {
		super(type);
		this.reportSheet = reportSheet;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	public void executeRedo() {
		reportSheet.switchWidget(oldIndex, newIndex);
		reportSheet.getPanelParent().replaceLastActionToUndo(this);
	}

	@Override
	public void executeUndo() {
		reportSheet.switchWidget(newIndex, oldIndex);
	}
	
}
