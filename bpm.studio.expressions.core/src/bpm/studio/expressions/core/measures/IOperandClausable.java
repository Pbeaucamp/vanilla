package bpm.studio.expressions.core.measures;

import java.util.List;

public interface IOperandClausable extends IOperand{
public List<IClause> getClauses();
	
	public int getClausesSize();
	
	public IClause getClause(int pos);
}
