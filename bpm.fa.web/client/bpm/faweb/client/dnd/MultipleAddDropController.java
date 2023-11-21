package bpm.faweb.client.dnd;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MultipleAddDropController extends SimpleDropController {

	private MainPanel mainPanel;
	
	private ListBox dropTarget;
	
	public MultipleAddDropController(MainPanel mainPanel, ListBox dropTarget) {
		super(dropTarget);
		this.mainPanel = mainPanel;
		this.dropTarget = dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {
		for(Widget w : context.selectedWidgets) {
			if(w instanceof DraggableTreeItem) {
				DraggableTreeItem item = (DraggableTreeItem)w;
				boolean exist = false;
				for(int i = 0 ; i < dropTarget.getItemCount() ; i++) {
					if(dropTarget.getValue(i).equals(item.getUname())) {
						exist = true;
						break;
					}
				}
				if(!exist) {
					dropTarget.addItem(item.getUname(), item.getUname());
				}
			}
			
			refreshTooltip();
		}
		
		mainPanel.clearDragSelection();
	}
	
	private void refreshTooltip() {
		SelectElement selectElement = SelectElement.as(dropTarget.getElement());
		NodeList<OptionElement> options = selectElement.getOptions();
	    for (int i = 0; i < options.getLength(); i++) {
	        options.getItem(i).setTitle(dropTarget.getItemText(i));
	    }
	}
	
}
