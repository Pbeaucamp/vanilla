package bpm.gwt.aklabox.commons.client.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GreyAbsolutePanel extends Composite {

	private static GreyAbsolutePanelUiBinder uiBinder = GWT
			.create(GreyAbsolutePanelUiBinder.class);

	interface GreyAbsolutePanelUiBinder extends
			UiBinder<Widget, GreyAbsolutePanel> {
	}

	public GreyAbsolutePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
