package bpm.data.viz.core.preparation;

public class PreparationRuleFormat extends PreparationRule {

	private static final long serialVersionUID = 1L;

	private String pattern;
	
	public PreparationRuleFormat() {
		type = RuleType.FORMAT_NUMBER;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
}
