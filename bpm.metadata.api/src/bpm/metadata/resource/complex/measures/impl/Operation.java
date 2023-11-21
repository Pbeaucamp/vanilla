package bpm.metadata.resource.complex.measures.impl;

import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class Operation implements IOperand{

	private IOperator operator;
	private IOperand[] operands;
	
	public Operation(IOperator operator){
		this.operator = operator;
		operands = new IOperand[this.operator.getOperandNumber()];
	}
	
	public IOperand getOperand(int pos) {
		return operands[pos];
	}

	public IOperand[] getOperands() {
		return operands;
	}

	public IOperator getOperator() {
		return operator;
	}

	public void setOperand(int pos, IOperand operand) throws Exception{
		if (pos >= getOperator().getOperandNumber()){
			throw new Exception("Operator " + getOperator().getSymbol() + " have only " + getOperator().getOperandNumber() + " operands");
		}
		 operands[pos] = operand;
	}

	public String toString(){
		StringBuffer buf = new StringBuffer();
		
		if (getOperator().getOperandNumber() > 0){
			buf.append("(");
		}
		buf.append(getOperator().getSymbol());
		
		for(int i = 0; i < getOperands().length; i++){
			buf.append(", ");
			if (getOperand(i) == null){
				buf.append("<?>");
			}
			else {
				buf.append(getOperand(i).toString());
			}
		}
		if (getOperator().getOperandNumber() > 0){
			buf.append(")");
		}
		
		return buf.toString();
	}

	public void validate() throws Exception {
		for(int i = 0; i < getOperator().getOperandNumber(); i++){
			if (getOperand(i) == null){
				throw new Exception("Missing Operand number " + i );
			}
			getOperand(i).validate();
		}
		
	}


	
}
