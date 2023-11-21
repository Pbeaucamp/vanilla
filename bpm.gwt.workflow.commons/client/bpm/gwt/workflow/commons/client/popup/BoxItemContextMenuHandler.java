package bpm.gwt.workflow.commons.client.popup;

import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class BoxItemContextMenuHandler implements ContextMenuHandler {
	
	private WorkspacePanel creationPanel;
	private BoxItem boxItem;
	
	public BoxItemContextMenuHandler(WorkspacePanel creationPanel, BoxItem boxItem) {
		super();
		this.creationPanel = creationPanel;
		this.boxItem = boxItem;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		BoxItemMenu itemMenu = new BoxItemMenu(creationPanel, boxItem);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
	}
}
