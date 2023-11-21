package bpm.studio.expressions.core.measures.impl;

import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.model.StructureDimension;

public class DimensionLevel implements IOperator{

	private StructureDimension dimension;
	private int lvlNumber;
	
	public DimensionLevel(StructureDimension dim, int levelNumber){
		this.lvlNumber = levelNumber;
		this.dimension = dim;
	}

	public IOperand createOperation() {
		return new Operation(this);
	}

	public int getOperandNumber() {
		return 0;
	}

	public String getSymbol() {
		return "{[" + dimension.getName() + "].[" + lvlNumber + "]}";
	}

	public String getTemplate() {
		return getSymbol() + " ";
	}

	public boolean supportClauses() {
		return false;
	}
	
	
}
