package bpm.fm.designer.web.client.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationPropertiesPanel extends Composite {

	private static ApplicationPropertiesPanelUiBinder uiBinder = GWT.create(ApplicationPropertiesPanelUiBinder.class);

	interface ApplicationPropertiesPanelUiBinder extends UiBinder<Widget, ApplicationPropertiesPanel> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	public ApplicationPropertiesPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
