package bpm.architect.web.client.panels.rules;

import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PatternPanel extends Composite implements IRulePanel {

	private static PatternPanelUiBinder uiBinder = GWT.create(PatternPanelUiBinder.class);

	interface PatternPanelUiBinder extends UiBinder<Widget, PatternPanel> {
	}

	@UiField
	DefinitionRulePanel panelDefinition;
	
	@UiField
	LabelTextBox txtRegex;
	
	@UiField
	CheckBox chkErrorIfMatch;
	
	private ClassRule rule;

	public PatternPanel(ClassRule rule, ClassField fieldParent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		if (rule != null && rule.getType() == TypeRule.PATTERN) {
			panelDefinition.setDefinition(rule.getName(), rule.getDescription(), rule.isEnabled());
			
			RulePatternComparison ruleDef = (RulePatternComparison) rule.getRule();
			txtRegex.setText(ruleDef.getRegex());
			chkErrorIfMatch.setValue(ruleDef.isErrorIfMatch());
		}
	}

	@Override
	public ClassRule getClassRule() {
		String name = panelDefinition.getName();
		String description = panelDefinition.getDescription();
		boolean enabled = panelDefinition.isEnabled();
		
		RulePatternComparison ruleDef = new RulePatternComparison();
		
		String regex = txtRegex.getText();
		boolean errorIfMatch = chkErrorIfMatch.getValue();
		
		if (regex.isEmpty()) {
			//TODO: Message
			return null;
		}
		
		ruleDef.setRegex(regex);
		ruleDef.setErrorIfMatch(errorIfMatch);
		
		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setName(name);
		rule.setDescription(description);
		rule.setEnabled(enabled);
		rule.setType(TypeRule.PATTERN);
		rule.setRule(ruleDef);
		
		return rule;
	}
}
