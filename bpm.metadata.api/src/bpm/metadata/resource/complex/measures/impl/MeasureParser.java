package bpm.metadata.resource.complex.measures.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.IOperator;

public class MeasureParser {
	private static char START_TAG = '(';
	private static char END_TAG = ')';
	private static char OPERAND_SEPARATOR = ',';
	private static char SPACE = ' ';
	
	private List<Measure> availableMeasures = new ArrayList<Measure>();
	private List<DimensionLevel> levels = new ArrayList<DimensionLevel>();
	private List<Dimension> dimensions = new ArrayList<Dimension>();
	
	public MeasureParser( List<Measure>  availableMeasures, List<DimensionLevel> levels, List<Dimension> dimensions){
		this.availableMeasures = availableMeasures;
		this.levels = levels;
		this.dimensions = dimensions;
	}
	
	public IOperand getOperand(String operatorName) throws Exception{
		IOperator result = null;
		
		for(MathOperator op : MathOperator.operators){
			if (op.getSymbol().equals(operatorName)){
				result = op;
				break;
			}
		}
		
		for(AggregationOperator op : AggregationOperator.operators){
			if (op.getSymbol().equals(operatorName)){
				result = op;
				break;
			}
		}
		
		for(ConditionOperator op : ConditionOperator.operators){
			if (op.getSymbol().equals(operatorName)){
				result = op;
				break;
			}
		}
		
		for(Measure m : availableMeasures){
			if (m.getSymbol().equals(operatorName)){
				result = m;
				break;
			}
		}
		
		for(DimensionFunctionOperator m : DimensionFunctionOperator.operators){
			if (m.getSymbol().equals(operatorName)){
				result = m;
				break;
			}
		}
		
		for(DimensionLevel m : levels){
			if (m.getSymbol().equals(operatorName)){
				result = m;
				break;
			}
		}
		
		for(Dimension m : dimensions){
			if (m.getSymbol().equals(operatorName)){
				result = m;
				break;
			}
		}
		
		//TODO : same with specific Object 
		
		
		/*
		 * check if its a literal
		 */
		if (result == null && Pattern.matches("\\'.*\\'", operatorName.trim())){
			result = new LiteralOperator(operatorName.trim());
		}
		
		
		/*
		 * check if the operatorName is from the template
		 */
		if (result == null && Pattern.matches("\\<[a-zA-Z]\\>", operatorName.trim())){
			throw new Exception("Undefined argument " + operatorName);
		}
		
		if (result == null){
			throw new Exception("Undefined operatorName " + operatorName);
		}
		return result.createOperation();
	}
	
	
		
	public IOperand readChunk(InputStream formula) throws Exception{
		StringBuffer buf = new StringBuffer();;
		int lastRead = 0;
		IOperand currentOp = null;
		int currentPos = -1;
		
		while((lastRead = formula.read()) != -1){
			char c = (char)lastRead;
			if (c == START_TAG){
				buf = new StringBuffer();;
				if (currentOp == null){
					 
					currentOp = readChunk(formula);
				}
				else{
					currentPos ++;
					if ( currentOp.getOperator().getOperandNumber() > 0 ){
						currentOp.setOperand(currentPos, readChunk(formula));
						
					}
				}
			}
			else if (c == OPERAND_SEPARATOR ){
				//end operand
				if (buf.length() != 0){
					IOperand op = null;
					try{
						op = getOperand(buf.toString().trim());
					}catch(Exception ex){
						if (currentOp != null){
							throw new Exception(currentOp.toString() + " : " + ex.getMessage());
						}
						else{
							throw ex;
						}
					}
					
					buf = new StringBuffer();
					
					if (currentOp == null){
						currentOp = op;
					}
					else{
						currentPos ++;
						if ( currentOp.getOperator().getOperandNumber() > 0 ){
							currentOp.setOperand(currentPos, op);
							
						}
						
					}
				}
				

				
			}
			else if (c == END_TAG){
				if (buf.length() != 0){
					IOperand op = null;
					try{
						op = getOperand(buf.toString().trim());
					}catch(Exception ex){
						if (currentOp != null){
							throw new Exception(currentOp.toString() + " : " + ex.getMessage());
						}
						else{
							throw ex;
						}
					}
					buf = new StringBuffer();
					
					if (currentOp == null){
						currentOp = op;
					}
					else{
						currentPos ++;
						if ( currentOp.getOperator().getOperandNumber() > 0){
							currentOp.setOperand(currentPos, op);
							
						}
					}
				}
				
				return currentOp;
				
			}
			else if (c == SPACE){
				
			}
			else {
				buf.append(c);
			}
			
		
			
		}
		return currentOp;
	}
}
