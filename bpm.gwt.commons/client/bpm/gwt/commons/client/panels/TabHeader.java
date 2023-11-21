package bpm.gwt.commons.client.panels;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TabHeader extends AbstractTabHeader implements ClickHandler {

	private static TabHeaderUiBinder uiBinder = GWT.create(TabHeaderUiBinder.class);

	interface TabHeaderUiBinder extends UiBinder<Widget, TabHeader> {
	}

	interface MyStyle extends CssResource {
		String btnSelected();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel focusPanel;

	@UiField
	Label lblTitle;
	
	@UiField
	Image btnClose;

	public TabHeader(String title, Tab tab, TabManager tabManager, boolean isCloseable) {
		super(tab, tabManager);
		initWidget(uiBinder.createAndBindUi(this));

		focusPanel.setTitle(title);
		lblTitle.setText(title);

		focusPanel.addClickHandler(this);

		btnClose.setVisible(isCloseable);
	}

	@UiHandler("btnClose")
	public void onCloseClick(ClickEvent event) {
		close();
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			this.focusPanel.addStyleName(style.btnSelected());
			this.focusPanel.addStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
		else {
			this.focusPanel.removeStyleName(style.btnSelected());
			this.focusPanel.removeStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
	}

	@Override
	public void applySize(double percentage) {
		focusPanel.getElement().getStyle().setWidth(percentage, Unit.PCT);
	}
	
	@Override
	public void setTitle(String title) {
		focusPanel.setTitle(title);
		lblTitle.setText(title);
	}

	@Override
	public void setModified(boolean isModified) { }
}
