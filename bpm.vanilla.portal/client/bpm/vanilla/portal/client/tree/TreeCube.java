package bpm.vanilla.portal.client.tree;

import java.util.List;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.vanilla.portal.client.Listeners.ItemMenu;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Image;

public class TreeCube extends TreeItemOk implements ContextMenuHandler, DoubleClickHandler{

	private ContentDisplayPanel contentDisplayPanel;
	private PortailItemCube cube;
	
	public TreeCube(ContentDisplayPanel contentDisplayPanel, CustomHTML html, PortailItemCube cube, TypeViewer typeViewer) {
		super(html, typeViewer);
		this.contentDisplayPanel = contentDisplayPanel;
		this.cube = cube;
		
		html.addContextMenuHandler(this);
		html.addDoubleClickHandler(this);
	}
	
	public PortailItemCube getCube() {
		return cube;
	}

	public void addViews(List<PortailItemCubeView> views) {
		for (PortailItemCubeView nameView : views) {
			CustomHTML html = new CustomHTML(new Image(PortalImage.INSTANCE.view()) + " " + nameView.getName());
			
			TreeView treeView = new TreeView(contentDisplayPanel, html, nameView, getTypeViewer());
			treeView.setParent(this);
			this.addItem(treeView);
		}
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		ItemMenu itemMenu = new ItemMenu(contentDisplayPanel, cube, getTypeViewer());
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		contentDisplayPanel.openViewer(cube);
	}
}
