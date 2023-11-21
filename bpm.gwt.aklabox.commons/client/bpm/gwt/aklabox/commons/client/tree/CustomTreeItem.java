package bpm.gwt.aklabox.commons.client.tree;

import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CustomTreeItem extends TreeItem {

	private TreeItemPanel widget;

	public CustomTreeItem() {
		super();
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget instanceof HasTreeItem) {
			this.widget = ((HasTreeItem) widget).getItemPanel();
			this.widget.addClickHandler(selectedHandler);
		}
		else if (widget instanceof TreeItemPanel) {
			this.widget = (TreeItemPanel) widget;
			this.widget.addClickHandler(selectedHandler);
		}

		super.setWidget(widget);
	}

	@Override
	public void setSelected(boolean selected) {
		if (widget != null)
			widget.setSelected(selected);
	}
	
	public void addContextMenuHandler(ContextMenuHandler handler) {
		if (widget != null)
			widget.addContextMenuHandler(handler);
	}

	public void addDoubleClickHandler(DoubleClickHandler handler) {
		if (widget != null)
			widget.addDoubleClickHandler(handler);
	}

	
	public void addClickHandler(ClickHandler handler) {
		if (widget != null)
			widget.addClickHandler(handler);
	}

	private ClickHandler selectedHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			setSelected(true);
		}
	};
}
