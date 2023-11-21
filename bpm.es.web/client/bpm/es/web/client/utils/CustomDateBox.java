package bpm.es.web.client.utils;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class CustomDateBox extends Composite {

	private static CustomDateBoxUiBinder uiBinder = GWT.create(CustomDateBoxUiBinder.class);

	interface CustomDateBoxUiBinder extends UiBinder<Widget, CustomDateBox> {
	}
	
	@UiField
	Label label;
	
	@UiField(provided=true)
	DateBox dateBox;

	public CustomDateBox() {
		dateBox = new DateBox();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public CustomDateBox(DateTimeFormat dateFormat, Date selectedDate) {
		dateBox = new DateBox(new DatePicker(), selectedDate, new DateBox.DefaultFormat(dateFormat));
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	@UiHandler("btnToday")
	public void onToday(ClickEvent event) {
		dateBox.setValue(new Date());
	}
}
