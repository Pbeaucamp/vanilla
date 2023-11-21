package bpm.profiling.database.bean;

public class Rule {
	private int id;
	private int ruleSetId;
	private String operator;
	private String value1;
	private String value2;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRuleSetId() {
		return ruleSetId;
	}
	public void setRuleSetId(int ruleSetId) {
		this.ruleSetId = ruleSetId;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	
}
