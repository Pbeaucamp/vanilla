package bpm.metadata.resource.complex.measures.impl;

import bpm.metadata.resource.complex.measures.IOperand;


public class DimensionOperation extends Operation{

	public DimensionOperation(DimensionFunctionOperator operator) {
		super(operator);
	}
	
	@Override
	public void setOperand(int pos, IOperand operand) throws Exception{
		if (pos == 0 &&  !( operand.getOperator() instanceof Dimension)){
			throw new Exception("This argument must be a Dimension");
		}
		super.setOperand(pos, operand);
	}
}
