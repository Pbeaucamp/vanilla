package bpm.faweb.client.dnd;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.InfosReport;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class GridItemDropController extends SimpleDropController {

	private MainPanel mainPanel;

	private int i;
	private int j;

	public GridItemDropController(MainPanel mainPanel, Widget dropTarget, int i, int j) {
		super(dropTarget);
		this.mainPanel = mainPanel;

		this.i = i;
		this.j = j;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget w : context.selectedWidgets) {
			if (w instanceof DraggableGridItem) {
				DraggableGridItem item = (DraggableGridItem) w;
				mainPanel.move(item.getI(), item.getJ(), i, j, true);
			}
			else if (w instanceof DraggableTreeItem) {
				DraggableTreeItem item = (DraggableTreeItem) w;
				mainPanel.showWaitPart(true);
				FaWebService.Connect.getInstance().addService(mainPanel.getKeySession(), item.getUname(), i, j, true, new AsyncCallback<InfosReport>() {
					public void onFailure(Throwable caught) {

					}

					public void onSuccess(InfosReport result) {
						mainPanel.setGridFromRCP(result);
						mainPanel.getNavigationPanel().clearItemSelected();
						mainPanel.showWaitPart(false);
					}
				});
			}
		}

		mainPanel.clearDragSelection();
	}
}
