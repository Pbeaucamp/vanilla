package bpm.faweb.client.listeners;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

public class FaWebNumberListener implements KeyPressHandler {

	public void onKeyPress(KeyPressEvent event) {
		if ((!Character.isDigit(event.getCharCode())) && (event.getCharCode() != (char) KeyCodes.KEY_TAB)
		&& (event.getCharCode() != (char) KeyCodes.KEY_BACKSPACE)
		&& (event.getCharCode() != (char) KeyCodes.KEY_DELETE) && (event.getCharCode() != (char) KeyCodes.KEY_ENTER)
		&& (event.getCharCode() != (char) KeyCodes.KEY_HOME) && (event.getCharCode() != (char) KeyCodes.KEY_END)
		&& (event.getCharCode() != (char) KeyCodes.KEY_LEFT) && (event.getCharCode() != (char) KeyCodes.KEY_UP)
		&& (event.getCharCode() != (char) KeyCodes.KEY_RIGHT) && (event.getCharCode() != (char) KeyCodes.KEY_DOWN)) 
		{
			((TextBox)event.getSource()).cancelKey(); 
		}
	}
	
}
