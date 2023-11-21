package bpm.architect.web.client.panels.rules;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparison;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonChild;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ColumnComparisonPanel extends Composite implements IRulePanel {

	private static ColumnComparisonPanelUiBinder uiBinder = GWT.create(ColumnComparisonPanelUiBinder.class);

	interface ColumnComparisonPanelUiBinder extends UiBinder<Widget, ColumnComparisonPanel> {
	}

	@UiField
	DefinitionRulePanel panelDefinition;
	
	@UiField
	HTMLPanel operationPanel;

	@UiField
	LabelTextBox txtMainValue;
	
	@UiField
	RadioButton radioNull, radioEqualTo;

	private ClassRule rule;
	private List<ClassField> fields;
	
	private List<ColumnComparisonSubPanel> childPanels;

	public ColumnComparisonPanel(ClassRule rule, ClassField fieldParent, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		this.fields = fields;

		if (rule != null && rule.getType() == TypeRule.COLUMN_COMPARAISON) {
			panelDefinition.setDefinition(rule.getName(), rule.getDescription(), rule.isEnabled());
			
			RuleColumnComparison ruleDef = (RuleColumnComparison) rule.getRule();

			if (ruleDef.getChilds() != null) {
				for (RuleColumnComparisonChild child : ruleDef.getChilds()) {
					addComparison(child);
				}
			}
			
			radioNull.setValue(!ruleDef.isEqualToValue());
			radioEqualTo.setValue(ruleDef.isEqualToValue());
			txtMainValue.setText(ruleDef.getMainValue());
		}

		updateUi();
	}
	
	private void addComparison(RuleColumnComparisonChild child) {
		if (childPanels == null) {
			childPanels = new ArrayList<>();
		}
		
		ColumnComparisonSubPanel childPanel = new ColumnComparisonSubPanel(this, child, fields);
		operationPanel.add(childPanel);
		childPanels.add(childPanel);
	}
	
	public void removeComparison(ColumnComparisonSubPanel child) {
		childPanels.remove(child);
		child.removeFromParent();
	}

	@UiHandler("btnAdd")
	public void onAddComparison(ClickEvent event) {
		addComparison(null);
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
		txtMainValue.setEnabled(radioEqualTo.getValue());
	}

	@Override
	public ClassRule getClassRule() {
		String name = panelDefinition.getName();
		String description = panelDefinition.getDescription();
		boolean enabled = panelDefinition.isEnabled();
		
		RuleColumnComparison ruleDef = new RuleColumnComparison();
		
		boolean isEqualToValue = radioEqualTo.getValue();
		String mainValue = txtMainValue.getText();

		if (childPanels == null || childPanels.isEmpty()) {
			// TODO: Message
			return null;
		}
		
		for (ColumnComparisonSubPanel childPanel : childPanels) {
			RuleColumnComparisonChild child = childPanel.getChild();
			if (child == null) {
				return null;
			}
			ruleDef.addChild(child);
		}

		ruleDef.setEqualToValue(isEqualToValue);
		ruleDef.setMainValue(mainValue);

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setName(name);
		rule.setDescription(description);
		rule.setEnabled(enabled);
		rule.setType(TypeRule.COLUMN_COMPARAISON);
		rule.setRule(ruleDef);

		return rule;
	}
}
