package bpm.metadata.resource.complex.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.complex.measures.IClause;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class Measure implements IOperator{

	private String symbol;
	private IDataStreamElement field;
	
	public Measure(IDataStreamElement field){
		this.field = field;
		this.symbol = "[" + field.getName() + "]";
	}
	
	public IDataStreamElement getField(){
		return field;
	}
	public int getOperandNumber() {
		return 0;
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
		return " " + getSymbol() + " ";
	}
	
	public IOperand createOperation() {
		return new Operation(this);
	}
}
