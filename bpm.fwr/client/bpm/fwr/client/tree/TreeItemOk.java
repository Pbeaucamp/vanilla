package bpm.fwr.client.tree;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeItemOk extends TreeItem {

	public enum TypeItem {
		FOLDER,
		ITEM
	}
	
	private int itemId;
	private CustomHTML html;
	
	private TypeItem typeItem;

	public TreeItemOk(CustomHTML html, TypeItem typeItem, int itemId) {
		super(html);
		this.html = html;
		this.typeItem = typeItem;
		this.itemId = itemId;
		
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

	public TypeItem getTypeItem() {
		return typeItem;
	}

	public int getItemId() {
		return itemId;
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
