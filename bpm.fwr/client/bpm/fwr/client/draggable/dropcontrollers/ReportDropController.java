package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.panels.ReportSheet;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class ReportDropController extends SimpleDropController {
	private static final String CSS_REPORT_ENGAGE = "reportFocus";
	private static final String CSS_REPORT_DESENGAGE = "reportLeave";

	private ReportSheet reportSheet;
	
	public ReportDropController(ReportSheet reportSheet) {
		super(reportSheet);
		this.reportSheet = reportSheet;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			reportSheet.manageWidget(widget);
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		reportSheet.setStyleName(CSS_REPORT_ENGAGE);
	}
	
	@Override
	public void onLeave(DragContext context) {
		reportSheet.setStyleName(CSS_REPORT_DESENGAGE);
	    super.onLeave(context);
	}
}
