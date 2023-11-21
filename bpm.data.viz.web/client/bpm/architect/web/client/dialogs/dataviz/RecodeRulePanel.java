package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RecodeRulePanel extends Composite implements IRulePanel {

	private static RecodeRulePanelUiBinder uiBinder = GWT.create(RecodeRulePanelUiBinder.class);

	interface RecodeRulePanelUiBinder extends UiBinder<Widget, RecodeRulePanel> {}

	@UiField
	LabelTextArea txtOrigin, txtResult;
	private PreparationRuleRecode rule;
	
	public RecodeRulePanel(PreparationRuleRecode rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		
		if(rule.getOriginFormula() != null && !rule.getOriginFormula().isEmpty()) {
			txtOrigin.setText(rule.getOriginFormula());
		}
		if(rule.getResultFormula() != null && !rule.getResultFormula().isEmpty()) {
			txtResult.setText(rule.getResultFormula());
		}
		
	}

	@Override
	public PreparationRule getRule() {
		rule.setOriginFormula(txtOrigin.getText());
		rule.setResultFormula(txtResult.getText());
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}

}
