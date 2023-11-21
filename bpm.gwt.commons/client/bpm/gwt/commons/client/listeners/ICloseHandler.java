package bpm.gwt.commons.client.listeners;

import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.viewer.ButtonViewer;

public interface ICloseHandler {

	public void closeViewer(ButtonViewer button, IClose close, Widget viewer);
	
}
