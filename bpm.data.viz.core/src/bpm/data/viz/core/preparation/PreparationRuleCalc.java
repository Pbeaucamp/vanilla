package bpm.data.viz.core.preparation;


public class PreparationRuleCalc extends PreparationRule {

	private static final long serialVersionUID = 1L;

	private String formula;
	
	public PreparationRuleCalc() {
		type = RuleType.CALC;
		newColumn = true;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	
}
