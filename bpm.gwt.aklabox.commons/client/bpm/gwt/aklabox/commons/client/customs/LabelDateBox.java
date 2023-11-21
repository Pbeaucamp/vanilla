package bpm.gwt.aklabox.commons.client.customs;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;

public class LabelDateBox extends Composite {
	
	private static final String DEFAULT_FORMAT = "dd/MM/yyyy";

	private static LabelTextBoxUiBinder uiBinder = GWT.create(LabelTextBoxUiBinder.class);

	interface LabelTextBoxUiBinder extends UiBinder<Widget, LabelDateBox> {
	}

	@UiField
	Label label;

	@UiField
	DateBox datebox;

	public LabelDateBox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		datebox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(DEFAULT_FORMAT)));
	}
	
	public void setFormat(Format format) {
		datebox.setFormat(format);
	}

	public void setValue(Date date) {
		datebox.setValue(date);
	}
	
	public Date getValue() {
		return datebox.getValue();
	}

	public void clear() {
		datebox.setValue(null);
	}

	public void setEnabled(boolean enabled) {
		datebox.setEnabled(enabled);
	}

	public void setLabel(String lbl) {
		label.setText(lbl);
	}

	public String getLabel() {
		return label.getText();
	}
	
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> changeHandler) {
		return datebox.addValueChangeHandler(changeHandler);
	}
}
