package bpm.vanilla.portal.client.Listeners;

import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.tree.TreeDirectoryItem;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class DirectoryItemContextMenuHandler implements ContextMenuHandler {
	
	private ContentDisplayPanel contentDisplayPanel;
	private TreeDirectoryItem treeDir;
	private TypeViewer typeViewer;
	
	public DirectoryItemContextMenuHandler(ContentDisplayPanel contentDisplayPanel, TreeDirectoryItem treeDir, TypeViewer typeViewer) {
		super();
		this.contentDisplayPanel = contentDisplayPanel;
		this.treeDir = treeDir;
		this.typeViewer = typeViewer;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		ItemMenu itemMenu = new ItemMenu(contentDisplayPanel, treeDir.getItem(), typeViewer);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
	}
}
