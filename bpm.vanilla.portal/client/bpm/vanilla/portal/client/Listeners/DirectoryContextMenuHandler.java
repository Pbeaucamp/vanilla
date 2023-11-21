package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class DirectoryContextMenuHandler implements ContextMenuHandler {
	
	private ContentDisplayPanel mainPanel;
	private PortailRepositoryDirectory dir;
	
	public DirectoryContextMenuHandler(ContentDisplayPanel mainPanel, PortailRepositoryDirectory dir) {
		super();
		this.mainPanel = mainPanel;
		this.dir = dir;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		DirectoryMenu contextuel = new DirectoryMenu(mainPanel, dir);
		contextuel.setAutoHideEnabled(true);
		contextuel.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		contextuel.show();
	}
}
