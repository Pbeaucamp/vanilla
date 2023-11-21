package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AddCharRulePanel extends Composite implements IRulePanel {

	private static AddCharRulePanelUiBinder uiBinder = GWT.create(AddCharRulePanelUiBinder.class);

	interface AddCharRulePanelUiBinder extends UiBinder<Widget, AddCharRulePanel> {}

	@UiField
	LabelTextBox txtChar;
	
	private PreparationRuleAddChar rule;
	
	public AddCharRulePanel(PreparationRuleAddChar rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		if(rule.getCharToAdd() != null && !rule.getCharToAdd().isEmpty()) {
			txtChar.setText(rule.getCharToAdd());
		}
	}

	@Override
	public PreparationRule getRule() {
		rule.setCharToAdd(txtChar.getText());
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}


}
