package bpm.gwt.commons.client.loading;

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
					waitPanel.getElement().getStyle().setProperty("top", ((height / 2) - 50) + "px");
					waitPanel.getElement().getStyle().setProperty("left", ((width / 2) - 100) + "px");
				}
				else if(height < 100 && width > 0) {
					waitPanel.getElement().getStyle().setProperty("top", "235px");
					waitPanel.getElement().getStyle().setProperty("left", ((width / 2) - 100) + "px");
				}
				else {
					waitPanel.getElement().getStyle().setProperty("top", "235px");
					waitPanel.getElement().getStyle().setProperty("left", "400px");
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
