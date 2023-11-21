package bpm.architect.web.client.panels.rules;

import java.util.Date;
import java.util.List;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorDate;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonDate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ColumnComparisonDatePanel extends Composite implements IRulePanel {
	
	private DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static ColumnComparisonDatePanelUiBinder uiBinder = GWT.create(ColumnComparisonDatePanelUiBinder.class);

	interface ColumnComparisonDatePanelUiBinder extends UiBinder<Widget, ColumnComparisonDatePanel> {
	}

	@UiField
	DefinitionRulePanel panelDefinition;

	@UiField
	ListBoxWithButton<ClassField> lstColumns;

	@UiField
	ListBoxWithButton<OperatorDate> lstOperators;

	@UiField
	DateBox dateBoxValue;
	
	@UiField
	ExclusionValuePanel panelExclusion;

	private ClassRule rule;

	public ColumnComparisonDatePanel(ClassRule rule, ClassField fieldParent, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		dateBoxValue.setFormat(new DateBox.DefaultFormat(df));
		dateBoxValue.getTextBox().setReadOnly(true);
		dateBoxValue.getDatePicker().setYearAndMonthDropdownVisible(true);

		lstColumns.setList(fields);
		lstOperators.setList(OperatorDate.values());

		if (rule != null && rule.getType() == TypeRule.COLUMN_COMPARAISON_DATE) {
			panelDefinition.setDefinition(rule.getName(), rule.getDescription(), rule.isEnabled());
			
			RuleColumnComparisonDate ruleDef = (RuleColumnComparisonDate) rule.getRule();

			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).getName().equals(ruleDef.getFieldName()))
					lstColumns.setSelectedIndex(i);
			}
			lstOperators.setSelectedObject(ruleDef.getOperator());
			dateBoxValue.setValue(ruleDef.getValue());
			panelExclusion.loadPanel(ruleDef);
		}
	}

	@Override
	public ClassRule getClassRule() {
		String name = panelDefinition.getName();
		String description = panelDefinition.getDescription();
		boolean enabled = panelDefinition.isEnabled();
		
		RuleColumnComparisonDate ruleDef = new RuleColumnComparisonDate();

		ClassField field = lstColumns.getSelectedObject();
		OperatorDate op = lstOperators.getSelectedObject();
		Date value = dateBoxValue.getValue();
		boolean hasExclusionValue = panelExclusion.hasExclusionValue();
		String exclusionValue = panelExclusion.getExclusionValue();

		if (value == null) {
			// TODO: Message
			return null;
		}

		ruleDef.setFieldName(field.getName());
		ruleDef.setOperator(op);
		ruleDef.setValue(value);
		ruleDef.setHasExclusionValue(hasExclusionValue);
		ruleDef.setExclusionValue(exclusionValue);

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setName(name);
		rule.setDescription(description);
		rule.setEnabled(enabled);
		rule.setType(TypeRule.COLUMN_COMPARAISON_DATE);
		rule.setRule(ruleDef);

		return rule;
	}
}
