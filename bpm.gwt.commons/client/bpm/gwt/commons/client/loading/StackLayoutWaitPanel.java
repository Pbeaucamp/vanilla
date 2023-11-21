package bpm.gwt.commons.client.loading;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class StackLayoutWaitPanel extends HTMLPanel implements IWait {

	private GreyAbsolutePanel greyPanel;
	private StackWaitAbsolutePanel waitPanel;
	private boolean isCharging = false;
	private int indexCharging = 0;

	public StackLayoutWaitPanel(String html) {
		super(html);
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				int height = this.getOffsetHeight();
				int width = this.getOffsetWidth();

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new StackWaitAbsolutePanel();

				this.add(greyPanel);
				this.add(waitPanel);

				if(height > 0 && width > 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", "50px");
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 85) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", "50px");
					DOM.setStyleAttribute(waitPanel.getElement(), "left", "95px");
				}
			}
		}
		else if (!visible) {
			indexCharging--;

			if (isCharging && indexCharging == 0) {
				isCharging = false;

				this.remove(greyPanel);
				this.remove(waitPanel);
			}
		}
	}

}
