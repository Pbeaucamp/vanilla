package bpm.data.viz.core.preparation;


public class PreparationRuleAddChar extends PreparationRule {

	private static final long serialVersionUID = 1L;

	private String charToAdd;

	public PreparationRuleAddChar() {
		type = RuleType.ADD_CHAR;
	}
	
	public String getCharToAdd() {
		return charToAdd;
	}

	public void setCharToAdd(String charToAdd) {
		this.charToAdd = charToAdd;
	}
	
	
}
