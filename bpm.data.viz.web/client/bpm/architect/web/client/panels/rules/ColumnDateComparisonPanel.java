package bpm.architect.web.client.panels.rules;

import java.util.List;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorDate;
import bpm.vanilla.platform.core.beans.resources.RuleColumnDateComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ColumnDateComparisonPanel extends Composite implements IRulePanel {

	private static ColumnDateComparisonPanelUiBinder uiBinder = GWT.create(ColumnDateComparisonPanelUiBinder.class);

	interface ColumnDateComparisonPanelUiBinder extends UiBinder<Widget, ColumnDateComparisonPanel> {
	}
	
	@UiField
	ListBoxWithButton<OperatorDate> lstOperators;

	@UiField
	ListBoxWithButton<ClassField> lstColumns;

	private ClassRule rule;

	public ColumnDateComparisonPanel(ClassRule rule, ClassField fieldParent, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		lstOperators.setList(OperatorDate.values());
		lstColumns.setList(fields);
		
		if (rule != null && rule.getType() == TypeRule.COLUMN_DATE_COMPARAISON) {
			RuleColumnDateComparison ruleDef = (RuleColumnDateComparison) rule.getRule();
			
			for (int i=0; i<fields.size(); i++) {
				if (fields.get(i).getName().equals(ruleDef.getFieldName())) 
					lstColumns.setSelectedIndex(i);
			}
			lstOperators.setSelectedObject(ruleDef.getOperator());
		}
	}

	@Override
	public ClassRule getClassRule() {
		RuleColumnDateComparison ruleDef = new RuleColumnDateComparison();
		
		OperatorDate op = lstOperators.getSelectedObject();
		ClassField field = lstColumns.getSelectedObject();
		
		ruleDef.setFieldName(field.getName());
		ruleDef.setOperator(op);
		
		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.COLUMN_DATE_COMPARAISON);
		rule.setRule(ruleDef);
		
		return rule;
	}
}
