package bpm.vanilla.portal.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryDisplayPopup extends PopupPanel {

	private static ViewsPopupUiBinder uiBinder = GWT.create(ViewsPopupUiBinder.class);

	interface ViewsPopupUiBinder extends UiBinder<Widget, RepositoryDisplayPopup> {
	}

	@UiField
	HTMLPanel panelMenu;
	
	private RepositoryContentPanel repositoryPanel;
	
	public RepositoryDisplayPopup(RepositoryContentPanel repositoryPanel) {
		setWidget(uiBinder.createAndBindUi(this));
		this.repositoryPanel = repositoryPanel;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	@UiHandler("btnTable")
	public void onTableClick(ClickEvent event) {
		repositoryPanel.changeDisplay(true);
		this.hide();
	}
	
	@UiHandler("btnThumbnail")
	public void onThumbnailClick(ClickEvent event) {
		repositoryPanel.changeDisplay(false);
		this.hide();
	}
}
