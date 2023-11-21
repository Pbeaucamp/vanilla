package bpm.vanilla.portal.client.tree;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.vanilla.portal.client.Listeners.ItemMenu;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Event;

public class TreeView extends TreeItemOk implements DoubleClickHandler, ContextMenuHandler{
	
	private ContentDisplayPanel contentDisplayPanel;
	private PortailItemCubeView viewDto;
	
	public TreeView(ContentDisplayPanel contentDisplayPanel, CustomHTML html, PortailItemCubeView viewDto, TypeViewer typeViewer) {
		super(html, typeViewer);
		this.contentDisplayPanel = contentDisplayPanel;
		this.viewDto = viewDto;
		
		html.addContextMenuHandler(this);
		html.addDoubleClickHandler(this);
		
		this.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
	}

	public PortailItemCubeView getCubeView() {
		return viewDto;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		contentDisplayPanel.openViewer(viewDto);
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		ItemMenu itemMenu = new ItemMenu(contentDisplayPanel, viewDto, getTypeViewer());
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
	}
}
