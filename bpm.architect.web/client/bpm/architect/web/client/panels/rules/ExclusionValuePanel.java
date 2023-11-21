package bpm.architect.web.client.panels.rules;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.resources.RuleExclusionValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ExclusionValuePanel extends Composite {

	private static ExclusionValuePanelUiBinder uiBinder = GWT.create(ExclusionValuePanelUiBinder.class);

	interface ExclusionValuePanelUiBinder extends UiBinder<Widget, ExclusionValuePanel> {
	}
	
	@UiField
	CheckBox chkExclusion;
	
	@UiField
	LabelTextBox txtDefaultValue;

	public ExclusionValuePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void loadPanel(RuleExclusionValue rule) {
		if (rule != null) {
			chkExclusion.setValue(rule.hasExclusionValue());
			txtDefaultValue.setText(rule.getExclusionValue() != null ? rule.getExclusionValue() : "");
		}
	}
	
	public boolean hasExclusionValue() {
		return chkExclusion.getValue();
	}
	
	public String getExclusionValue() {
		return hasExclusionValue() ? txtDefaultValue.getText() : null;
	}

}
