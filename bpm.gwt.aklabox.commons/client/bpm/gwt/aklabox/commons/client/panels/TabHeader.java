package bpm.gwt.aklabox.commons.client.panels;

//import bpm.gwt.aklabox.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TabHeader extends Composite implements ClickHandler {

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

	private Tab tab;
	private TabManager tabManager;

	private boolean open = false;

	public TabHeader(String title, Tab tab, TabManager tabManager, boolean isCloseable) {
		initWidget(uiBinder.createAndBindUi(this));
		this.tab = tab;
		this.tabManager = tabManager;

		focusPanel.setTitle(title);
		lblTitle.setText(title);

		focusPanel.addClickHandler(this);

		btnClose.setVisible(isCloseable);
	}

	@Override
	public void onClick(ClickEvent event) {
		tabManager.selectTab(this);
	}

	@UiHandler("btnClose")
	public void onCloseClick(ClickEvent event) {
		close();
	}

	public void close() {
		tabManager.closeTab(this);
		setOpen(false);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			this.focusPanel.addStyleName(style.btnSelected());
			//this.focusPanel.addStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
		else {
			this.focusPanel.removeStyleName(style.btnSelected());
			//this.focusPanel.removeStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
	}

	public Tab getTab() {
		return tab;
	}

	public void applySize(double percentage) {
		focusPanel.getElement().getStyle().setWidth(percentage, Unit.PCT);
		// lblTitle.getElement().getStyle().setWidth(width, Unit.PCT);
	}
	
	public void setTitle(String title) {
		focusPanel.setTitle(title);
		  lblTitle.setText(title);
	}
}
