package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.ContentComponent;
import bpm.fd.core.component.ContentComponent.TabDisplay;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ContentComponentOptionsProperties extends CompositeProperties<IComponentOption> {
	
	private static ContentComponentOptionsPanelUiBinder uiBinder = GWT.create(ContentComponentOptionsPanelUiBinder.class);

	interface ContentComponentOptionsPanelUiBinder extends UiBinder<Widget, ContentComponentOptionsProperties> {
	}
	
	@UiField
	ListBoxWithButton lstPosition;

	public ContentComponentOptionsProperties(ContentComponent contentComponent) {
		initWidget(uiBinder.createAndBindUi(this));

		for (TabDisplay type : TabDisplay.values()) {
			lstPosition.addItem(findName(type), String.valueOf(type.getType()));
		}
		
		if (contentComponent != null) {
			lstPosition.setSelectedIndex(contentComponent.getTabDisplay().getType());
		}
	}

	private String findName(TabDisplay type) {
		switch (type) {
		case BOTTOM:
			return Labels.lblCnst.Bottom();
		case LEFT:
			return Labels.lblCnst.Left();
		case RIGHT:
			return Labels.lblCnst.Right();
		case TOP:
			return Labels.lblCnst.Top();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	@Override
	public void buildProperties(IComponentOption component) {
		ContentComponent contentComponent = (ContentComponent) component;
		contentComponent.setTabDisplay(getSelectedTypeDisplay());
	}

	private TabDisplay getSelectedTypeDisplay() {
		int type = Integer.parseInt(lstPosition.getSelectedItem());
		return TabDisplay.valueOf(type);
	}
}
