package bpm.fd.web.client.popup;

import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.WorkspacePanel;
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
	
	private WorkspacePanel parent;
	private boolean close;
	
	public SaveTypePopup(WorkspacePanel parent, boolean close) {
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.close = close;
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
		
		addType(Labels.lblCnst.SaveNewDashboard(), SAVE, true);
		addType(Labels.lblCnst.UpdateExistingDashboard(), UPDATE, false);
		
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
				parent.saveDashboard(close);
			}
			else if (type == UPDATE) {
				parent.saveDashboard(parent.getItemInfos(), true, close);
			}
			SaveTypePopup.this.hide();
		}
	}
}
