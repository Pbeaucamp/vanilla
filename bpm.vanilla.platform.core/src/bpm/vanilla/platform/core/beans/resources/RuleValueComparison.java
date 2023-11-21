package bpm.vanilla.platform.core.beans.resources;

public class RuleValueComparison extends RuleExclusionValue {

	private static final long serialVersionUID = 1L;
	
	private OperatorClassic firstOperator;
	private String firstValue;

	private boolean hasLastValue;
	private OperatorClassic lastOperator;
	private String lastValue;

	public RuleValueComparison() {
	}

	public OperatorClassic getFirstOperator() {
		return firstOperator;
	}

	public void setFirstOperator(OperatorClassic firstOperator) {
		this.firstOperator = firstOperator;
	}

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public boolean hasLastValue() {
		return hasLastValue;
	}

	public void setHasLastValue(boolean hasLastValue) {
		this.hasLastValue = hasLastValue;
	}

	public OperatorClassic getLastOperator() {
		return lastOperator;
	}

	public void setLastOperator(OperatorClassic lastOperator) {
		this.lastOperator = lastOperator;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}
	
	@Override
	public String toString() {
		return "To remake RuleValueComparison ToString";
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
