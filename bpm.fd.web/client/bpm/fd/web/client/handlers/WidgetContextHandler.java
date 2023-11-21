package bpm.fd.web.client.handlers;

import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.popup.WidgetMenu;
import bpm.fd.web.client.widgets.WidgetComposite;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

public class WidgetContextHandler implements ContextMenuHandler {
	
	private IDropPanel dropPanel;
	private WidgetComposite widget;
	
	public WidgetContextHandler(WidgetComposite widget) {
		super();
		this.widget = widget;
	}
	
	public void setDropPanel(IDropPanel dropPanel) {
		this.dropPanel = dropPanel;
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		WidgetMenu itemMenu = new WidgetMenu(dropPanel, widget);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
		
		event.stopPropagation();
		event.preventDefault();
	}

}
