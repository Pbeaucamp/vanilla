package bpm.architect.web.client.panels.rules;

import java.util.List;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ColumnComparisonPanel extends Composite implements IRulePanel {

	private static ColumnComparisonPanelUiBinder uiBinder = GWT.create(ColumnComparisonPanelUiBinder.class);

	interface ColumnComparisonPanelUiBinder extends UiBinder<Widget, ColumnComparisonPanel> {
	}

	@UiField
	ListBoxWithButton<ClassField> lstColumns;

	@UiField
	ListBoxWithButton<OperatorClassic> lstOperators;

	@UiField
	LabelTextBox txtValue, txtMainValue;
	
	@UiField
	RadioButton radioNull, radioEqualTo;

	@UiField
	Label lblIn;

	private ClassRule rule;

	public ColumnComparisonPanel(ClassRule rule, ClassField fieldParent, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;

		lstColumns.setList(fields);
		lstOperators.setList(OperatorClassic.values());

		if (rule != null && rule.getType() == TypeRule.COLUMN_COMPARAISON) {
//			RuleColumnComparison ruleDef = (RuleColumnComparison) rule.getRule();
//
//			for (int i = 0; i < fields.size(); i++) {
//				if (fields.get(i).getName().equals(ruleDef.getFieldName()))
//					lstColumns.setSelectedIndex(i);
//			}
//			lstOperators.setSelectedObject(ruleDef.getOperator());
//			txtValue.setText(ruleDef.getValue());
//			
//			radioNull.setValue(!ruleDef.isEqualToValue());
//			radioEqualTo.setValue(ruleDef.isEqualToValue());
//			txtMainValue.setText(ruleDef.getMainValue());
		}

		updateUi();
	}

	@UiHandler("lstOperators")
	public void onOperatorChange(ChangeEvent event) {
		updateUi();
	}

	@UiHandler("radioNull")
	public void onRadioNull(ClickEvent event) {
		updateUi();
	}

	@UiHandler("radioEqualTo")
	public void onRadioEqualTo(ClickEvent event) {
		updateUi();
	}

	private void updateUi() {
		OperatorClassic op = lstOperators.getSelectedObject();
		lblIn.setVisible(op != null && op == OperatorClassic.IN);
		txtMainValue.setEnabled(radioEqualTo.getValue());
	}

	@Override
	public ClassRule getClassRule() {
		RuleColumnComparison ruleDef = new RuleColumnComparison();

		ClassField field = lstColumns.getSelectedObject();
		OperatorClassic op = lstOperators.getSelectedObject();
		String value = txtValue.getText();
		
		boolean isEqualToValue = radioEqualTo.getValue();
		String mainValue = txtMainValue.getText();

		if (value.isEmpty()) {
			// TODO: Message
			return null;
		}

//		ruleDef.setFieldName(field.getName());
//		ruleDef.setOperator(op);
//		ruleDef.setValue(value);
//		ruleDef.setEqualToValue(isEqualToValue);
//		ruleDef.setMainValue(mainValue);

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.COLUMN_COMPARAISON);
		rule.setRule(ruleDef);

		return rule;
	}
}
