package bpm.studio.expressions.core.measures;


public interface IOperand {
	public IOperator getOperator();
	
	public IOperand[] getOperands();
	
	public IOperand getOperand(int pos);
	
	public void setOperand(int pos, IOperand operand) throws Exception;
	
	public void validate() throws Exception;
}
