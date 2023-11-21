package bpm.metadata.web.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.dialogs.ResourceDialog.TypeResource;
import bpm.metadata.web.client.panels.ColumnPropertiesPanel;

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

public class ResourceTypePopup extends PopupPanel {

	private static final int FILTER = 0;
	private static final int PROMPT = 1;
	
	private static FormatUiBinder uiBinder = GWT.create(FormatUiBinder.class);

	interface FormatUiBinder extends UiBinder<Widget, ResourceTypePopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private ColumnPropertiesPanel parent;
	
	public ResourceTypePopup(ColumnPropertiesPanel parent) {
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
		
		addType(Labels.lblCnst.AddFilter(), FILTER, true);
		addType(Labels.lblCnst.AddPrompt(), PROMPT, false);
		
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
			if (type == FILTER) {
				parent.addResource(TypeResource.FILTER);
			}
			else if (type == PROMPT) {
				parent.addResource(TypeResource.PROMPT);
			}
			ResourceTypePopup.this.hide();
		}
	}
}
