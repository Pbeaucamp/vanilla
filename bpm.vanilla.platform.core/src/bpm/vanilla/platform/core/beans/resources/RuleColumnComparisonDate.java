package bpm.vanilla.platform.core.beans.resources;

import java.util.Date;

public class RuleColumnComparisonDate extends RuleExclusionValue {

	private static final long serialVersionUID = 1L;

	private String fieldName;
	private OperatorDate operator;
	private Date value;
	
	public RuleColumnComparisonDate() { }
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public OperatorDate getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorDate operator) {
		this.operator = operator;
	}
	
	public Date getValue() {
		return value;
	}
	
	public void setValue(Date value) {
		this.value = value;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
