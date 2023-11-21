package bpm.gwt.commons.client.listeners;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public abstract class EnterKeyHandler implements KeyDownHandler {
	
	public void onKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			enterKeyDown(event);
	}

	public abstract void enterKeyDown(KeyDownEvent event);
}