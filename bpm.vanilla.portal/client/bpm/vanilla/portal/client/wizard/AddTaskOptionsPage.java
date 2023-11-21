package bpm.vanilla.portal.client.wizard;

import java.util.Date;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail.Period;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class AddTaskOptionsPage extends Composite implements IGwtPage {

	private static AddTaskOptionsPageUiBinder uiBinder = GWT.create(AddTaskOptionsPageUiBinder.class);

	interface AddTaskOptionsPageUiBinder extends UiBinder<Widget, AddTaskOptionsPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	DateBox dateBoxBegin, dateBoxStop;

	@UiField(provided = true)
	TimeBox timeStart;

	@UiField(provided = true)
	TimeBox timeStop;

	@UiField
	ListBox lstPeriod;

	@UiField
	TextHolderBox txtInterval;

	@UiField
	Label lblIntervalError;

	@UiField
	CheckBox checkStopDate, checkRepeat;

	private int index;

	public AddTaskOptionsPage(IGwtWizard parent, int index) {
		timeStart = new TimeBox(new Date());
		timeStop = new TimeBox(new Date());
		initWidget(uiBinder.createAndBindUi(this));
		this.index = index;

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		dateBoxBegin.setFormat(new DateBox.DefaultFormat(dateFormat));
		// dateBoxBegin.getDatePicker().setYearArrowsVisible(true);
		dateBoxBegin.setValue(new Date());

		dateBoxStop.setFormat(new DateBox.DefaultFormat(dateFormat));
		// dateBoxStop.getDatePicker().setYearArrowsVisible(true);
		dateBoxStop.setValue(new Date());

		fillPeriodList(null);

		checkRepeat.setValue(true);
		checkStopDate.setValue(false);

		updateUi(true);
		updateDateStop(false);
	}

	private void fillPeriodList(JobDetail detail) {
		lstPeriod.clear();

		int index = 0;
		int selectedIndex = -1;
		for (Period period : Period.values()) {
			if (detail != null && detail.getPeriod() != null && detail.getPeriod() == period) {
				selectedIndex = index;
			}

			switch (period) {
			case YEAR:
				lstPeriod.addItem(ToolsGWT.lblCnst.Yearly(), String.valueOf(period.getType()));
				break;
			case MONTH:
				lstPeriod.addItem(ToolsGWT.lblCnst.Monthly(), String.valueOf(period.getType()));
				break;
			case WEEK:
				lstPeriod.addItem(ToolsGWT.lblCnst.Weekly(), String.valueOf(period.getType()));
				break;
			case DAY:
				lstPeriod.addItem(ToolsGWT.lblCnst.Daily(), String.valueOf(period.getType()));
				break;
			case HOUR:
				lstPeriod.addItem(ToolsGWT.lblCnst.Hourly(), String.valueOf(period.getType()));
				break;
			case MINUTE:
				lstPeriod.addItem(ToolsGWT.lblCnst.Minute(), String.valueOf(period.getType()));
				break;
			default:
				break;
			}

			index++;
		}

		if (selectedIndex != -1) {
			lstPeriod.setSelectedIndex(selectedIndex);
		}
	}

	public void setDetail(JobDetail detail) {
		dateBoxBegin.setValue(detail.getBeginDate() != null ? detail.getBeginDate() : new Date());
		timeStart.setValue(detail.getBeginDate() != null ? detail.getBeginDate() : new Date());
		fillPeriodList(detail);
		txtInterval.setText(detail.getInterval() > 0 ? String.valueOf(detail.getInterval()) : "0");

		checkStopDate.setValue(detail.getStopDate() != null);
		dateBoxStop.setVisible(detail.getStopDate() != null);
		dateBoxStop.setValue(detail.getStopDate() != null ? detail.getStopDate() : new Date());
		timeStop.setVisible(detail.getStopDate() != null);
		timeStop.setValue(detail.getStopDate() != null ? detail.getStopDate() : new Date());
	}

	private void updateUi(boolean repeat) {
		lstPeriod.setEnabled(repeat);
		txtInterval.setEnabled(repeat);
		checkStopDate.setEnabled(repeat);
		dateBoxStop.setEnabled(repeat);
		timeStop.setEnabled(repeat);
	}

	private void updateDateStop(boolean visible) {
		dateBoxStop.setVisible(visible);
		timeStop.setVisible(visible);
	}

	@UiHandler("checkRepeat")
	public void onRepeatClick(ValueChangeEvent<Boolean> event) {
		updateUi(event.getValue());
	}

	@UiHandler("txtInterval")
	public void onIntervalChange(ValueChangeEvent<String> event) {
		String value = event.getValue();
		if (value.isEmpty()) {
			txtInterval.setText("1");
			lblIntervalError.setText(ToolsGWT.lblCnst.IntervalNotValid());
		}
		else {
			try {
				Integer.parseInt(value);
				lblIntervalError.setText("");
			} catch (Exception e) {
				txtInterval.setText("1");
				lblIntervalError.setText(ToolsGWT.lblCnst.IntervalNotValid());
			}
		}
	}

	@UiHandler("checkStopDate")
	public void onStopDateClick(ValueChangeEvent<Boolean> event) {
		updateDateStop(event.getValue());
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	public Date getBeginDate() {
		Date beginDate = dateBoxBegin.getValue();
		int hours = timeStart.getSelectedHours();
		int minutes = timeStart.getSelectedMinutes();

		beginDate.setTime(beginDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);
		return beginDate;
	}

	public Date getStopDate() {
		Date stopDate = null;
		if (checkStopDate.getValue()) {
			stopDate = dateBoxStop.getValue();
			int hours = timeStop.getSelectedHours();
			int minutes = timeStop.getSelectedMinutes();

			stopDate.setTime(stopDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);
		}
		return stopDate;
	}

	public Period getPeriod() {
		if (checkRepeat.getValue()) {
			int periodType = Integer.parseInt(lstPeriod.getValue(lstPeriod.getSelectedIndex()));
			return Period.valueOf(periodType);
		}
		return null;
	}

	public int getInterval() {
		if (checkRepeat.getValue()) {
			return Integer.parseInt(txtInterval.getText());
		}
		return -1;
	}
}
