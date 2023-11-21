package bpm.smart.web.client.utils;

import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class LevelItem extends Composite {

	private static LevelItemUiBinder uiBinder = GWT.create(LevelItemUiBinder.class);

	interface LevelItemUiBinder extends UiBinder<Widget, LevelItem> {
	}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtLevel;

	@UiField
	Image btnDel;

	//private RecodeClassificationPage parent;

	public LevelItem() {
		initWidget(uiBinder.createAndBindUi(this));
		//this.parent = parent;
	}

	@UiHandler("btnDel")
	public void onDelClick(ClickEvent event) {
		this.removeFromParent();
	}

	public String getLevel() {
		return txtLevel.getText();
	}
}