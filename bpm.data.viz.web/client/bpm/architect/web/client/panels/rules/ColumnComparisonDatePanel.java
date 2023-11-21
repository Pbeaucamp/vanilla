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
	ListBoxWithButton<ClassField> lstColumns;

	@UiField
	ListBoxWithButton<OperatorDate> lstOperators;

	@UiField
	DateBox dateBoxValue;

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
			RuleColumnComparisonDate ruleDef = (RuleColumnComparisonDate) rule.getRule();

			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).getName().equals(ruleDef.getFieldName()))
					lstColumns.setSelectedIndex(i);
			}
			lstOperators.setSelectedObject(ruleDef.getOperator());
			dateBoxValue.setValue(ruleDef.getValue());
		}
	}

	@Override
	public ClassRule getClassRule() {
		RuleColumnComparisonDate ruleDef = new RuleColumnComparisonDate();

		ClassField field = lstColumns.getSelectedObject();
		OperatorDate op = lstOperators.getSelectedObject();
		Date value = dateBoxValue.getValue();

		if (value == null) {
			// TODO: Message
			return null;
		}

		ruleDef.setFieldName(field.getName());
		ruleDef.setOperator(op);
		ruleDef.setValue(value);

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.COLUMN_COMPARAISON_DATE);
		rule.setRule(ruleDef);

		return rule;
	}
}
