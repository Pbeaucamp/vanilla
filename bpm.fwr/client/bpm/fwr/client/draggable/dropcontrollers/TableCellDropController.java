package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.widgets.GridCell;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class TableCellDropController extends SimpleDropController {
	private static final String CSS_CELL_ENGAGE = "cellFocus";

	private GridCell cell;
	
	public TableCellDropController(GridCell cell) {
		super(cell);
		this.cell = cell;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			cell.manageWidget(widget);
		}
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		cell.addStyleName(CSS_CELL_ENGAGE);
	}
	
	@Override
	public void onLeave(DragContext context) {
		cell.removeStyleName(CSS_CELL_ENGAGE);
	    super.onLeave(context);
	}
}
