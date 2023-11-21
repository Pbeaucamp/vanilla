package bpm.gwt.commons.client.listeners;

import bpm.gwt.commons.client.viewer.ButtonViewer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ViewerSwitchHandler<T> implements ClickHandler {

	private ISwitchHandler<T> vanillaViewer;
	private ButtonViewer buttonViewer;
	private T viewer;
	
	public ViewerSwitchHandler(ISwitchHandler<T> vanillaViewer, ButtonViewer buttonViewer, T viewer) {
		this.vanillaViewer = vanillaViewer;
		this.buttonViewer = buttonViewer;
		this.viewer = viewer;
	}

	@Override
	public void onClick(ClickEvent event) {
		vanillaViewer.switchViewer(buttonViewer, viewer);
	}
}
