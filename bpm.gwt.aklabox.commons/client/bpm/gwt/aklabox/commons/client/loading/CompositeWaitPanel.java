package bpm.gwt.aklabox.commons.client.loading;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class CompositeWaitPanel extends Composite implements IWait {
	
	private WaitAbsolutePanel waitPanel;
	private GreyAbsolutePanel greyPanel;
	private boolean isCharging = false;

	@Override
	public void showWaitPart(boolean visible) {
		if (visible && !isCharging) {
			isCharging = true;

			int height = Window.getClientHeight();
			int width = Window.getClientWidth();

			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();

			RootPanel.get().add(greyPanel);
			RootPanel.get().add(waitPanel);

			DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
			DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
		}
		else if (!visible && isCharging) {
			isCharging = false;

			RootPanel.get().remove(greyPanel);
			RootPanel.get().remove(waitPanel);
		}
	}

}
