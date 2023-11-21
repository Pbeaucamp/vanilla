package bpm.architect.web.client.panels.rules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;

public class DefinitionRulePanel extends Composite {

	private static DefinitionRuleValuePanelUiBinder uiBinder = GWT.create(DefinitionRuleValuePanelUiBinder.class);

	interface DefinitionRuleValuePanelUiBinder extends UiBinder<Widget, DefinitionRulePanel> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;
	
	@UiField
	CheckBox chkEnabled;

	public DefinitionRulePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setDefinition(String name, String description, boolean enabled) {
		txtName.setText(name != null ? name : "");
		txtDescription.setText(description != null ? description : "");
		chkEnabled.setValue(enabled);
	}
	
	public String getName() {
		return txtName.getText();
	}
	
	public String getDescription() {
		return txtDescription.getText();
	}
	
	public boolean isEnabled() {
		return chkEnabled.getValue();
	}
}
