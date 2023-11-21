package bpm.es.web.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HomePanel extends Composite {

	private static HomePanelUiBinder uiBinder = GWT.create(HomePanelUiBinder.class);

	interface HomePanelUiBinder extends UiBinder<Widget, HomePanel> {
	}

	public HomePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
