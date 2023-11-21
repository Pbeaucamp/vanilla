package bpm.gwt.workflow.commons.client.resources.properties;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.dialog.DateDefinitionDialog;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesAreaText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class VariableResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private Variable variable;

	private VariablePropertiesAreaText txtValue;
	private VariablePropertiesText txtPattern;
	private CheckBox checkDate;

	private boolean isNameValid = true;

	public VariableResourceProperties(NameChecker dialog, IResourceManager resourceManager, Variable myVariable) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, myVariable != null ? myVariable.getId() : 0, myVariable != null ? myVariable.getName() : LabelsCommon.lblCnst.Variable(), true, true);
		this.variable = myVariable != null ? myVariable : new Variable(LabelsCommon.lblCnst.Variable());

		setNameChecker(dialog);
		setNameChanger(this);

		txtValue = addVariableTextArea(LabelsCommon.lblCnst.Definition(), variable.getValueVS(), WidgetWidth.SMALL_PX, validHandler);

		addButton(LabelsCommon.lblCnst.HelpScriptDate(), scriptHandler);
		addHelper(LabelsCommon.lblCnst.HelpFileName() + " " + Variable.FILE_NAME_PATTERN);
		checkDate = addCheckbox(LabelsCommon.lblCnst.VariableIsDate(), variable.isDate(), dateHandler);

		txtPattern = addVariableText(LabelsCommon.lblCnst.DateFormat(), variable.getDatePatternVS(), WidgetWidth.SMALL_PX, null);
		addHelper(LabelsCommon.lblCnst.HelpDateFormat());

		checkName(getTxtName(), variable.getName());

		txtPattern.setEnabled(variable.isDate());
	}

	private ValueChangeHandler<Boolean> dateHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			variable.setDate(event.getValue());
			txtPattern.setEnabled(event.getValue());
		}
	};

	private ClickHandler scriptHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			DateDefinitionDialog dial = new DateDefinitionDialog(VariableResourceProperties.this);
			dial.center();
		}
	};

	public void setScriptDate(String script) {
		txtValue.setVariableText(script);
		
		checkDate.setValue(true);
		variable.setDate(true);
		txtPattern.setEnabled(true);
	}

	private ClickHandler validHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			VariableString value = txtValue.getVariableText();
			if (value.getStringForTextbox().isEmpty()) {
				txtValue.setTxtResult(LabelsCommon.lblCnst.ScriptEmpty(), true);
				return;
			}
			
			VariableString pattern = txtPattern.getVariableText();

			variable.setValue(value);
			variable.setDatePattern(pattern);

			showWaitPart(true);

			WorkflowsService.Connect.getInstance().validScript(variable, new GwtCallbackWrapper<CheckResult>(VariableResourceProperties.this, true) {

				@Override
				public void onSuccess(CheckResult result) {
					txtValue.setTxtResult(result.getValue(), result.isError());
				}
			}.getAsyncCallback());
		}
	};

	@Override
	public boolean isValid() {
		return isNameValid;
	}

	@Override
	public void changeName(final String value, boolean isValid) {
		this.isNameValid = isValid;

		showWaitPart(true);

		WorkflowsService.Connect.getInstance().clearName(value, new GwtCallbackWrapper<String>(this, true) {

			@Override
			public void onSuccess(String clearName) {
				variable.setName(clearName);
				getTxtName().setText(clearName);

				if (!clearName.equals(value)) {
					getTxtName().setTxtError(LabelsCommon.lblCnst.AutomaticNameChange());
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public Resource buildItem() {
		VariableString value = txtValue.getVariableText();
		VariableString pattern = txtPattern.getVariableText();

		variable.setValue(value);
		variable.setDatePattern(pattern);

		return variable;
	}
}
