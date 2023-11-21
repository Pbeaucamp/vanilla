package bpm.metadata.resource.complex.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.resource.complex.measures.IClause;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class AggregationOperator implements IOperator{
	
	public static int SUM = 0;
	public static int MIN = 1;
	public static int MAX = 2;
	public static int COUNT = 3;
	public static int COUNT_DISTINCT = 4;
	
	public static AggregationOperator[] operators = new AggregationOperator[]{
		new AggregationOperator("SUM", 1),
		new AggregationOperator("MIN", 1),
		new AggregationOperator("MAX", 1),
		new AggregationOperator("COUNT", 1),
		new AggregationOperator("COUNT_DISTINCT", 1)
		
	};
	
	private int operandNumber;
	private String symbol;
	
	
	private AggregationOperator(String symbol, int operandNumber){
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
		return " ( " + getSymbol() + ", <a> ) ";
	}
	
	public IOperand createOperation() {
		return new Operation(this);
	}
}
