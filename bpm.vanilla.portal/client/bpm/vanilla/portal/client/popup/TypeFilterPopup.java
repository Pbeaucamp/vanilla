package bpm.vanilla.portal.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.panels.center.DocumentManagerPanel;
import bpm.vanilla.portal.client.panels.center.DocumentManagerPanel.TypeFilter;

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

public class TypeFilterPopup extends PopupPanel {

	private static TypeFilterPopupUiBinder uiBinder = GWT.create(TypeFilterPopupUiBinder.class);

	interface TypeFilterPopupUiBinder extends UiBinder<Widget, TypeFilterPopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private DocumentManagerPanel documentPanel;
	
	public TypeFilterPopup(DocumentManagerPanel documentPanel) {
		setWidget(uiBinder.createAndBindUi(this));
		this.documentPanel = documentPanel;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		addFilter(TypeFilter.TYPE_ALL_DOC, true);
		addFilter(TypeFilter.TYPE_GED_DOC, false);
		addFilter(TypeFilter.TYPE_MDM_DOC, false);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addFilter(TypeFilter filter, boolean first) {
		Label lblTheme = new Label(documentPanel.getTypeFilterLabel(filter));
		lblTheme.addClickHandler(new FilterHandler(filter));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class FilterHandler implements ClickHandler {
		
		private TypeFilter filter;

		public FilterHandler(TypeFilter filter) {
			this.filter = filter;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			documentPanel.changeFilter(filter);;
			TypeFilterPopup.this.hide();
		}
	}
}
