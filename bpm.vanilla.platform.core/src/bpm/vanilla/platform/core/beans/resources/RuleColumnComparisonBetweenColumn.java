package bpm.vanilla.platform.core.beans.resources;

public class RuleColumnComparisonBetweenColumn extends RuleExclusionValue {

	private static final long serialVersionUID = 1L;

	private OperatorClassic operator;
	private String fieldName;
	
	public RuleColumnComparisonBetweenColumn() { }
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public OperatorClassic getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorClassic operator) {
		this.operator = operator;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
