package bpm.metadata.resource.complex.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.measures.IClause;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class Dimension implements IOperator{

	private String symbol;
	private FmdtDimension dimension;
	
	public Dimension(FmdtDimension dimension){
		this.dimension = dimension;
		this.symbol = "{" + dimension.getName() + "}";
	}
	
	public FmdtDimension getDimension(){
		return dimension;
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
