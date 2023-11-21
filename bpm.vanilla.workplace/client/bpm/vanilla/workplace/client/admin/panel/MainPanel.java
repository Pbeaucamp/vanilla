package bpm.vanilla.workplace.client.admin.panel;

import bpm.vanilla.workplace.client.admin.panel.project.PanelProject;
import bpm.vanilla.workplace.client.admin.panel.user.PanelUser;
import bpm.vanilla.workplace.client.tab.CustomTabLayoutPanel;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {

	private static MainPanelUiBinder uiBinder = GWT
			.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	public MainPanel(PlaceWebUser user) {
		initWidget(uiBinder.createAndBindUi(this));
		
		CustomTabLayoutPanel tabPanel = new CustomTabLayoutPanel(150, Unit.PX, true);
		mainPanel.add(tabPanel);
		
		PanelUser panelUser = new PanelUser();
		tabPanel.add(panelUser, "Manage Users");
		
		PanelProject panelAdmin = new PanelProject(user);
		tabPanel.add(panelAdmin, "Manage Packages");
		
		HTMLPanel panel = new HTMLPanel("Test");
		tabPanel.add(panel, "Logs");
	}
}
