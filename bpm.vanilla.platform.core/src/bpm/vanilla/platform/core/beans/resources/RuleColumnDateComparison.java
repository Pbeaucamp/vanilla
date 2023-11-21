package bpm.vanilla.platform.core.beans.resources;

public class RuleColumnDateComparison extends RuleExclusionValue {

	private static final long serialVersionUID = 1L;

	private OperatorDate operator;
	private String fieldName;
	
	public RuleColumnDateComparison() { }

	public OperatorDate getOperator() {
		return operator;
	}

	public void setOperator(OperatorDate operator) {
		this.operator = operator;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}

}
