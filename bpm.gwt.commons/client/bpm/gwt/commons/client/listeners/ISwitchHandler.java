package bpm.gwt.commons.client.listeners;

import bpm.gwt.commons.client.viewer.ButtonViewer;

public interface ISwitchHandler<T> {
	
	public void switchViewer(ButtonViewer buttonViewer, T viewer);
	
}
