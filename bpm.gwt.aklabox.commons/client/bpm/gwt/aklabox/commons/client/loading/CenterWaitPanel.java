package bpm.gwt.aklabox.commons.client.loading;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class CenterWaitPanel extends HTMLPanel implements IWait {

	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	private boolean isCharging;

	private int indexCharging = 0;

	public CenterWaitPanel(String html) {
		super(html);
	}
	
	@Override
	public void showWaitPart(boolean visible) {
		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				this.add(greyPanel);
				this.add(waitPanel);

				int height = this.getOffsetHeight();
				int width = this.getOffsetWidth();

				if(height > 100 && width > 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
				}
				else if(height < 100 && width > 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", "235px");
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", "235px");
					DOM.setStyleAttribute(waitPanel.getElement(), "left", "400px");
				}
			}
		}
		else if (!visible && indexCharging != 0) {
			indexCharging--;

			if (isCharging && indexCharging == 0) {
				isCharging = false;

				this.remove(greyPanel);
				this.remove(waitPanel);
			}
		}
	}

}
