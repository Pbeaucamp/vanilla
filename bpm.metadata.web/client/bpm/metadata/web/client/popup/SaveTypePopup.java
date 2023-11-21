package bpm.metadata.web.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.panels.MetadataPanel;

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

public class SaveTypePopup extends PopupPanel {

	private static final int SAVE = 0;
	private static final int UPDATE = 1;
	
	private static FormatUiBinder uiBinder = GWT.create(FormatUiBinder.class);

	interface FormatUiBinder extends UiBinder<Widget, SaveTypePopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private MetadataPanel parent;
	
	public SaveTypePopup(MetadataPanel parent) {
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
		
		addType(Labels.lblCnst.SaveNewMetadata(), SAVE, true);
		addType(Labels.lblCnst.UpdateExistingMetadata(), UPDATE, false);
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addType(String typeName, int type, boolean first) {
		Label lblTheme = new Label(typeName);
		lblTheme.addClickHandler(new TypeHandler(type));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class TypeHandler implements ClickHandler {
		
		private int type;

		public TypeHandler(int type) {
			this.type = type;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			if (type == SAVE) {
				parent.saveMetadata();
			}
			else if (type == UPDATE) {
				parent.saveMetadata(null, true);
			}
			SaveTypePopup.this.hide();
		}
	}
}
