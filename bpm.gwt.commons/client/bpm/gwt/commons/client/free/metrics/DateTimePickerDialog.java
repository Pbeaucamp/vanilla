package bpm.gwt.commons.client.free.metrics;

import java.util.Date;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTimePickerDialog extends AbstractDialogBox {

	private static DateTimePickerDialogUiBinder uiBinder = GWT.create(DateTimePickerDialogUiBinder.class);

	interface DateTimePickerDialogUiBinder extends UiBinder<Widget, DateTimePickerDialog> {
	}

	@UiField
	DatePicker datePicker;
	
	@UiField
	ListBox lstJumpToYear;
	
	@UiField
	TimeBox timeBox;
	
	private Button okButton;
	
	public DateTimePickerDialog(Date initialDate) {
		super(LabelsConstants.lblCnst.dateSelection(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
//		increaseZIndex(750);
		
		okButton = createButton("Ok", new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				DateTimePickerDialog.this.hide();
			}
		});
		
		datePicker.setValue(initialDate);
		datePicker.setCurrentMonth(initialDate);
		
		fillListYear();
		
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				fillListYear();
			}
		});
		
		lstJumpToYear.addChangeHandler(new ChangeHandler() {		
			@Override
			public void onChange(ChangeEvent event) {
				Date date = datePicker.getValue();
				
				date.setYear(Integer.parseInt(lstJumpToYear.getValue(lstJumpToYear.getSelectedIndex())) - 1900);
				datePicker.setValue(date);
				datePicker.setCurrentMonth(date);
				
				fillListYear();
			}
		});
	}
	
	public DateTimePickerDialog(Date initialDate, ClickHandler closeHandler) {
		this(initialDate);
		okButton.addClickHandler(closeHandler);
	}

	private void fillListYear() {
		int year = datePicker.getValue().getYear() + 1900;
		
		lstJumpToYear.clear();
		int index = 0;
		for(int i = (year - 5) ; i < (year + 6) ; i++) {
			lstJumpToYear.addItem(i + "", i + "");
			if(i == year) {
				lstJumpToYear.setSelectedIndex(index);
			}
			index++;
		}
		
	}
	
	public Date getSelectedDate() {
		Date date = datePicker.getValue();
		
		int hour = timeBox.getSelectedHours();
		int minute = timeBox.getSelectedMinutes();
		
		date.setHours(hour);
		date.setMinutes(minute);
		
		return date;
	}

}
