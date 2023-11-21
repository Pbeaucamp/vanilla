package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LabelTextBox extends Composite {

	private static LabelTextBoxUiBinder uiBinder = GWT.create(LabelTextBoxUiBinder.class);

	interface LabelTextBoxUiBinder extends UiBinder<Widget, LabelTextBox> {
	}
	
	interface MyStyle extends CssResource {
		String padding();
		String paddingWithClear();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Label label;

	@UiField
	TextHolderBox textbox;
	
	@UiField
	Image btnClear;
	
	private boolean showClear = false;

	public LabelTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		textbox.setText(text);
	}

	public String getText() {
		return textbox.getText();
	}

	public String getLabel() {
		return label.getText();
	}
	
	public void clear() {
		textbox.setText("");
	}

	public void setEnabled(boolean enabled) {
		textbox.setEnabled(enabled);
	}

	public void setLabel(String lbl) {
		label.setText(lbl);
	}

	public void setPlaceHolder(String placeHolder) {
		label.setText(placeHolder);
		textbox.setPlaceHolder(placeHolder);
	}

	public void setPassword(String password) {
		textbox.setPassword(password);
	}

	public String getPlaceHolder() {
		return label.getText();
	}
	
	public void displayClear(ClickHandler handler) {
		this.showClear = true;
		
		textbox.removeStyleName(style.padding());
		textbox.addStyleName(style.paddingWithClear());

		if (handler != null) {
			btnClear.addClickHandler(handler);
		}
		refreshClear();
	}
	
	@UiHandler("textbox")
	public void onValueChange(KeyUpEvent event) {
		refreshClear();
	}
	
	@UiHandler("btnClear")
	public void onClear(ClickEvent event) {
		textbox.setText("");
		refreshClear();
	}

	public void setFocus(boolean focused) {
		textbox.setFocus(focused);
	}

	public void setReadOnly(boolean readOnly) {
		textbox.setReadOnly(readOnly);
	}
	
	private void refreshClear() {
		btnClear.setVisible(showClear && !textbox.getText().isEmpty());
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return textbox.addDomHandler(handler, ClickEvent.getType());
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textbox.addDomHandler(handler, KeyUpEvent.getType());
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return textbox.addDomHandler(handler, BlurEvent.getType());
	}
}
