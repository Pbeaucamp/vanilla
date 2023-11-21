package bpm.architect.web.client.panels;

import bpm.data.viz.core.preparation.PreparationRule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizAppliedRulesPanel extends Composite {

	private static DataVizAppliedRulesPanelUiBinder uiBinder = GWT.create(DataVizAppliedRulesPanelUiBinder.class);

	interface DataVizAppliedRulesPanelUiBinder extends UiBinder<Widget, DataVizAppliedRulesPanel> {}

	@UiField
	HTMLPanel mainPanel;
	
	public DataVizAppliedRulesPanel(DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		
		for(PreparationRule rule : parent.getDataPreparation().getRules()) {
			DataVizRuleDisplayPanel pan = new DataVizRuleDisplayPanel(rule, parent);
			mainPanel.add(pan);
		}
		
	}

}
