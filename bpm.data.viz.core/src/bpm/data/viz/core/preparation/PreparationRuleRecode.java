package bpm.data.viz.core.preparation;

public class PreparationRuleRecode extends PreparationRule {

	private static final long serialVersionUID = 1L;

	private String originFormula;
	private String resultFormula;
	
	public PreparationRuleRecode() {
		type = RuleType.RECODE;
	}

	public String getOriginFormula() {
		return originFormula;
	}

	public void setOriginFormula(String originFormula) {
		this.originFormula = originFormula;
	}

	public String getResultFormula() {
		return resultFormula;
	}

	public void setResultFormula(String resultFormula) {
		this.resultFormula = resultFormula;
	}

}
