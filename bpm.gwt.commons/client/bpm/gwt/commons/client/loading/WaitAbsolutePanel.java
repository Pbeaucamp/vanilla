package bpm.gwt.commons.client.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WaitAbsolutePanel extends Composite {

	private static WaitAbsolutePanelUiBinder uiBinder = GWT
			.create(WaitAbsolutePanelUiBinder.class);

	interface WaitAbsolutePanelUiBinder extends
			UiBinder<Widget, WaitAbsolutePanel> {
	}

	public WaitAbsolutePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
