package bpm.gwt.commons.client.custom;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class LabelDateBox extends Composite {

	private DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static LabelTextBoxUiBinder uiBinder = GWT.create(LabelTextBoxUiBinder.class);

	interface LabelTextBoxUiBinder extends UiBinder<Widget, LabelDateBox> {
	}

	@UiField
	Label label;

	@UiField
	DateBox datebox;

	public LabelDateBox() {
		initWidget(uiBinder.createAndBindUi(this));

		datebox.setFormat(new DateBox.DefaultFormat(df));
		datebox.getDatePicker().setYearAndMonthDropdownVisible(true);
		datebox.getDatePicker().setVisibleYearCount(200);
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
	
	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setPlaceHolder(String placeHolder) {
		label.setText(placeHolder);
	}

	public String getPlaceHolder() {
		return label.getText();
	}
}
