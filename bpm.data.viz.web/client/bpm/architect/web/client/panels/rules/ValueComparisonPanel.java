package bpm.architect.web.client.panels.rules;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ValueComparisonPanel extends Composite implements IRulePanel {

	private static ValueComparisonPanelUiBinder uiBinder = GWT.create(ValueComparisonPanelUiBinder.class);

	interface ValueComparisonPanelUiBinder extends UiBinder<Widget, ValueComparisonPanel> {
	}
	
	@UiField
	ListBoxWithButton<OperatorClassic> lstFirstOperator, lstLastOperator;
	
	@UiField
	LabelTextBox txtFirstValue, txtLastValue;
	
	@UiField
	CheckBox chkLastValue;
	
	@UiField
	Label lblIn;

	private ClassRule rule;
	
	public ValueComparisonPanel(ClassRule rule, ClassField fieldParent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		lstFirstOperator.setList(OperatorClassic.values());
		lstLastOperator.setList(OperatorClassic.values());
		
		if (rule != null && rule.getType() == TypeRule.VALUE_COMPARAISON) {
			RuleValueComparison ruleDef = (RuleValueComparison) rule.getRule();
			
			lstFirstOperator.setSelectedObject(ruleDef.getFirstOperator());
			txtFirstValue.setText(ruleDef.getFirstValue());
			
			if (ruleDef.hasLastValue()) {
				chkLastValue.setValue(true);
				
				lstFirstOperator.setSelectedObject(ruleDef.getFirstOperator());
				txtFirstValue.setText(ruleDef.getFirstValue());
			}
		}
		
		updateUi(chkLastValue.getValue());
	}
	
	@UiHandler("lstFirstOperator")
	public void onFirstOperatorChange(ChangeEvent event) {
		updateUi(chkLastValue.getValue());
	}
	
	@UiHandler("lstLastOperator")
	public void onLastOperatorChange(ChangeEvent event) {
		updateUi(chkLastValue.getValue());
	}
	
	@UiHandler("chkLastValue")
	public void onLastValue(ClickEvent event) {
		updateUi(chkLastValue.getValue());
	}
	
	private void updateUi(boolean lastValueEnabled) {
		lstLastOperator.setEnabled(lastValueEnabled);
		txtLastValue.setEnabled(lastValueEnabled);
		
		OperatorClassic opFirst = lstFirstOperator.getSelectedObject();
		OperatorClassic opLast = lstLastOperator.getSelectedObject();
		
		lblIn.setVisible((opFirst != null && opFirst == OperatorClassic.IN) || (opLast != null && opLast == OperatorClassic.IN));
	}

	@Override
	public ClassRule getClassRule() {
		RuleValueComparison ruleDef = new RuleValueComparison();
		
		OperatorClassic firstOp = lstFirstOperator.getSelectedObject();
		String firstValue = txtFirstValue.getText();
		
		if (firstValue.isEmpty()) {
			//TODO: Message
			return null;
		}
		
		ruleDef.setFirstOperator(firstOp);
		ruleDef.setFirstValue(firstValue);
		
		boolean hasLastValue = chkLastValue.getValue();
		ruleDef.setHasLastValue(hasLastValue);
		if (hasLastValue) {
			OperatorClassic lastOp = lstLastOperator.getSelectedObject();
			String lastValue = txtLastValue.getText();

			if (lastValue.isEmpty()) {
				//TODO: Message
				return null;
			}
			
			ruleDef.setLastOperator(lastOp);
			ruleDef.setLastValue(lastValue);
		}
		
		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.VALUE_COMPARAISON);
		rule.setRule(ruleDef);
		
		return rule;
	}

}
