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

public class FilterDropController extends SimpleDropController {

	private MainPanel mainPanel;
	
	private HasFilter viewerParent;
	private boolean fromCubeview;
	
	public FilterDropController(MainPanel mainPanel, HasFilter viewerParent, AbsolutePanel dropTarget, boolean fromCubeView) {
		super(dropTarget);
		this.mainPanel = mainPanel;
		this.viewerParent = viewerParent;
		this.fromCubeview = fromCubeView;
	}

	@Override
	public void onDrop(DragContext context) {
		List<String> fil = new ArrayList<String>();
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

					
					
					if(fromCubeview) {
						
						fil.add(item.getUname());
					}
					else {
						viewerParent.addFilterItem(item.getUname());
					}
				}
			}
		}
		if(!fil.isEmpty()) {
			CubeServices.filter(fil, mainPanel, viewerParent);
		}
		
		mainPanel.clearDragSelection();
	}

	public HasFilter getFilter() {
		return viewerParent;
	}
	
}
