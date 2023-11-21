package bpm.smart.core.model.workflow.activity.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.DataColumn;

public class ColumnFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	public static List<String> operators;
	static {
		operators = new ArrayList<String>();
		operators.add("==");
		operators.add("!=");
		operators.add("<=");
		operators.add(">=");
		operators.add("<");
		operators.add(">");
	}
	
	private DataColumn column;
	private String operator = "==";
	private String value = "";

	public DataColumn getColumn() {
		return column;
	}

	public void setColumn(DataColumn column) {
		this.column = column;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
