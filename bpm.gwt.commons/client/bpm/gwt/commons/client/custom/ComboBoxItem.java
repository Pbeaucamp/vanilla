package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ComboBoxItem<T> extends Composite implements HasMouseDownHandlers {

	private static ComboBoxItemUiBinder uiBinder = GWT.create(ComboBoxItemUiBinder.class);

	interface ComboBoxItemUiBinder extends UiBinder<Widget, ComboBoxItem<?>> {
	}
	
	@UiField
	Label label;
	
	private String text;
	private T item;

	public ComboBoxItem(String text, T item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.text = text;
		this.item = item;
		
		label.setText(text);
	}

	public String getText() {
		return text;
	}

	public T getItem() {
		return item;
	}

//	@Override
//	public HandlerRegistration addClickHandler(ClickHandler handler) {
//		return addDomHandler(handler, ClickEvent.getType());
//	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
}
