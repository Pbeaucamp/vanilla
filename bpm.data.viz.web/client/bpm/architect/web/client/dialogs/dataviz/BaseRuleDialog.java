package bpm.architect.web.client.dialogs.dataviz;

import bpm.architect.web.client.panels.DataVizDataPanel;
import bpm.architect.web.client.utils.ToolHelper;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.data.viz.core.preparation.PreparationRuleAffecter;
import bpm.data.viz.core.preparation.PreparationRuleSort.SortType;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.data.viz.core.preparation.PreparationRuleGroup;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.data.viz.core.preparation.PreparationRuleSort;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class BaseRuleDialog extends AbstractDialogBox {

	private static BaseRuleDialogUiBinder uiBinder = GWT.create(BaseRuleDialogUiBinder.class);

	interface BaseRuleDialogUiBinder extends UiBinder<Widget, BaseRuleDialog> {}

	@UiField
	CheckBox btnCol, btnColNew;
	
	@UiField
	ListBoxWithButton<DataColumn> lstColumn;
	
	@UiField
	LabelTextBox txtColName;
	
	@UiField
	SimplePanel rulePanel;
	
	private boolean confirm;

	private DataPreparation dataPrep;
	private RuleType type;
	private DataVizDataPanel dataPanel;
	
	private IRulePanel ruleContentPanel;
	private PreparationRule rule;
	
	public BaseRuleDialog(DataPreparation dataPrep, RuleType type, DataVizDataPanel dataPanel) {
		this(dataPrep, instanciateNewRule(type), dataPanel);
	}

	public BaseRuleDialog(DataPreparation dataPrep, PreparationRule rule, DataVizDataPanel dataPanel) {
		super(getDialogHeader(rule), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.dataPrep = dataPrep;
		this.rule = rule;
		this.type = rule.getType();
		this.dataPanel = dataPanel;
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
		
 		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
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
	
	private static String getDialogHeader(PreparationRule rule) {
		return ToolHelper.getLabel(rule.getType());
	}

	private static PreparationRule instanciateNewRule(RuleType type) {
		PreparationRule r = new PreparationRule();
		switch(type) {
			case LOWER_CASE:
				return new PreparationRule(RuleType.LOWER_CASE);
			case UPPER_CASE:
				return new PreparationRule(RuleType.UPPER_CASE);
			case NORMALIZE:
				return new PreparationRule(RuleType.NORMALIZE);
			case DATE_TO_AGE:
				return new PreparationRule(RuleType.DATE_TO_AGE);
			case DEDOUBLON:
				return new PreparationRule(RuleType.DEDOUBLON);
			case RECODE:
				return new PreparationRuleRecode();
			case SORT:
				return new PreparationRuleSort();
			case ADD_CHAR:
				return new PreparationRuleAddChar();
			case FORMAT_NUMBER:
				return new PreparationRuleFormat();
			case MAX:
				return new PreparationRuleMinMax(RuleType.MAX);
			case MIN:
				return new PreparationRuleMinMax(RuleType.MIN);
			case ROUND:
				return new PreparationRule(RuleType.ROUND);
			case CALC:
				return new PreparationRuleCalc();
			case FILTER:
				return new PreparationRuleFilter();
			case GROUP:
				return new PreparationRuleGroup();
				
			case AFFECTER:
				return new PreparationRuleAffecter();
			default:
				break;
		}
		return r;
	}
	
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
				return new GroupRulePanel((PreparationRuleGroup) rule, dataPanel.getLastResult());
				
			case AFFECTER:
				return new AffectationNombre((PreparationRuleAffecter) rule, dataPanel.getLastResult());
				
			default:
				break;
		}
		return null;
	}

	@UiHandler("btnCol")
	public void onCol(ClickEvent event) {
//		lstColumn.setEnabled(true);
	}
	
	@UiHandler("btnColNew")
	public void onColNew(ClickEvent event) {
//		lstColumn.setEnabled(false);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
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
				}
			}
			
			confirm = true;
			hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}
	
	@UiHandler("lstColumn")
	public void onColumnSelection(ChangeEvent event) {
		if(ruleContentPanel != null) {
			ruleContentPanel.changeColumnSelection(lstColumn.getSelectedObject());
		}
	}
}
