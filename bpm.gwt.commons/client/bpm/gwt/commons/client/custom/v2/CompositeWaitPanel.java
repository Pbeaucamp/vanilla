package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

/**
 * Extends this class if you want your composite to automatically display WaitPanel when you call {@link CompositeWaitPanel#showWaitPart(boolean)}
 */
public abstract class CompositeWaitPanel extends Composite implements IWait {
	
	private WaitPanel waitPanel;
	private boolean isCharging = false;

	@Override
	public void showWaitPart(boolean visible) {
		Panel panel = getRelativeMainPanel();
		if (panel != null) {
			if (visible && !isCharging) {
				isCharging = true;
	
				int height = panel.getOffsetHeight();
				int width = panel.getOffsetWidth();
	
				waitPanel = new WaitPanel();
				panel.add(waitPanel);
	
				waitPanel.setWaitPosition(((height / 2) - 50) + "px", ((width / 2) - 100) + "px");
			}
			else if (!visible && isCharging) {
				isCharging = false;
				panel.remove(waitPanel);
			}
		}
	}

	/**
	 * Override this method if you want the wait support for your panel.
	 * You need to return a panel with a relative position.
	 * 
	 * @return a panel with a relative position
	 */
	protected Panel getRelativeMainPanel() {
		return null;
	}
}
