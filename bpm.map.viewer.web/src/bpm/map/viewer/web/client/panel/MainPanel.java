package bpm.map.viewer.web.client.panel;

import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.map.viewer.web.client.Bpm_map_viewer_web.TypeDisplay;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
	private TypeDisplay display;

	public MainPanel(TypeDisplay display, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.display = display;
		if(display == TypeDisplay.NORMAL) {
			buildToolbar(keycloak);
			buildFooterPanel();
		} 
		
		buildMainPanel(display);
		
		this.addStyleName(VanillaCSS.BODY_BACKGROUND);
	}
	
	@Override
	public void onLoad(){
		if(display == TypeDisplay.VIEWER) {
			toolbarPanel.setVisible(false);
			footerPanel.setVisible(false);
			dockPanel.setWidgetSize(dockPanel.getWidget(0), 0);
			dockPanel.setWidgetSize(dockPanel.getWidget(1), 0);

		}
	}

	private void buildToolbar(Keycloak keycloak) {
		topToolbar = new TopToolbar(this, keycloak);
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		toolbarPanel.setWidget(topToolbar);
	}

	private void buildFooterPanel() {
		footerPanel.addStyleName(VanillaCSS.BOTTOM);
	}

	private void buildMainPanel(TypeDisplay display) {
		contentDisplayPanel = new ContentDisplayPanel(this, display);
		mainPanel.setWidget(contentDisplayPanel);
	}

	public ContentDisplayPanel getContentDisplayPanel() {
		return contentDisplayPanel;
	}

}
