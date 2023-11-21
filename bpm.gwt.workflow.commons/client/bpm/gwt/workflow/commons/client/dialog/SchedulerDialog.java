package bpm.gwt.workflow.commons.client.dialog;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.IWorkflowManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Schedule.Period;
import bpm.workflow.commons.beans.Workflow;

public class SchedulerDialog extends AbstractDialogBox {

	private static ConnectionAklaboxDialogUiBinder uiBinder = GWT.create(ConnectionAklaboxDialogUiBinder.class);

	interface ConnectionAklaboxDialogUiBinder extends UiBinder<Widget, SchedulerDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	DateBox dateBoxBegin, dateBoxStop;

	@UiField(provided = true)
	TimeBox timeStart;

	@UiField
	ListBox lstPeriod;

	@UiField
	TextHolderBox txtInterval;

	@UiField
	Label lblIntervalError;

	@UiField
	CheckBox checkStopDate;

	@UiField
	SimplePanel panelParam;

	private IWorkflowManager workflowManager;
	private Workflow workflow;
	private Schedule schedule;

	private ParametersPanel parameterPanel;
	private boolean hasParam = false;

	public SchedulerDialog(IWorkflowManager workflowManager, Workflow workflow, List<Parameter> parameters, List<ListOfValues> lovs) {
		super(LabelsCommon.lblCnst.ScheduleWorkflow(), false, true);
		timeStart = new TimeBox(new Date());

		this.workflowManager = workflowManager;
		this.workflow = workflow;
		this.schedule = workflow.isScheduleDefine() ? workflow.getSchedule() : new Schedule();

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.OK(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		dateBoxBegin.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBoxBegin.getDatePicker().setYearArrowsVisible(true);

		dateBoxStop.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBoxStop.getDatePicker().setYearArrowsVisible(true);

		txtInterval.addValueChangeHandler(intervalHandler);
		checkStopDate.addValueChangeHandler(dateStopHandler);

		dateBoxBegin.setValue(schedule.getBeginDate() != null ? schedule.getBeginDate() : new Date());
		timeStart.setValue(schedule.getBeginDate() != null ? schedule.getBeginDate() : new Date());
		fillPeriodList(schedule);
		txtInterval.setText(schedule.getInterval() > 0 ? String.valueOf(schedule.getInterval()) : "");

		checkStopDate.setValue(schedule.getStopDate() != null);
		dateBoxStop.setVisible(schedule.getStopDate() != null);
		dateBoxStop.setValue(schedule.getStopDate() != null ? schedule.getStopDate() : new Date());

		if (parameters != null && !parameters.isEmpty()) {
			this.hasParam = true;
			parameterPanel = new ParametersPanel(parameters, lovs);
			panelParam.setWidget(parameterPanel);
		}
		else {
			this.hasParam = false;
			panelParam.setVisible(false);
		}
	}

	private void fillPeriodList(Schedule schedule) {
		int index = 0;
		int selectedIndex = -1;
		for (Period period : Period.values()) {
			if (schedule.getPeriod() != null && schedule.getPeriod() == period) {
				selectedIndex = index;
			}

			switch (period) {
			case YEAR:
				lstPeriod.addItem(LabelsCommon.lblCnst.Yearly(), String.valueOf(period.getType()));
				break;
			case MONTH:
				lstPeriod.addItem(LabelsCommon.lblCnst.Monthly(), String.valueOf(period.getType()));
				break;
			case WEEK:
				lstPeriod.addItem(LabelsCommon.lblCnst.Weekly(), String.valueOf(period.getType()));
				break;
			case DAY:
				lstPeriod.addItem(LabelsCommon.lblCnst.Daily(), String.valueOf(period.getType()));
				break;
			case HOUR:
				lstPeriod.addItem(LabelsCommon.lblCnst.Hourly(), String.valueOf(period.getType()));
				break;
			case MINUTE:
				lstPeriod.addItem(LabelsCommon.lblCnst.Minute(), String.valueOf(period.getType()));
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

	private ValueChangeHandler<String> intervalHandler = new ValueChangeHandler<String>() {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			String value = event.getValue();
			if (value.isEmpty()) {
				txtInterval.setText("1");
				lblIntervalError.setText(LabelsCommon.lblCnst.IntervalNotValid());
			}
			else {
				try {
					Integer.parseInt(value);
					lblIntervalError.setText("");
				} catch (Exception e) {
					txtInterval.setText("1");
					lblIntervalError.setText(LabelsCommon.lblCnst.IntervalNotValid());
				}
			}
		}
	};

	private ValueChangeHandler<Boolean> dateStopHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			dateBoxStop.setVisible(event.getValue());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Date beginDate = dateBoxBegin.getValue();
			int hours = timeStart.getSelectedHours();
			int minutes = timeStart.getSelectedMinutes();

			beginDate.setTime(beginDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);

			int periodType = Integer.parseInt(lstPeriod.getValue(lstPeriod.getSelectedIndex()));
			Period period = Period.valueOf(periodType);

			int interval = Integer.parseInt(txtInterval.getText());

			Date stopDate = null;
			if (checkStopDate.getValue()) {
				stopDate = dateBoxStop.getValue();
			}

			if (hasParam) {
				try {
					List<Parameter> parameters = parameterPanel.getParameters();
					schedule.setParameters(parameters);
				} catch (Exception e) {
					MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Error(), e.getMessage());
					return;
				}
			}

			schedule.setBeginDate(beginDate);
			schedule.setPeriod(period);
			schedule.setInterval(interval);
			schedule.setOn(true);
			schedule.setStopDate(stopDate);
			schedule.setWorkflowId(workflow.getId());

			workflow.setSchedule(schedule);

			workflowManager.updateWorkflow(workflow);

			hide();
		}
	};
}
