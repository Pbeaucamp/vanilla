package bpm.studio.expressions.core.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.studio.expressions.core.measures.IClause;
import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.model.StructureDimension;

public class Dimension implements IOperator{

	private String symbol;
	private StructureDimension dimension;
	
	public Dimension(StructureDimension dimension){
		this.dimension = dimension;
		this.symbol = "{" + dimension.getName() + "}";
	}
	
	
	
	public StructureDimension getDimension(){
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
