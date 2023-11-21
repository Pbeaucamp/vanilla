package bpm.fd.web.client.panels.properties;

import bpm.fd.core.DashboardComponent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DrillProperties extends Composite {

	private static OptionsPropertiesUiBinder uiBinder = GWT.create(OptionsPropertiesUiBinder.class);

	interface OptionsPropertiesUiBinder extends UiBinder<Widget, DrillProperties> {
	}

	public DrillProperties(DashboardComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
