package bpm.architect.web.client.panels;

import bpm.architect.web.client.dialogs.PreparationExportInLibreOfficeDailog;
import bpm.architect.web.client.dialogs.dataviz.AddCharRulePanel;
import bpm.architect.web.client.dialogs.dataviz.AffectationNombre;
import bpm.architect.web.client.dialogs.dataviz.CalcRulePanel;
import bpm.architect.web.client.dialogs.dataviz.FilterRulePanel;
import bpm.architect.web.client.dialogs.dataviz.FormatRulePanel;
import bpm.architect.web.client.dialogs.dataviz.GroupRulePanel;
import bpm.architect.web.client.dialogs.dataviz.IRulePanel;
import bpm.architect.web.client.dialogs.dataviz.MinMaxRulePanel;
import bpm.architect.web.client.dialogs.dataviz.RecodeRulePanel;
import bpm.architect.web.client.dialogs.dataviz.SortRulePanel;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.data.viz.core.preparation.PreparationRuleAffecter;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.data.viz.core.preparation.PreparationRuleGroup;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.data.viz.core.preparation.PreparationRuleSort;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertiesPanel extends Composite {

	private static PropertiesPanelUiBinder uiBinder = GWT.create(PropertiesPanelUiBinder.class);

	interface PropertiesPanelUiBinder extends UiBinder<Widget, PropertiesPanel> {}

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	ListBoxWithButton<DataColumn> lstColumn;
	
	@UiField
	CheckBox btnCol, btnColNew;
	
	@UiField
	LabelTextBox txtColName;
	
	@UiField
	SimplePanel rulePanel;
	
	private RuleType type;
	private PreparationRule rule;
	private IRulePanel ruleContentPanel;
	//private DataVizDataPanel dataPanel;
	private DataVizVisualPanel visualPanel;
	private DataPreparation dataPrep;
	private boolean confirm;
	private DataVizDesignPanel parent;


	public PropertiesPanel(PreparationRule rule, DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		
		this.type = rule.getType();
		this.rule = rule;
		this.visualPanel = this.parent.getDataPanel().getVisualPanel();
		this.dataPrep = this.parent.getDataPreparation();
		display();
	}
	
	public void display() {

		btnCol.setValue(true);
		btnCol.removeFromParent();
		
		if(rule.isMultiColumn()) {
			lstColumn.setLabel("Colonnes");
			lstColumn.setMultiple(true);
			lstColumn.setList(dataPrep.getDataset().getMetacolumns());
			lstColumn.getListBox().setVisibleItemCount(10);
		}
		else {
			lstColumn.setList(dataPrep.getDataset().getMetacolumns(), true);
		}
		if(!type.canCreateColumn()) {
			btnColNew.removeFromParent();
			txtColName.removeFromParent();
		}
		if(type == RuleType.CALC || type == RuleType.FILTER) {
			btnColNew.removeFromParent();
			lstColumn.removeFromParent();
		}
		
		
		if(rule.getColumns() != null) {
			for(DataColumn c : rule.getColumns()) {
				lstColumn.setSelectedObject(c);
			}
		}
		if(rule.isNewColumn()) {
			btnColNew.setValue(true);
			txtColName.setText(rule.getNewColumnName());
		}
		
		ruleContentPanel = getRulePanel();
		if(ruleContentPanel != null) {
			rulePanel.add((Widget) ruleContentPanel);
		}
	}
	
//	private static PreparationRule instanciateNewRule(RuleType type) {
//		PreparationRule r = new PreparationRule();
//		switch(type) {
//			case LOWER_CASE:
//				return new PreparationRule(RuleType.LOWER_CASE);
//			case UPPER_CASE:
//				return new PreparationRule(RuleType.UPPER_CASE);
//			case NORMALIZE:
//				return new PreparationRule(RuleType.NORMALIZE);
//			case DATE_TO_AGE:
//				return new PreparationRule(RuleType.DATE_TO_AGE);
//			case DEDOUBLON:
//				return new PreparationRule(RuleType.DEDOUBLON);
//			case RECODE:
//				return new PreparationRuleRecode();
//			case SORT:
//				return new PreparationRuleSort();
//			case ADD_CHAR:
//				return new PreparationRuleAddChar();
//			case FORMAT_NUMBER:
//				return new PreparationRuleFormat();
//			case MAX:
//				return new PreparationRuleMinMax(RuleType.MAX);
//			case MIN:
//				return new PreparationRuleMinMax(RuleType.MIN);
//			case ROUND:
//				return new PreparationRule(RuleType.ROUND);
//			case CALC:
//				return new PreparationRuleCalc();
//			case FILTER:
//				return new PreparationRuleFilter();
//			case GROUP:
//				return new PreparationRuleGroup();
//				
//			case AFFECTER:
//				return new PreparationRuleAffecter();
//			default:
//				break;
//		}
//		return r;
//	}
	
	private IRulePanel getRulePanel() {
		switch(type) {
			case LOWER_CASE:
			case UPPER_CASE:
			case NORMALIZE:
			case ROUND:
			case DATE_TO_AGE:
			case DEDOUBLON:
				return null;
			case RECODE:
				return new RecodeRulePanel((PreparationRuleRecode) rule);
			case SORT:
				return new SortRulePanel((PreparationRuleSort) rule);
			case ADD_CHAR:
				return new AddCharRulePanel((PreparationRuleAddChar) rule);
			case FORMAT_NUMBER:
				return new FormatRulePanel((PreparationRuleFormat) rule);
			case MAX:
			case MIN:
				return new MinMaxRulePanel((PreparationRuleMinMax) rule);
			case CALC:
				return new CalcRulePanel((PreparationRuleCalc) rule);
			case FILTER:
				return new FilterRulePanel((PreparationRuleFilter) rule);
			case GROUP:
				return new GroupRulePanel((PreparationRuleGroup) rule, this.parent.getDataPanel().getLastResult());
				
			case AFFECTER:
				return new AffectationNombre((PreparationRuleAffecter) rule, this.parent.getDataPanel().getLastResult());
			case LIBREOFFICE:
				return new PreparationExportInLibreOfficeDailog(dataPrep, parent.getInfoUser());
			default:
				break;
		}
		return null;
	}

	
	public void refresh() {
		if(ruleContentPanel != null) {
			rule = ruleContentPanel.getRule();
		}
		rule.setType(type);
		
		if(type == RuleType.SORT) {
			
			
			int index = dataPrep.getRules().indexOf(rule);
			if(index > 0) {
				PreparationRuleSort rule = (PreparationRuleSort) dataPrep.getRules().get(index);
				if(!rule.getColumns().contains(lstColumn.getSelectedObject())) {
					rule.getColumns().add(lstColumn.getSelectedObject());
				}
			}
			else {
				rule.setColumn(lstColumn.getSelectedObject());
				dataPrep.addRule(rule);
				//this.visualPanel.addBox(rule);
			}
		}
		else {
		
			if(btnCol.getValue()) {
				try {
					rule.setColumns(lstColumn.getSelectedItems());
				} catch(Exception e) {
				}
				rule.setRowNumber(-1);
			}
			rule.setNewColumn(btnColNew.getValue());
			rule.setNewColumnName(txtColName.getText());
			
			if(!dataPrep.getRules().contains(rule)) {
				dataPrep.addRule(rule);
				//this.visualPanel.addBox(rule);
			}
		}
		
		
		this.confirm = true;
	}
	
	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	public RuleType getType() {
		return type;
	}


	public void setType(RuleType type) {
		this.type = type;
	}


	public PreparationRule getRule() {
		return rule;
	}


	public void setRule(PreparationRule rule) {
		this.rule = rule;
	}
}
