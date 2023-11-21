package bpm.fasd.expressions.api.model;

import java.util.ArrayList;
import java.util.List;

import bpm.studio.expressions.core.measures.IClause;
import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.measures.impl.Operation;

public class FormatingOperator implements IOperator{
	public static int COLOR = 0;
	
	private int operandNumber;
	private String symbol;
	private String template;
	
	
	public static FormatingOperator[] operators = new FormatingOperator[]{
		new FormatingOperator("Color", 1)
		
	};
	
	private FormatingOperator(String symbol, int operandNumber){
		this.operandNumber = operandNumber;
		this.symbol = symbol;
	}

	public int getOperandNumber() {
		return operandNumber;
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
		if (template == null){
			StringBuffer buf = new StringBuffer();
			buf.append(" ( " + getSymbol());
			
			for(int i = 0; i < operandNumber; i++){
				buf.append(", <" + (char)('a' + i) + ">");
			}
			buf.append(") ");
			template = buf.toString();
		}
		
		
		
		return template;
	}

	public IOperand createOperation() {
		return new Operation(this);
	}
}
