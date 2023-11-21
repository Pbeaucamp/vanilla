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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PatternPanel extends Composite implements IRulePanel {

	private static PatternPanelUiBinder uiBinder = GWT.create(PatternPanelUiBinder.class);

	interface PatternPanelUiBinder extends UiBinder<Widget, PatternPanel> {
	}
	
	@UiField
	LabelTextBox txtRegex;
	
	private ClassRule rule;

	public PatternPanel(ClassRule rule, ClassField fieldParent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		
		if (rule != null && rule.getType() == TypeRule.PATTERN) {
			RulePatternComparison ruleDef = (RulePatternComparison) rule.getRule();
			txtRegex.setText(ruleDef.getRegex());
		}
	}

	@Override
	public ClassRule getClassRule() {
		RulePatternComparison ruleDef = new RulePatternComparison();
		
		String regex = txtRegex.getText();
		
		if (regex.isEmpty()) {
			//TODO: Message
			return null;
		}
		
		ruleDef.setRegex(regex);
		
		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.PATTERN);
		rule.setRule(ruleDef);
		
		return rule;
	}
}
