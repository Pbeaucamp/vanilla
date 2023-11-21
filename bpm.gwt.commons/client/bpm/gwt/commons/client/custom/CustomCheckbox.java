package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CustomCheckbox extends Composite {

	private static CheckboxPropertyUiBinder uiBinder = GWT.create(CheckboxPropertyUiBinder.class);

	interface CheckboxPropertyUiBinder extends UiBinder<Widget, CustomCheckbox> {
	}
	
	@UiField
	CheckBox checkBox;

	public CustomCheckbox() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setText(String text) {
		checkBox.setText(text);
	}
	
	public void setValue(boolean value) {
		checkBox.setValue(value);
	}
	
	public void setEnabled(boolean enabled) {
		checkBox.setEnabled(enabled);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return checkBox.addClickHandler(handler);
	}

	public boolean getValue() {
		return checkBox.getValue();
	}
}
