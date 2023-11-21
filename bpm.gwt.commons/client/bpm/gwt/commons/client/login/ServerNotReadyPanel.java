package bpm.gwt.commons.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ServerNotReadyPanel extends Composite {

	private static ServerNotReadyPanelUiBinder uiBinder = GWT
			.create(ServerNotReadyPanelUiBinder.class);

	interface ServerNotReadyPanelUiBinder extends
			UiBinder<Widget, ServerNotReadyPanel> {
	}

	public ServerNotReadyPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
