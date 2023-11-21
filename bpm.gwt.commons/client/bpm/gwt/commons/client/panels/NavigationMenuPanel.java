package bpm.gwt.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class NavigationMenuPanel extends HTMLPanel {

	public static final int DEFAULT_WIDTH = 250;

	private static NavigationMenuPanelUiBinder uiBinder = GWT.create(NavigationMenuPanelUiBinder.class);

	interface NavigationMenuPanelUiBinder extends UiBinder<Widget, NavigationMenuPanel> {
	}
	
	interface MyStyle extends CssResource {
		String menu();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Image btnMenu;

	@UiField
	HTMLPanel header, menuContent;

	private List<INavigationItem> items = new ArrayList<>();
	private boolean collapse = true;

	private int collapseLeft;
	
	private NavigationItem selectedItem;

	public NavigationMenuPanel() {
		this("");
	}

	public NavigationMenuPanel(String html) {
		super(html);

		add(uiBinder.createAndBindUi(this));

		setWidth(DEFAULT_WIDTH);
		setLeft(-collapseLeft);

		header.addStyleName(VanillaCSS.LEVEL_2);
		addStyleName(style.menu());
	}
	
	public void addItem(NavigationItem item) {
		this.items.add(item);
		menuContent.add(item);
	}
	
	public void setWidth(int width) {
		this.collapseLeft = width - 50;

		setWidth(width + "px");
	}

	private void setLeft(int left) {
		getElement().getStyle().setLeft(left, Unit.PX);
	}

	@UiHandler("btnMenu")
	public void onBtnMenu(ClickEvent event) {
		this.collapse = !collapse;
		updateUi(collapse);
		for (INavigationItem item : items) {
			item.onAction(collapse);
		}
	}

	public void setSelectedItem(NavigationItem navigationItem) {
		if (selectedItem != null) {
			selectedItem.setSelected(false);
		}
		this.selectedItem = navigationItem;
		navigationItem.setSelected(true);
	}

	private void updateUi(boolean collapse) {
		if (collapse) {
//			header.removeStyleName(ThemeCSS.LEVEL_1);
//			header.addStyleName(ThemeCSS.LEVEL_2);
			btnMenu.setResource(CommonImages.INSTANCE.ic_menu_white_36dp());
			setLeft(-collapseLeft);
		}
		else {
//			header.removeStyleName(ThemeCSS.LEVEL_2);
//			header.addStyleName(ThemeCSS.LEVEL_1);
			btnMenu.setResource(CommonImages.INSTANCE.ic_arrow_back_white_36dp());
			setLeft(0);
		}
	}
}
