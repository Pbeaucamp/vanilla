package bpm.gwt.aklabox.commons.client.customs;

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

public class AutoCompleteItem<T> extends Composite implements HasMouseDownHandlers {

	private static AutoCompleteItemUiBinder uiBinder = GWT.create(AutoCompleteItemUiBinder.class);

	interface AutoCompleteItemUiBinder extends UiBinder<Widget, AutoCompleteItem<?>> {
	}
	
	@UiField
	Label label;
	
	private String text;
	private T item;
	
	private String preWord;

	public AutoCompleteItem(String text, T item, String preWord) {
		initWidget(uiBinder.createAndBindUi(this));
		this.text = text;
		this.item = item;
		this.preWord = preWord;
		
		label.setText(text);
		label.setTitle(text);
	}

	public String getText() {
		return text;
	}

	public T getItem() {
		return item;
	}
	
	public String getPreWord() {
		return preWord;
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
