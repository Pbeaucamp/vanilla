package bpm.gwt.commons.client.listeners;

import bpm.gwt.commons.client.dialog.ButtonTab;
import bpm.gwt.commons.client.dialog.ITabDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class TabSwitchHandler implements ClickHandler {

	private ITabDialog tab;
	private ButtonTab buttonTab;
	private Widget widget;
	
	public TabSwitchHandler(ITabDialog tab, ButtonTab buttonTab, Widget widget) {
		this.tab = tab;
		this.buttonTab = buttonTab;
		this.widget = widget;
	}

	@Override
	public void onClick(ClickEvent event) {
		tab.switchViewer(buttonTab, widget);
	}
}
