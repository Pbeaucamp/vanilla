package bpm.data.viz.core.preparation;

public class PreparationRuleMinMax extends PreparationRule {

	private static final long serialVersionUID = 1L;
	
	private double value;

	public PreparationRuleMinMax() {
		super();
	}

	public PreparationRuleMinMax(RuleType type) {
		super(type);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
