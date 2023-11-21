package bpm.metadata.resource.complex.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.resource.complex.measures.IClause;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class DimensionFunctionOperator implements IOperator{
	public static int CURRENT_MEMBER_LEVEL = 0;
	public static int CURRENT_MEMBER = 1;
	public static int CURRENT_MEMBER_PARENT = 2;
	public static int MEMBER_CHILD_SIZE = 3;
	public static int MEMBER_CHILD = 4;
	
	
	public static DimensionFunctionOperator[] operators = new DimensionFunctionOperator[]{
		new DimensionFunctionOperator("currentMemberLvl", 1),
		new DimensionFunctionOperator("currentMember", 1),
		new DimensionFunctionOperator("currentMemberParent", 1),
		new DimensionFunctionOperator("currentMemberChildrenSize", 1),
		new DimensionFunctionOperator("currentMemberChild", 2)
	};
	
	private int operandNumber;
	private String symbol;
	private String template;
	
	private DimensionFunctionOperator(String symbol, int operandNumber){
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
		return new DimensionOperation(this);
	}
}
