package bpm.fwr.client.tree;

import bpm.fwr.client.listeners.AdvClickHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

public class DatasourceTreeItem extends HTML{
	private AdvClickHandler listener;
	
	public DatasourceTreeItem(String html, AdvClickHandler listener) {
		super(html);
		this.listener = listener;
		sinkEvents(Event.ONCONTEXTMENU);
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		GWT.log("onBrowserEvent", null);
		switch (DOM.eventGetType(event)) {
			case Event.ONCONTEXTMENU:
				GWT.log("Event.ONCONTEXTMENU", null);
				event.stopPropagation();
				event.preventDefault();
				
				listener.onRightClick(this, event);
				
				return;
		 
			default:
				break; // Do nothing
		}

	    // We must call super for all handlers.
	    super.onBrowserEvent(event);
	}
}
