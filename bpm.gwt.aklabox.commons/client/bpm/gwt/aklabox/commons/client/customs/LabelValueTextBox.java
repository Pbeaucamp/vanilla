package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LabelValueTextBox extends Composite {

	private static LabelTextBoxUiBinder uiBinder = GWT.create(LabelTextBoxUiBinder.class);

	interface LabelTextBoxUiBinder extends UiBinder<Widget, LabelValueTextBox> {
	}
	
	@UiField
	Label label;
	
	@UiField
	ValueTextBox textbox;

	public LabelValueTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
		setWidth("80px");
	}

	public void setText(String text) {
		textbox.setText(text != null ? text : "0");
	}

	public void setText(int text) {
		textbox.setText(String.valueOf(text));
	}
	
	public void setWidth(String width) {
		textbox.setWidth(width);
	}
	
	public String getText() {
		return textbox.getText();
	}
	
	public int getValue() {
		return Integer.parseInt(textbox.getText());
	}
	
	public boolean isEmpty() {
		return !(getText() != null && !getText().isEmpty());
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
}
