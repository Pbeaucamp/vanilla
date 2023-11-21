package bpm.vanilla.platform.core.beans.resources;

import java.util.List;

public class RuleColumnOperation implements IRule {

	private static final long serialVersionUID = 1L;

	private OperatorClassic operator;
	private OperatorOperation operatorOperation;
	private List<String> fieldNames;
	
	public RuleColumnOperation() { }
	
	public OperatorClassic getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorClassic operator) {
		this.operator = operator;
	}
	
	public OperatorOperation getOperatorOperation() {
		return operatorOperation;
	}
	
	public void setOperatorOperation(OperatorOperation operatorOperation) {
		this.operatorOperation = operatorOperation;
	}
	
	public List<String> getFieldNames() {
		return fieldNames;
	}
	
	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
