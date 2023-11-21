package bpm.metadata.resource.complex.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.resource.complex.measures.IClause;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class MathOperator implements IOperator{
	
	public static int ADD = 0;
	public static int SUB = 1;
	public static int MUL = 2;
	public static int DIV = 3;
	public static int ABS = 4;
	
	public static MathOperator[] operators = new MathOperator[]{
		new MathOperator("+", 2),
		new MathOperator("-", 2),
		new MathOperator("*", 2),
		new MathOperator("/", 2),
		new MathOperator("ABS", 1)
		
	};
	
	private int operandNumber;
	private String symbol;
	private String template;
	
	
	private MathOperator(String symbol, int operandNumber){
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
