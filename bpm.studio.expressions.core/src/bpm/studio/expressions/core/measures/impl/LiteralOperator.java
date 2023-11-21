package bpm.studio.expressions.core.measures.impl;

import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;

public class LiteralOperator implements IOperator{

	private String value;
	
	public LiteralOperator(String value){
		this.value = value;
	}
	
	public IOperand createOperation() {
		return new Operation(this);
	}

	public int getOperandNumber() {
		return 0;
	}

	public String getSymbol() {
		return "'" + value + "'";
	}

	public String getTemplate() {
		return getSymbol();
	}

	public boolean supportClauses() {
		return false;
	}

}
