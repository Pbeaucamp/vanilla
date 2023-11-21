package bpm.architect.web.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CompositeContainer extends Composite {

	private static CompositeContainerUiBinder uiBinder = GWT.create(CompositeContainerUiBinder.class);

	interface CompositeContainerUiBinder extends UiBinder<Widget, CompositeContainer> {
	}
	
	@UiField
	SimplePanel mainPanel;

	public CompositeContainer(HTMLPanel child) {
		initWidget(uiBinder.createAndBindUi(this));
		
		mainPanel.setWidget(child);
	}

}
