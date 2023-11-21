package bpm.gwt.commons.client.popup;

import bpm.gwt.commons.client.panels.TopToolbar;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewsPopup extends PopupPanel {
	
	public enum TypeView {
		CLASSIC_VIEW,
		MINIMAL_VIEW,
		FULL_SCREEN_VIEW
	}

	private static ViewsPopupUiBinder uiBinder = GWT.create(ViewsPopupUiBinder.class);

	interface ViewsPopupUiBinder extends UiBinder<Widget, ViewsPopup> {
	}

	@UiField
	HTMLPanel panelMenu;
	
	private IChangeView changeView;
	
	public ViewsPopup(IChangeView changeView) {
		setWidget(uiBinder.createAndBindUi(this));
		this.changeView = changeView;
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	@UiHandler("btnClassicView")
	public void onClassicClick(ClickEvent event) {
		changeView.switchView(TypeView.CLASSIC_VIEW);
		this.hide();
	}
	
	@UiHandler("btnMinimal")
	public void onMinimalClick(ClickEvent event) {
		changeView.switchView(TypeView.MINIMAL_VIEW);
		this.hide();
	}

	@UiHandler("btnFullView")
	public void onFullScreenClick(ClickEvent event) {
		changeView.switchView(TypeView.FULL_SCREEN_VIEW);
		this.hide();
	}
}
