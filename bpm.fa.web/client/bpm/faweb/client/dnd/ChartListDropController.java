package bpm.faweb.client.dnd;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.panels.center.chart.ChartPanel;
import bpm.faweb.client.panels.center.chart.ChartPanel.ListType;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartListDropController extends SimpleDropController {

	private MainPanel mainPanel;

	private ListType type;
	private ChartPanel tabParent;

	public ChartListDropController(MainPanel mainPanel, AbsolutePanel dropTarget, ChartPanel tabParent, ListType type) {
		super(dropTarget);
		this.mainPanel = mainPanel;
		this.tabParent = tabParent;
		this.type = type;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget w : context.selectedWidgets) {
			if (w instanceof DraggableTreeItem) {
				tabParent.manageWidget((DraggableTreeItem) w, type);
			}
		}

		mainPanel.clearDragSelection();
	}

}
