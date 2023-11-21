package bpm.gwt.workflow.commons.client.dialog;

import java.util.Date;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.properties.VariableResourceProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class DateDefinitionDialog extends AbstractDialogBox {

	private static DateDefinitionDialogUiBinder uiBinder = GWT.create(DateDefinitionDialogUiBinder.class);

	interface DateDefinitionDialogUiBinder extends UiBinder<Widget, DateDefinitionDialog> {
	}

	@UiField
	DateBox dateBoxBegin;

	@UiField(provided = true)
	TimeBox timeStart;

	@UiField
	ListBox lstType;

	@UiField
	RadioButton radioToday, radioDefineDate;

	@UiField
	RadioButton radioAdd, radioWithdraw;

	@UiField
	TextHolderBox txtValue;

	@UiField
	Label lblIntervalError;

	private VariableResourceProperties parentProperties;

	public DateDefinitionDialog(VariableResourceProperties parentProperties) {
		super(LabelsCommon.lblCnst.ScriptDate(), false, true);
		timeStart = new TimeBox(new Date());
		this.parentProperties = parentProperties;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		lstType.addItem(LabelsCommon.lblCnst.Day());
		lstType.addItem(LabelsCommon.lblCnst.Month());
		lstType.addItem(LabelsCommon.lblCnst.Year());

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		dateBoxBegin.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBoxBegin.getDatePicker().setYearArrowsVisible(true);
		dateBoxBegin.setValue(new Date());

		updateUi(true);
	}

	private void updateUi(boolean today) {
		timeStart.setEnabled(!today);
		dateBoxBegin.setEnabled(!today);
	}

	@UiHandler("radioToday")
	public void onTodayClick(ClickEvent event) {
		updateUi(true);
	}

	@UiHandler("radioDefineDate")
	public void onDefineDateClick(ClickEvent event) {
		updateUi(false);
	}

	@UiHandler("txtValue")
	public void onTxtValueChange(ValueChangeEvent<String> event) {
		String value = event.getValue();
		if (value.isEmpty()) {
			txtValue.setText("0");
			lblIntervalError.setText(LabelsCommon.lblCnst.IntervalNotValidWithZero());
		}
		else {
			try {
				Integer.parseInt(value);
				lblIntervalError.setText("");
			} catch (Exception e) {
				txtValue.setText("0");
				lblIntervalError.setText(LabelsCommon.lblCnst.IntervalNotValidWithZero());
			}
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Date beginDate = null;
			if (radioDefineDate.getValue()) {
				beginDate = dateBoxBegin.getValue();
				int hours = timeStart.getSelectedHours();
				int minutes = timeStart.getSelectedMinutes();
				beginDate.setTime(beginDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);
			}

			String operator = radioWithdraw.getValue() ? "-" : "+";

			int type = lstType.getSelectedIndex();
			int value = Integer.parseInt(txtValue.getText());

			String script = createScript(beginDate, operator, value, type);
			parentProperties.setScriptDate(script);

			DateDefinitionDialog.this.hide();
		}
	};

	private String createScript(Date date, String operator, int value, int type) {
		StringBuffer buf = new StringBuffer();
		if(date == null) {
			buf.append("var date = new Date();\n");
		}
		else {
			buf.append("var date = new Date(" + getYear(date) + "," + getMonth(date) + "," + getDay(date) + "," + getHour(date) + "," + getMin(date) + "," + getSec(date) + ");\n");
		}
		if (value > 0) {
			if (type == 0) {
				buf.append("date.setDate(date.getDate() " + operator + " " + value + ");\n");
			}
			else if (type == 1) {
				buf.append("var day = date.getDate();\n");
				buf.append("var month = date.getMonth() " + operator + " " + value + ";\n");
				buf.append("if (month < 0) {\n");
				buf.append("	month = 11 + month;\n");
				buf.append("}\n");
				buf.append("date.setMonth(date.getMonth() " + operator + " " + value + ");\n");
				buf.append("if ((day > 28 && month == 1) || (day == 31 && (month == 3 || month == 5 || month == 8 || month == 10))) {\n");
				buf.append("	date.setDate(0);\n");
				buf.append("}\n");
			}
			else if (type == 2) {
				buf.append("date.setYear(dateObj.getYear() " + operator + " " + value + ")\n");
			}
		}
		buf.append("date;");
		return buf.toString();
	}

	private String getYear(Date date) {
		return DateTimeFormat.getFormat("yyyy").format(date);
	}

	private String getMonth(Date date) {
		return String.valueOf(Integer.parseInt(DateTimeFormat.getFormat("MM").format(date)) - 1);
	}

	private String getDay(Date date) {
		return DateTimeFormat.getFormat("dd").format(date);
	}

	private String getHour(Date date) {
		return DateTimeFormat.getFormat("HH").format(date);
	}

	private String getMin(Date date) {
		return DateTimeFormat.getFormat("mm").format(date);
	}

	private String getSec(Date date) {
		return DateTimeFormat.getFormat("ss").format(date);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			DateDefinitionDialog.this.hide();
		}
	};
}
