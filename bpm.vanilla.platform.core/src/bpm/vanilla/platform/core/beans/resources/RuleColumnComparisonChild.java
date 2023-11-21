package bpm.vanilla.platform.core.beans.resources;

public class RuleColumnComparisonChild implements IRule {

	private static final long serialVersionUID = 1L;

	private String fieldName;
	private OperatorClassic operator;
	private String value;
	
	public RuleColumnComparisonChild() { }
	
	public RuleColumnComparisonChild(String fieldName, OperatorClassic operator, String value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}
	
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
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
