package bpm.gwt.commons.shared.fmdt.metadata;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

public class MetadataPrompt extends MetadataResource implements IsSerializable {

	public static final String EQUAL = "=";
	public static final String INF = "<";
	public static final String INF_EQUAL = "<=";
	public static final String SUP = ">";
	public static final String SUP_EQUAL = ">=";
	public static final String NOT_EQUAL = "!=";
	public static final String DIFFERENT = "<>";
	public static final String IN = "IN";
	public static final String LIKE = "LIKE";
	public static final String BETWEEN = "BETWEEN";
	
	public static final String[] OPERATORS = {EQUAL, INF, INF_EQUAL, SUP, SUP_EQUAL, NOT_EQUAL, DIFFERENT, IN, LIKE, BETWEEN};
	
	private String operator = "=";
	
	public MetadataPrompt() { }

	public MetadataPrompt(String name, String operator, DatabaseColumn column) {
		super(name, column);
		this.operator = operator;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
}
