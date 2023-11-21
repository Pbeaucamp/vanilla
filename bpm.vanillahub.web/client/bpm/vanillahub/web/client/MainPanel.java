package bpm.vanillahub.web.client;

import bpm.gwt.commons.client.popup.IChangeView;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.TopToolbar;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite implements IChangeView {
	
	private static final int CLASSIC_TOP_TOOLBAR = 70;
	private static final int CLASSIC_FOOTER = 30;
	
	private static final int FULL_SCREEN_TOP_TOOLBAR = 25;
	private static final int FULL_SCREEN_FOOTER = 0;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}
	
	@UiField
	DockLayoutPanel dockPanel;
	
	@UiField
	SimplePanel toolbarPanel, mainPanel, footerPanel;

	private TopToolbar topToolbar;
	private ContentDisplayPanel contentDisplayPanel;
	
	public MainPanel(Bpm parentPanel, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		
		topToolbar = new TopToolbar(Labels.lblCnst.TitleApplication(), Images.INSTANCE.vanilla_hub_bandeau(), parentPanel, this, parentPanel, infoUser);
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		toolbarPanel.setWidget(topToolbar);
		
		contentDisplayPanel = new ContentDisplayPanel(parentPanel, infoUser);
		mainPanel.setWidget(contentDisplayPanel);

		footerPanel.addStyleName(VanillaCSS.BOTTOM);
		this.addStyleName(VanillaCSS.BODY_BACKGROUND);
	}

	@Override
	public void switchView(TypeView typeView) {
		switch (typeView) {
		case CLASSIC_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, CLASSIC_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, CLASSIC_FOOTER);
			break;
		case FULL_SCREEN_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, FULL_SCREEN_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, FULL_SCREEN_FOOTER);
			break;

		default:
			break;
		}
	}
}
