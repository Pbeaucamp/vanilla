package bpm.gwt.aklabox.commons.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BreackCrumbItem extends Composite implements HasClickHandlers {

	private static NavigationItemUiBinder uiBinder = GWT.create(NavigationItemUiBinder.class);

	interface NavigationItemUiBinder extends UiBinder<Widget, BreackCrumbItem> {
	}
	
	@UiField
	FocusPanel focus;
	
	@UiField
	Label lbl;

	public BreackCrumbItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public BreackCrumbItem(String lbl) {
		initWidget(uiBinder.createAndBindUi(this));

		setText(lbl);
	}
	
	public void setText(String text) {
		lbl.setText(text);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focus.addClickHandler(handler);
	}
}
