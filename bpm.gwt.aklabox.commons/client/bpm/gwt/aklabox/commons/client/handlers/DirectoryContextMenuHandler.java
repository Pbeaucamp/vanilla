package bpm.gwt.aklabox.commons.client.handlers;

import bpm.document.management.core.model.IObject;
import bpm.gwt.aklabox.commons.client.tree.IActionManager;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class DirectoryContextMenuHandler implements ContextMenuHandler {
	
	private IActionManager actionManager;
	private IObject item;
	
	public DirectoryContextMenuHandler(IActionManager actionManager, IObject item) {
		super();
		this.actionManager = actionManager;
		this.item = item;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		DirectoryMenu contextuel = new DirectoryMenu(actionManager, item);
		if (contextuel.canShow()) {
			contextuel.setAutoHideEnabled(true);
			contextuel.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
			contextuel.show();
		}
	}
}
