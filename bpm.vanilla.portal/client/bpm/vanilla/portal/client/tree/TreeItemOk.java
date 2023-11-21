package bpm.vanilla.portal.client.tree;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeItemOk extends TreeItem {
	
	private CustomHTML html;
	private TypeViewer typeViewer;
	
	private TreeItemOk parent;

	public TreeItemOk(CustomHTML html, TypeViewer typeViewer) {
		super(html);
		this.html = html;
		this.typeViewer = typeViewer;
		
		this.html.addClickHandler(selectedHandler);
	}
	
	public void setSelected(boolean selected){
		if(!selected){
			this.html.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.html.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
	}
	
	public void setParent(TreeItemOk parent) {
		this.parent = parent;
	}

	public TreeItemOk getParent() {
		return parent;
	}
	
	public TypeViewer getTypeViewer() {
		return typeViewer;
	}
	
	public void addContextMenuHandler(ContextMenuHandler handler){
		this.html.addContextMenuHandler(handler);
	}
	
	public void addDoubleClickHandler(DoubleClickHandler handler){
		this.html.addDoubleClickHandler(handler);
	}
	
	public void addClickHandler(ClickHandler handler){
		this.html.addClickHandler(handler);
	}

	private ClickHandler selectedHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			setSelected(true);
		}
	};
}
