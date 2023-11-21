package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.IObject;
import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel;
import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel.ItemSize;

import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusPanel;

public class DocHTML extends FocusPanel implements HasContextMenuHandlers, HasDoubleClickHandlers, HasTreeItem {

	private TreeItemPanel treeItemPanel;
	private IObject element;

	public DocHTML(ImageResource resource, String label, IObject element, ItemSize itemSize) {
		this.element = element;
		this.treeItemPanel = new TreeItemPanel(resource, label, itemSize);
		
		setWidget(treeItemPanel);
	}

	public DocHTML(String imgUrl, String label, IObject element, ItemSize itemSize) {
		this.element = element;
		this.treeItemPanel = new TreeItemPanel(imgUrl, label, itemSize);
		
		setWidget(treeItemPanel);
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return treeItemPanel.addContextMenuHandler(handler);
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return treeItemPanel.addDoubleClickHandler(handler);
	}

	public IObject getIObject() {
		return element;
	}

	@Override
	public TreeItemPanel getItemPanel() {
		return treeItemPanel;
	}
}