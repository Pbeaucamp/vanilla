package bpm.smart.web.client.panels.contents;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.smart.web.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ViewerPanel extends Tab {

	private static ViewerPanelUiBinder uiBinder = GWT.create(ViewerPanelUiBinder.class);
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	interface ViewerPanelUiBinder extends UiBinder<Widget, ViewerPanel> {
	}
	
	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;
	
	

	public ViewerPanel(TabManager tabManager) {
		super(tabManager, lblCnst.TabViewer(), false);
		this.add(uiBinder.createAndBindUi(this));

		
		loadEvent();
	}

	private void loadEvent() {
		
	}

	
}
