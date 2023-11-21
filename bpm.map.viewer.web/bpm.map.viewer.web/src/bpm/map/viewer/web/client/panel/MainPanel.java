package bpm.map.viewer.web.client.panel;

import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel {
		
	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}
	
	@UiField
	DockLayoutPanel dockPanel;
	
	@UiField
	SimplePanel toolbarPanel,  footerPanel, mainPanel;
	
	private TopToolbar topToolbar;
	private ContentDisplayPanel contentDisplayPanel;

	public MainPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		buildToolbar();
		buildFooterPanel();
		buildMainPanel();
		
		this.addStyleName(VanillaCSS.BODY_BACKGROUND);
	}

	private void buildToolbar() {
		topToolbar = new TopToolbar(this);
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		toolbarPanel.setWidget(topToolbar);
	}

	private void buildFooterPanel() {
		footerPanel.addStyleName(VanillaCSS.BOTTOM);
	}

	private void buildMainPanel() {
		contentDisplayPanel = new ContentDisplayPanel(this);
		mainPanel.setWidget(contentDisplayPanel);
	}

	public ContentDisplayPanel getContentDisplayPanel() {
		return contentDisplayPanel;
	}

}
