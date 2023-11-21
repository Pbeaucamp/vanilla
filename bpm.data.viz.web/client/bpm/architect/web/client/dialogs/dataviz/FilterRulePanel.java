package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FilterRulePanel extends Composite implements IRulePanel {

	private static FilterRulePanelUiBinder uiBinder = GWT.create(FilterRulePanelUiBinder.class);

	interface FilterRulePanelUiBinder extends UiBinder<Widget, FilterRulePanel> {}
	
	@UiField
	LabelTextArea txtFilter;
	private PreparationRuleFilter rule;
	
	public FilterRulePanel(PreparationRuleFilter rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		
		if(rule.getFilter() != null && !rule.getFilter().isEmpty()) {
			txtFilter.setText(rule.getFilter());
		}
		
	}

	@Override
	public PreparationRule getRule() {
		rule.setFilter(txtFilter.getText());
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}
}
