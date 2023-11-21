package bpm.faweb.client.listeners;

import bpm.faweb.client.MainPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class MeasureTreeListener implements ClickHandler {

	private MainPanel mainPanel;

	public MeasureTreeListener(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public void onClick(ClickEvent arg0) {
		if (((CheckBox) arg0.getSource()).getValue()) {
			mainPanel.getNavigationPanel().addItemSelected(((CheckBox) arg0.getSource()).getName());
		}
		else {
			mainPanel.getNavigationPanel().removeItemSelected(((CheckBox) arg0.getSource()).getName());
		}
	}
}
