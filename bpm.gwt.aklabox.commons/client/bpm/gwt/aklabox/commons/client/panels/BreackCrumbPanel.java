package bpm.gwt.aklabox.commons.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class BreackCrumbPanel extends HTMLPanel {

	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);

	interface NavigationPanelUiBinder extends UiBinder<Widget, BreackCrumbPanel> {
	}

	@UiField
	HTMLPanel breakcrumb;

	public BreackCrumbPanel() {
		this("");
	}

	public BreackCrumbPanel(String html) {
		super(html);
		add(uiBinder.createAndBindUi(this));
	}
	
	public void addItem(BreackCrumbItem item) {
		breakcrumb.add(item);
	}
}
