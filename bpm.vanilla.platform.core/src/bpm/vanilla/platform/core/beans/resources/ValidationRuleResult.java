package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;

public class ValidationRuleResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int validationSchemaId;

	private String field;
	private String rule;
	private String ruleName;
	private String value;

	public ValidationRuleResult() {
	}

	public ValidationRuleResult(String field, String rule, String ruleName, String value) {
		this.field = field;
		this.rule = rule;
		this.ruleName = ruleName;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValidationSchemaId() {
		return validationSchemaId;
	}

	public void setValidationSchemaId(int validationSchemaId) {
		this.validationSchemaId = validationSchemaId;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
