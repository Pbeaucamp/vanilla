package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.aklabox.commons.client.loading.WaitAbsolutePanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class WaitDialog {

	private static GreyAbsolutePanel greyPanel;
	private static WaitAbsolutePanel waitPanel;
	private static boolean waitVisible = false;

	public static void showWaitPart(boolean visible) {
		if (visible && !waitVisible) {
			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();
			greyPanel.getElement().setAttribute("style", "z-index: 999;");
			waitPanel.getElement().setAttribute("style", "z-index: 999;");
			int height = Window.getClientHeight();
			int width = Window.getClientWidth();

			RootPanel.get().add(greyPanel);
			RootPanel.get().add(waitPanel);
			RootPanel.get().setWidgetPosition(waitPanel, (width / 2) - 100, (height / 2) - 60);
			waitVisible = true;
		}
		else if (!visible && waitVisible) {
			greyPanel.removeFromParent();
			waitPanel.removeFromParent();
			waitVisible = false;
		}
	}

}
