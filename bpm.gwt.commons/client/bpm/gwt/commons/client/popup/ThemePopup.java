package bpm.gwt.commons.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ThemePopup extends PopupPanel {

	private static ViewsPopupUiBinder uiBinder = GWT.create(ViewsPopupUiBinder.class);

	interface ViewsPopupUiBinder extends UiBinder<Widget, ThemePopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private IChangeTheme changeTheme;
	
	public ThemePopup(IChangeTheme changeTheme) {
		setWidget(uiBinder.createAndBindUi(this));
		this.changeTheme = changeTheme;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for(String[] themes : VanillaCSS.CSS_FILE_NAMES){
			addTheme(themes, first);
			first = false;
		}
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addTheme(String[] themes, boolean first) {
		Label lblTheme = new Label(themes[0]);
		lblTheme.addClickHandler(new ThemeHandler(themes[1]));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class ThemeHandler implements ClickHandler {
		
		private String themeValue;

		public ThemeHandler(String themeValue) {
			this.themeValue = themeValue;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			changeTheme.changeTheme(themeValue);
			ThemePopup.this.hide();
		}
	}
}
