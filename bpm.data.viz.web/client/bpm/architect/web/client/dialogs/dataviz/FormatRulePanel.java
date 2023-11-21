package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FormatRulePanel extends Composite implements IRulePanel {

	private static FormatRulePanelUiBinder uiBinder = GWT.create(FormatRulePanelUiBinder.class);

	interface FormatRulePanelUiBinder extends UiBinder<Widget, FormatRulePanel> {}

	@UiField
	LabelTextBox txtPattern;
	
	private PreparationRuleFormat rule;
	
	public FormatRulePanel(PreparationRuleFormat rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		if(rule.getPattern() != null && !rule.getPattern().isEmpty()) {
			txtPattern.setText(rule.getPattern());
		}
	}
	
	@Override
	public PreparationRule getRule() {
		rule.setPattern(txtPattern.getText());
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}

}
