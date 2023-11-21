package bpm.architect.web.client.dialogs.dataviz;

import java.util.ArrayList;
import java.util.List;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleSort;
import bpm.data.viz.core.preparation.PreparationRuleSort.SortType;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SortRulePanel extends Composite implements IRulePanel {

	private static SortRulePanelUiBinder uiBinder = GWT.create(SortRulePanelUiBinder.class);

	interface SortRulePanelUiBinder extends UiBinder<Widget, SortRulePanel> {}

	@UiField
	ListBoxWithButton<SortType> lstSortType;

	private PreparationRuleSort rule;
	
	public SortRulePanel(PreparationRuleSort rule) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule  = rule;
		
		List<SortType> items = new ArrayList<>();
		items.add(SortType.ASC);
		items.add(SortType.DESC);
		
		lstSortType.setList(items);
		
		if(rule.getSortType() != null) {
			lstSortType.setSelectedObject(rule.getSortType());
		}
	}



	@Override
	public PreparationRule getRule() {
		rule.setSortType(lstSortType.getSelectedObject());
		return rule;
	}



	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}

}
