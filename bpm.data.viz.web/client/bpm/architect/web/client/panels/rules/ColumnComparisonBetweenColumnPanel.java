package bpm.architect.web.client.panels.rules;

import java.util.List;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonBetweenColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ColumnComparisonBetweenColumnPanel extends Composite implements IRulePanel {

	private static ColumnComparisonBetweenColumnPanelPanelUiBinder uiBinder = GWT.create(ColumnComparisonBetweenColumnPanelPanelUiBinder.class);

	interface ColumnComparisonBetweenColumnPanelPanelUiBinder extends UiBinder<Widget, ColumnComparisonBetweenColumnPanel> {
	}

	@UiField
	ListBoxWithButton<OperatorClassic> lstOperators;

	@UiField
	ListBoxWithButton<ClassField> lstColumns;

	private ClassRule rule;

	public ColumnComparisonBetweenColumnPanel(ClassRule rule, ClassField fieldParent, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		lstOperators.setList(OperatorClassic.values());
		lstColumns.setList(fields);
		
		if (rule != null && rule.getType() == TypeRule.COLUMN_COMPARAISON_BETWEEN_COLUMN) {
			RuleColumnComparisonBetweenColumn ruleDef = (RuleColumnComparisonBetweenColumn) rule.getRule();
			
			for (int i=0; i<fields.size(); i++) {
				if (fields.get(i).getName().equals(ruleDef.getFieldName())) 
					lstColumns.setSelectedIndex(i);
			}
			lstOperators.setSelectedObject(ruleDef.getOperator());
		}
	}

	@Override
	public ClassRule getClassRule() {
		RuleColumnComparisonBetweenColumn ruleDef = new RuleColumnComparisonBetweenColumn();
		
		OperatorClassic op = lstOperators.getSelectedObject();
		ClassField field = lstColumns.getSelectedObject();
		
		ruleDef.setFieldName(field.getName());
		ruleDef.setOperator(op);
		
		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.COLUMN_COMPARAISON_BETWEEN_COLUMN);
		rule.setRule(ruleDef);
		
		return rule;
	}
}
