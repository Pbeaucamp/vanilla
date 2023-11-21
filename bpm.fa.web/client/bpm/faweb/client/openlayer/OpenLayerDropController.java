package bpm.faweb.client.openlayer;

import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.openlayer.OpenLayerMapContainer.ListType;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenLayerDropController extends SimpleDropController {

	private ListType type;
	private OpenLayerMapContainer tabParent;
	
	public OpenLayerDropController(AbsolutePanel dropTarget, OpenLayerMapContainer tabParent, ListType type) {
		super(dropTarget);
		this.tabParent = tabParent;
		this.type = type;
	}

	@Override
	public void onDrop(DragContext context) {
		for(Widget w : context.selectedWidgets) {
			if(w instanceof DraggableTreeItem) {
				tabParent.manageWidget((DraggableTreeItem)w, type);
			}
		}
		
	}

}
