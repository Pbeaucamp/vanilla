package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MinMaxRulePanel extends Composite implements IRulePanel {

	private static MinMaxRulePanelUiBinder uiBinder = GWT.create(MinMaxRulePanelUiBinder.class);

	interface MinMaxRulePanelUiBinder extends UiBinder<Widget, MinMaxRulePanel> {}

	@UiField
	LabelTextBox txtValue;
	
	private PreparationRuleMinMax rule;
	
	public MinMaxRulePanel(PreparationRuleMinMax rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;	
		txtValue.setText(String.valueOf(rule.getValue()));	
	}
	
	@Override
	public PreparationRule getRule() {
		rule.setValue(Double.parseDouble(txtValue.getText()));
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}

}
