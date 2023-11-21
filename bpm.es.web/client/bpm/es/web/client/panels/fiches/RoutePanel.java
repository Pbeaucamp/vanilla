package bpm.es.web.client.panels.fiches;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RoutePanel extends Composite {

	private static RoutePanelUiBinder uiBinder = GWT.create(RoutePanelUiBinder.class);

	interface RoutePanelUiBinder extends UiBinder<Widget, RoutePanel> {
	}

	public RoutePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
}
