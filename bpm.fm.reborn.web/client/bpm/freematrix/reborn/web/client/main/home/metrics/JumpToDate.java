package bpm.freematrix.reborn.web.client.main.home.metrics;

import java.util.Date;

import bpm.freematrix.reborn.web.client.i18n.LabelConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class JumpToDate extends Composite {

	private static JumpToDateUiBinder uiBinder = GWT
			.create(JumpToDateUiBinder.class);

	interface JumpToDateUiBinder extends UiBinder<Widget, JumpToDate> {
	}

	@UiField ListBox lstJumpToYear	;
	private int year;
	private DatePicker datePicker;
	
	public JumpToDate(DatePicker datePicker, Date date) {
		initWidget(uiBinder.createAndBindUi(this));
		this.datePicker = datePicker;
		year = date.getYear() + 1900;
		
		generateTenYearsBefore(year);
		generateTenYearsFromNow(year);
	}
	
	public void generateTenYearsFromNow(int year){
		
		for(int i=1; i <= 5; i++){
			lstJumpToYear.addItem(String.valueOf(year++));
		}
		
	}
	
	public void generateTenYearsBefore(int year){
		lstJumpToYear.addItem(LabelConstants.lblCnst.jumpToYear());
		for(int i=5; i > 0; i--){
			lstJumpToYear.addItem(String.valueOf(year - i));
		}
	}
	
	@UiHandler("lstJumpToYear")
	void onSelecList(ChangeEvent e){
		
		int i = lstJumpToYear.getSelectedIndex();
		if(i!=0){
			Date date = new Date(Integer.parseInt(lstJumpToYear.getItemText(i)) - 1900, 2, 1);
			lstJumpToYear.clear();
			datePicker.setCurrentMonth(date);
		
			generateTenYearsBefore(date.getYear() + 1900);
			generateTenYearsFromNow(date.getYear() + 1900);
		}
	}

}
