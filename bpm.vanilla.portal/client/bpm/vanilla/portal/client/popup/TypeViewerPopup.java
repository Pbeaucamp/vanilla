package bpm.vanilla.portal.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.panels.navigation.NavigationPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TypeViewerPopup extends PopupPanel {

	private static TypeViewerPopupUiBinder uiBinder = GWT.create(TypeViewerPopupUiBinder.class);

	interface TypeViewerPopupUiBinder extends UiBinder<Widget, TypeViewerPopup> {
	}

	@UiField
	HTMLPanel panelMenu;
	
	private NavigationPanel navigationPanel;
	
	public TypeViewerPopup(NavigationPanel navigationPanel) {
		setWidget(uiBinder.createAndBindUi(this));
		this.navigationPanel = navigationPanel;
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	@UiHandler("btnRepositoryContent")
	public void onRepositoryClick(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.REPOSITORY);
		this.hide();
	}
	
	@UiHandler("btnLastUsed")
	public void onLastUsedClick(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.LAST_USED);
		this.hide();
	}

	@UiHandler("btnOpenOnStartup")
	public void onOpenOnStartupClick(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.OPEN_ON_STARTUP);
		this.hide();
	}

	@UiHandler("btnWatchlist")
	public void onWatchlistClick(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.WATCH_LIST);
		this.hide();
	}

	@UiHandler("btnReportBackground")
	public void onReportBackgroundClick(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.REPORT_BACKGROUND);
		this.hide();
	}
}
