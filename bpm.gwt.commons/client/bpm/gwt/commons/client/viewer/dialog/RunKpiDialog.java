package bpm.gwt.commons.client.viewer.dialog;

import java.util.Date;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.viewer.KpiThemeViewer;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class RunKpiDialog extends AbstractDialogBox {

	private DateTimeFormat dfShort = DateTimeFormat.getFormat("yyyy-MM-dd");
	private DateTimeFormat df = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
	private DateTimeFormat yearDf = DateTimeFormat.getFormat("yyyy");
	private DateTimeFormat monthDf = DateTimeFormat.getFormat("MM");
	private DateTimeFormat dayDf = DateTimeFormat.getFormat("dd");
	
	private static RunKpiDialogUiBinder uiBinder = GWT.create(RunKpiDialogUiBinder.class);

	interface RunKpiDialogUiBinder extends UiBinder<Widget, RunKpiDialog> {
	}

	@UiField
	DatePicker dateSelectionPicker;
	
	@UiField
	ListBox lstJumpToYear;
	
	@UiField
	TimeBox timeBox;

	private KpiThemeViewer viewer;
	private LaunchReportInformations itemInfo;

	public RunKpiDialog(KpiThemeViewer viewer, LaunchReportInformations itemInfo) {
		super(LabelsConstants.lblCnst.RunKpiTheme(), false, true);
		this.viewer = viewer;
		this.itemInfo = itemInfo;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		dateSelectionPicker.setValue(new Date());
		dateSelectionPicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				fillListYear();
			}
		});
		
		lstJumpToYear.addChangeHandler(new ChangeHandler() {		
			@Override
			public void onChange(ChangeEvent event) {
				String year = lstJumpToYear.getValue(lstJumpToYear.getSelectedIndex());
				String month = getMonth();
				String day = getDay();
				
				Date date = dfShort.parse(year + "-" + month + "-" + day);
				
				dateSelectionPicker.setValue(date);
				dateSelectionPicker.setCurrentMonth(date);
				
				fillListYear();
			}
		});
		timeBox.setValue(new Date());
		
		fillListYear();
	}

	private void fillListYear() {
		int year = Integer.parseInt(getYear());
		
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
	
	private String getYear() {
		return yearDf.format(dateSelectionPicker.getValue());
	}
	
	private String getMonth() {
		return monthDf.format(dateSelectionPicker.getValue());
	}
	
	private String getDay() {
		return dayDf.format(dateSelectionPicker.getValue());
	}

	private Date getSelectedDate() {
		Date beginDate = dateSelectionPicker.getValue();
		int hours = timeBox.getSelectedHours();
		int minutes = timeBox.getSelectedMinutes();
		
		beginDate.setTime(beginDate.getTime() + (hours - 12) * TimeBox.HOURS + minutes * TimeBox.MINUTES);
		return beginDate;
	}
	
	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String selectedDate = df.format(getSelectedDate());
			viewer.setSelectedDate(selectedDate);
			
			viewer.runItem(itemInfo);
			RunKpiDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			RunKpiDialog.this.hide();
		}
	};
}
