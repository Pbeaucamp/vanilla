package bpm.gwt.commons.client.panels;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NavigationItem extends Composite implements HasClickHandlers, INavigationItem {
	
	private static final int MARGIN_LEFT = 10;
	private static final int IMG_PADDING = 9;
	private static final int IMG_WIDTH = 32;

	private static NavigationItemUiBinder uiBinder = GWT.create(NavigationItemUiBinder.class);

	interface NavigationItemUiBinder extends UiBinder<Widget, NavigationItem> {
	}
	
	interface MyStyle extends CssResource {
		String selected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel focus;
	
	@UiField
	HTMLPanel item;
	
	@UiField
	Label lbl;
	
	@UiField
	Image img;
	
	private NavigationMenuPanel parent;
	
	public NavigationItem(NavigationMenuPanel parent, String lbl, ImageResource imgResource, int width) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		
		this.lbl.setText(lbl);
		this.img.setResource(imgResource);
		this.img.setTitle(lbl);
		
		this.lbl.getElement().getStyle().setMarginLeft(MARGIN_LEFT, Unit.PX);
		this.lbl.getElement().getStyle().setWidth(width - (MARGIN_LEFT + (2 * IMG_PADDING) + IMG_WIDTH), Unit.PX);
		
		this.img.getElement().getStyle().setWidth(IMG_WIDTH, Unit.PX);
		this.img.getElement().getStyle().setPadding(IMG_PADDING, Unit.PX);
	}
	
	public void setText(String text) {
		lbl.setText(text);
	}
	
	public void setResource(ImageResource resource) {
		img.setResource(resource);
	}
	
	public void fireSelection() {
		NativeEvent event = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
		DomEvent.fireNativeEvent(event, focus);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focus.addClickHandler(handler);
	}
	
	@UiHandler("focus")
	public void onItemClick(ClickEvent event) {
		parent.setSelectedItem(this);
	}
	
	public void setSelected(boolean selected) {
		if (selected) {
			item.addStyleName(VanillaCSS.LEVEL_1);
			item.addStyleName(style.selected());
		}
		else {
			item.removeStyleName(VanillaCSS.LEVEL_1);
			item.removeStyleName(style.selected());
		}
	}

	@Override
	public void onAction(boolean collapse) { }
}
