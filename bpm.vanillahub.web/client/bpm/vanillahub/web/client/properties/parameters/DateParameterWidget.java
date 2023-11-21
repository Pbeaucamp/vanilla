package bpm.vanillahub.web.client.properties.parameters;

import java.util.Date;

import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.parameters.HasParameterWidget;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class DateParameterWidget extends Composite implements HasParameterWidget {

	private static DateParameterWidgetUiBinder uiBinder = GWT.create(DateParameterWidgetUiBinder.class);

	interface DateParameterWidgetUiBinder extends UiBinder<Widget, DateParameterWidget> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	RadioButton btnDate, btnVariable;

	@UiField
	DateBox dateBox;

	@UiField(provided = true)
	TimeBox timeBox;

	@UiField(provided = true)
	VariableTextHolderBox txtVariable;

	private boolean useDate = true;

	public DateParameterWidget(WebServiceParameter param, ResourceManager resourceManager) {
		VariableString paramValue = param.getParameterValue() != null && param.getParameterValue() instanceof VariableString ? (VariableString) param.getParameterValue() : null;

		timeBox = new TimeBox(new Date());
		txtVariable = new VariableTextHolderBox(paramValue, Labels.lblCnst.UseVariable(), null, resourceManager.getVariables(), resourceManager.getParameters());
		initWidget(uiBinder.createAndBindUi(this));

		String radioName = "DateChoice_" + new Object().hashCode();
		btnDate.setName(radioName);
		btnVariable.setName(radioName);

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);

		dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBox.getDatePicker().setYearArrowsVisible(true);
		dateBox.setValue(new Date());

		if(paramValue != null) {
			useDate = false;
			btnVariable.setValue(true);
			
			txtVariable.setText(paramValue.getStringForTextbox());
		}
		else if (param.getParameterValue() != null && param.getParameterValue() instanceof Date) {
			useDate = true;
			
			Date date = (Date) param.getParameterValue();
			dateBox.setValue(date);
			timeBox.setValue(date);
		}

		updateUI(useDate);
	}

	@UiHandler("btnDate")
	public void onRadioDateClick(ClickEvent event) {
		this.useDate = true;
		updateUI(useDate);
	}

	@UiHandler("btnVariable")
	public void onRadioVariableClick(ClickEvent event) {
		this.useDate = false;
		updateUI(useDate);
	}

	private void updateUI(boolean useDate) {
		dateBox.setEnabled(useDate);
		timeBox.setEnabled(useDate);
		txtVariable.setEnabled(!useDate);
	}

	@Override
	public Object getParameterValue() {
		if (useDate) {
			Date beginDate = dateBox.getValue();
			int hours = timeBox.getSelectedHours();
			int minutes = timeBox.getSelectedMinutes();

			beginDate.setTime(beginDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);
			return beginDate;
		}
		else {
			return txtVariable.getVariableString();
		}
	}
}
