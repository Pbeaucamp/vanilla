package bpm.metadata.resource.complex.measures.impl;

import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class DimensionLevel implements IOperator{

	private FmdtDimension dimension;
	private int lvlNumber;
	
	public DimensionLevel(FmdtDimension dim, int levelNumber){
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
