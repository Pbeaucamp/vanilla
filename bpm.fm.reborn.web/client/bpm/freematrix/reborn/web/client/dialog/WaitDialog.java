package bpm.freematrix.reborn.web.client.dialog;

import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;

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
