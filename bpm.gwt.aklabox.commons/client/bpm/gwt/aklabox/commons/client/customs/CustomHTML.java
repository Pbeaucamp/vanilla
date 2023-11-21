package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

public class CustomHTML extends HTML implements HasContextMenuHandlers{

	HandlerManager manager = new HandlerManager(this);
	
	public CustomHTML() {
		super();

		this.sinkEvents(Event.ONCONTEXTMENU);
	}
	
	public CustomHTML(String html){
		super(html);
		
		this.sinkEvents(Event.ONCONTEXTMENU);
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return manager.addHandler(ContextMenuEvent.getType(), handler);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
		super.fireEvent(event);
	}
}
