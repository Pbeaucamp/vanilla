package bpm.document.management.core.model;

import java.io.Serializable;

public class SpecificSearch implements Serializable {
	
	private static final long serialVersionUID = 136903958412437945L;
	
	public static final int LOGICAL_AND = 0;
	public static final int LOGICAL_OR = 1;
	
	public static final String EQUAL = "=";
	public static final String NOT_EQUAL = "!=";
	
	private FormField field;
	private String operator;
	private int logical;
	private String value;
	
	public SpecificSearch() { }
	
	public SpecificSearch(FormField field, String operator, int logical, String value) {
		this.field = field;
		this.operator = operator;
		this.logical = logical;
		this.value = value;
	}
	
	public FormField getField() {
		return field;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public int getLogical() {
		return logical;
	}
	
	public String getValue() {
		return value;
	}
}

