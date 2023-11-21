package bpm.es.web.client.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MenuItem extends Composite implements HasWidgets {

	private static MenuItemUiBinder uiBinder = GWT.create(MenuItemUiBinder.class);

	interface MenuItemUiBinder extends UiBinder<Widget, MenuItem> {
	}
	
	interface MyStyle extends CssResource {
		String menuItemSelected();
		String menuSubItemSelected();
		String subLbl();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel item;
	
	@UiField
	Image img;
	
	@UiField
	Label lbl;
	
	@UiField
	HTMLPanel childsPanel;
	
	private List<Widget> childs;
	private boolean isChild = false;

	public MenuItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setResource(ImageResource resource) {
		img.setResource(resource);
	}

	public void setText(String text) {
		lbl.setText(text);
	}
	
	public void setSelected(boolean selected) {
		if (selected) {
			item.addStyleName(isChild() ? style.menuSubItemSelected() : style.menuItemSelected());
		}
		else {
			item.removeStyleName(style.menuItemSelected());
			item.removeStyleName(style.menuSubItemSelected());
		}
		
		if (hasChild()) {
			showChild(selected);
		}
	}
	
	private void showChild(boolean selected) {
		childsPanel.setVisible(selected);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return item.addClickHandler(handler);
	}

	@Override
	public void add(Widget w) {
		if (w instanceof MenuItem) {
			if (childs == null) {
				childs = new ArrayList<>();
			}
			childs.add(w);
			
			childsPanel.add(w);
			
			((MenuItem) w).setChild(true);
		}
	}

	@Override
	public void clear() {
		childs.clear();
		childsPanel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return childs.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		childs.remove(w);
		return childsPanel.remove(w);
	}
	
	private boolean hasChild() {
		return childs != null && !childs.isEmpty();
	}
	
	private void setChild(boolean isChild) {
		this.isChild = isChild;
		lbl.addStyleName(style.subLbl());
	}
	
	public boolean isChild() {
		return isChild;
	}
}
