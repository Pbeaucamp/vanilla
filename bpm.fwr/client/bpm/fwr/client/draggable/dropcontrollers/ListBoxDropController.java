package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.dialogs.AddColumnToDatasetDialogBox;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class ListBoxDropController extends SimpleDropController {
	private static final String CSS_REPORT_ENGAGE = "listBoxFocus";

	private AddColumnToDatasetDialogBox dialogParent;
	private AbsolutePanel listBoxForDrag;
	
	public ListBoxDropController(AddColumnToDatasetDialogBox dialogParent, AbsolutePanel listBoxForDrag) {
		super(listBoxForDrag);
		this.dialogParent = dialogParent;
		this.listBoxForDrag = listBoxForDrag;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			dialogParent.manageWidget(widget);
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		listBoxForDrag.addStyleName(CSS_REPORT_ENGAGE);
	}
	
	@Override
	public void onLeave(DragContext context) {
		listBoxForDrag.removeStyleName(CSS_REPORT_ENGAGE);
	    super.onLeave(context);
	}
}
