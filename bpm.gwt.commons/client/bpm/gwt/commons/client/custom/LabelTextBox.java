package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LabelTextBox extends Composite {

	private static LabelTextBoxUiBinder uiBinder = GWT.create(LabelTextBoxUiBinder.class);

	interface LabelTextBoxUiBinder extends UiBinder<Widget, LabelTextBox> {
	}

	@UiField
	Label label;

	@UiField
	TextHolderBox textbox;

	public LabelTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		textbox.setText(text);
	}

	public String getText() {
		return textbox.getText();
	}

	public void clear() {
		textbox.setText("");
	}

	public void setEnabled(boolean enabled) {
		textbox.setEnabled(enabled);
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
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
