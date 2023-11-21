package bpm.faweb.client.dnd;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.panels.center.HasFilter;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.utils.FaWebFilterHTML;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class RowColDropController extends SimpleDropController {

	private MainPanel mainPanel;
	
	private HasFilter viewerParent;
	private boolean fromCubeview;

	private AbsolutePanel dropTarget;

	private boolean isCol;
	
	public RowColDropController(MainPanel mainPanel, HasFilter viewerParent, AbsolutePanel dropTarget, boolean fromCubeView, boolean isCol) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.mainPanel = mainPanel;
		this.viewerParent = viewerParent;
		this.fromCubeview = fromCubeView;
		this.isCol = isCol;
	}

	@Override
	public void onDrop(DragContext context) {
		for(Widget w : context.selectedWidgets) {
			if(w instanceof DraggableTreeItem) {
				DraggableTreeItem item = (DraggableTreeItem)w;
				
				boolean exist = false;
				for(FaWebFilterHTML filterHtml : viewerParent.getFilters()) {
					String filter = filterHtml.getFilter();
					if(filter.equals(item.getUname())) {
						exist = true;
						break;
					}
				}
				
				if(!exist && !viewerParent.charging()) {
//					viewerParent.addFilterItem(item.getUname());

					List<String> fil = new ArrayList<String>();
					fil.add(item.getUname());
					if(fromCubeview) {
						
						CubeServices.add(fil, isCol, mainPanel);
//						CubeServices.filter(fil, mainPanel, viewerParent);
					}
					else {
						viewerParent.addRowCol(dropTarget, item.getUname());
					}
				}
			}
		}
		
		mainPanel.clearDragSelection();
	}

	public HasFilter getRowCol() {
		return viewerParent;
	}
	
}
