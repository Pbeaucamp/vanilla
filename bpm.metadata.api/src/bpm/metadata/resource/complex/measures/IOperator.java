package bpm.metadata.resource.complex.measures;

public interface IOperator {
	
	public String getSymbol();
	
	public int getOperandNumber();
	
	public boolean supportClauses();

	public String getTemplate();
	
	public IOperand createOperation();
}
