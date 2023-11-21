package bpm.architect.web.client.panels;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TabHeader extends FocusPanel {

	private static TabHeaderUiBinder uiBinder = GWT.create(TabHeaderUiBinder.class);

	interface TabHeaderUiBinder extends UiBinder<Widget, TabHeader> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	Label lblTitle;
	
	@UiField
	Image btnClose;

	public TabHeader(String title) {
		super();
		setWidget(uiBinder.createAndBindUi(this));

//		setTitle(title);
		lblTitle.setText(title);
	}
	
	public void setSelectHandler(ClickHandler selectHandler) {
		addClickHandler(selectHandler);
	}
	
	public void setCloseHandler(ClickHandler closeHandler) {
		btnClose.setVisible(closeHandler != null);
		btnClose.addClickHandler(closeHandler);
	}
	
	@Override
	public void setTitle(String title) {
//		setTitle(title);
		lblTitle.setText(title);
	}
}
