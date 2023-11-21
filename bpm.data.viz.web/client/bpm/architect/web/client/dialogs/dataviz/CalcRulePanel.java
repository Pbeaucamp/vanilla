package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CalcRulePanel extends Composite implements IRulePanel {

	private static CalcRulePanelUiBinder uiBinder = GWT.create(CalcRulePanelUiBinder.class);

	interface CalcRulePanelUiBinder extends UiBinder<Widget, CalcRulePanel> {}

	@UiField
	LabelTextArea txtCalc;
	private PreparationRuleCalc rule;
	
	public CalcRulePanel(PreparationRuleCalc rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		
		if(rule.getFormula() != null && !rule.getFormula().isEmpty()) {
			txtCalc.setText(rule.getFormula());
		}
		
	}

	@Override
	public PreparationRule getRule() {
		rule.setFormula(txtCalc.getText());
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}

}
