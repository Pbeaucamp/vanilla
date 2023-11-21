package bpm.gwt.aklabox.commons.client.loading;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class RootWaitPanel implements IWait {
	
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

			waitPanel.getElement().getStyle().setTop((height / 2) - 50, Unit.PX);
			waitPanel.getElement().getStyle().setLeft((width / 2) - 100, Unit.PX);
		}
		else if (!visible && isCharging) {
			isCharging = false;

			RootPanel.get().remove(greyPanel);
			RootPanel.get().remove(waitPanel);
		}
	}

}
