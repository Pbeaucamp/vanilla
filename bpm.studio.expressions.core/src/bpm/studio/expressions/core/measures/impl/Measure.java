package bpm.studio.expressions.core.measures.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.studio.expressions.core.measures.IClause;
import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.model.IField;

public class Measure implements IOperator{

	private String symbol;
	private IField field;
	
	public Measure(IField field){
		this.field = field;
		this.symbol = "[" + field.getName() + "]";
	}
	
	public IField getField(){
		return field;
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
