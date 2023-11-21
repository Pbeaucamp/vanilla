package bpm.faweb.client.dnd;


import bpm.faweb.client.MainPanel;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.HTML;

public class DraggableTreeItem extends HTML {

	private String uname;
	private String name;
	private MainPanel mainPanel;
	
	public DraggableTreeItem(MainPanel mainPanel, String name, String uname) {
		super(" " + name);
		this.uname = uname;
		this.name = name;
		this.mainPanel = mainPanel;

		makeDraggable();
	}
	
	private void makeDraggable() {
		mainPanel.getDragController().makeDraggable(this);
		this.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				event.getNativeEvent().preventDefault();
				if(event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					mainPanel.hideTreePopup();
					mainPanel.showNewTreePopup(DraggableTreeItem.this.uname, event.getClientX(), event.getClientY());
				}
			}
		});
	}

	public DraggableTreeItem(String name, String uname) {
		super(name);
		this.uname = uname;
		makeDraggable();
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
	public DraggableTreeItem cloneItem() {
		DraggableTreeItem item = new DraggableTreeItem(mainPanel, name, uname);
		return item;
	}

}
