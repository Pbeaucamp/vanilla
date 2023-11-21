package bpm.gwt.aklabox.commons.client.workflows;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleWindow extends Composite {

	private static ConsoleWindowUiBinder uiBinder = GWT.create(ConsoleWindowUiBinder.class);

	interface ConsoleWindowUiBinder extends UiBinder<Widget, ConsoleWindow> {
	}

	@UiField
	HTMLPanel console, controls, window;
	@UiField
	Image imgMinimize, imgClear;
	@UiField
	FocusPanel title;
	private boolean isMinimize;

	public ConsoleWindow(InstanceTable table) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void minimize(boolean isMinimize) {
		if (isMinimize) {
			window.setHeight("0px");
		}
		else {
			window.setHeight("190px");
		}
	}

	@UiHandler("imgMinimize")
	void onMinimize(ClickEvent event) {
		minimize(true);
	}

	@UiHandler("title")
	void onClickPanel(ClickEvent event) {
		if (isMinimize) {
			minimize(isMinimize);
			isMinimize = false;
		}
		else {
			minimize(isMinimize);
			isMinimize = true;
		}

	}

	@UiHandler("imgClear")
	void onClear(ClickEvent event) {
		window.clear();
	}

}
