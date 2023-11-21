package bpm.studio.expressions.core.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.studio.expressions.core.measures.IClause;
import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;

public class ConditionOperator implements IOperator{
	public static int IF = 0;
	public static int EQU = 1;
	public static int LT = 2;
	public static int LT_EQ = 3;
	public static int GT = 4;
	public static int GT_EQ = 5;
	
	public static ConditionOperator[] operators = new ConditionOperator[]{
		new ConditionOperator("if", 3),
		new ConditionOperator("==", 2),
		new ConditionOperator("<", 2),
		new ConditionOperator("<=", 2),
		new ConditionOperator(">", 2),
		new ConditionOperator(">=", 2)
		
	};
	
	private int operandNumber;
	private String symbol;
	private String template;
	
	private ConditionOperator(String symbol, int operandNumber){
		this.operandNumber = operandNumber;
		this.symbol = symbol;
	}
	
	public int getOperandNumber() {
		return operandNumber;
	}

	public String getSymbol() {
		return symbol;
	}

	public boolean supportClauses() {
		return false;
	}

	public IClause getClause(int pos) {
		return null;
	}

	public List<IClause> getClauses() {
		return new ArrayList<IClause>();
	}

	public int getClausesSize() {
		return 0;
	}

	public String getTemplate() {
		if (template == null){
			StringBuffer buf = new StringBuffer();
			buf.append(" ( " + getSymbol());
			
			for(int i = 0; i < operandNumber; i++){
				buf.append(", <" + (char)('a' + i) + ">");
			}
			buf.append(") ");
			template = buf.toString();
		}
		
		
		
		return template;
	}

	public IOperand createOperation() {
		return new Operation(this);
	}
}
