package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This panel display a grey background to disable user interaction, a message and an image inviting users to wait.
 */
public class WaitPanel extends Composite {

	private static WaitPanelUiBinder uiBinder = GWT.create(WaitPanelUiBinder.class);

	interface WaitPanelUiBinder extends UiBinder<Widget, WaitPanel> {
	}

	@UiField
	SimplePanel panelLoading;

	public WaitPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setWaitPosition(String top, String left) {
		panelLoading.getElement().getStyle().setProperty("top", top);
		panelLoading.getElement().getStyle().setProperty("left", left);
	}
}
