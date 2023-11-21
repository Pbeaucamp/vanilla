package bpm.faweb.client.projection.panel.impl;

import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ConditionDropController extends SimpleDropController {

	private ListBox dropTarget;
	
	
	public ConditionDropController(ListBox dropTarget) {
		super(dropTarget);
		this.dropTarget = dropTarget;
	}
	
	@Override
	public void onDrop(DragContext context) {

		for(Widget w : context.selectedWidgets) {
			if(w instanceof DraggableTreeItem) {
				DraggableTreeItem item = (DraggableTreeItem)w;
				
				String[] part = item.getUname().split("\\]\\.\\[");
				String hieraName = part[0];
				
				boolean exists = false;
				for(int i = 0 ; i < dropTarget.getItemCount() ; i++) {
					String uname = dropTarget.getItemText(i);
					if(uname.startsWith(hieraName)) {
						String[] unamePart = uname.split("\\]\\.\\[");
						if(unamePart.length > part.length) {
							if(!uname.startsWith(item.getUname())) {
								exists = true;
								break;
							}
						}
						else if(unamePart.length < part.length) {
							if(!item.getUname().startsWith(uname)) {
								exists = true;
								break;
							}
						}
						else {
							exists = true;
							break;
						}
					}
				}
				
				if(!exists) {
					dropTarget.addItem(item.getUname(),item.getUname());
				}
				
				else {
					Window.alert("This condition will be impossible to satisfy if this member is added in.");
				}
				
			}
		}
		
	}

	
}
