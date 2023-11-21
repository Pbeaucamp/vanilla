package bpm.fasd.expressions.api.model;

import bpm.studio.expressions.core.measures.IOperand;
import bpm.studio.expressions.core.measures.impl.ConditionOperator;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionFunctionOperator;
import bpm.studio.expressions.core.measures.impl.DimensionOperation;
import bpm.studio.expressions.core.measures.impl.LiteralOperator;

public class FasdMeasureMdxConverter {

	
	public static String getOperandMdx(IOperand operand){
		StringBuffer buf = new StringBuffer();
		
		
		
		
		if (operand  instanceof DimensionOperation){
			DimensionFunctionOperator op = (DimensionFunctionOperator)operand.getOperator();
			
			buf.append("[" + ((FasdDimension)(((Dimension)operand.getOperand(0).getOperator())).getDimension()).getName() + "]");
//			buf.append(".[" + ((FasdDimension)((Dimension)operand.getOperator()).getDimension()).getName() + "]");
			if (op == DimensionFunctionOperator.operators[DimensionFunctionOperator.CURRENT_MEMBER]){
				
//				buf.append("[" + ((FasdDimension)(((Dimension)operand.getOperand(0))).getDimension()).getName() + "]");
				buf.append(".CurrentMember");
			}
			else if (op == DimensionFunctionOperator.operators[DimensionFunctionOperator.CURRENT_MEMBER_LEVEL]){
				buf.append(".CurrentMember.Level");
			}
			else if (op == DimensionFunctionOperator.operators[DimensionFunctionOperator.CURRENT_MEMBER_PARENT]){
				buf.append(".CurrentMember.Parent");
			}
			else if (op == DimensionFunctionOperator.operators[DimensionFunctionOperator.MEMBER_CHILD]){
				
			}
			else if (op == DimensionFunctionOperator.operators[DimensionFunctionOperator.MEMBER_CHILD_SIZE]){
				buf.append(".CurrentMember.Children");
				return "COUNT(" + buf.toString() + ")";
			}
			
			return buf.toString();
			
		}
		if (operand.getOperator() instanceof FasdMeasure){
			buf.append(((FasdMeasure)operand.getOperator()).getMdx());
		}
		else if (operand.getOperator() instanceof LiteralOperator){
			try{
				Double.parseDouble(((LiteralOperator)operand.getOperator()).getSymbol().replace("''", ""));
				buf.append(((LiteralOperator)operand.getOperator()).getSymbol().replace("'", ""));
			}catch(Exception ex){
				
				try{
					Long.parseLong(((LiteralOperator)operand.getOperator()).getSymbol().replace("''", ""));
					buf.append(((LiteralOperator)operand.getOperator()).getSymbol().replace("'", ""));
				}catch(Exception e1){
					buf.append(((LiteralOperator)operand.getOperator()).getSymbol().replace("''", "'"));
				}
				
			}
			
		}
		else if (operand.getOperator()  == FormatingOperator.operators[FormatingOperator.COLOR]){
			buf.append("'|#|style=color:" + getOperandMdx(operand.getOperand(0)).replace("'", "").replace("#", "\\#").replace("0", "\\0") + "'");
			
			
		}
		else if (operand.getOperator() != null){
			
			if (operand.getOperator() == ConditionOperator.operators[ConditionOperator.IF]){
				
				buf.append("Iif(");
				buf.append(getOperandMdx(operand.getOperand(0)));
				buf.append(", ");
				buf.append(getOperandMdx(operand.getOperand(1)));
				buf.append(", ");
				buf.append(getOperandMdx(operand.getOperand(2)));
				buf.append(")");
			}
			else if (operand.getOperator().getOperandNumber() == 2){
				buf.append(getOperandMdx(operand.getOperand(0)) + operand.getOperator().getSymbol() + getOperandMdx(operand.getOperand(1)));
			}
			else{
				buf.append(operand.getOperator().getSymbol());
				
				if (operand.getOperator() instanceof LiteralOperator || operand.getOperator() instanceof FasdMeasure){
					
				}
				else{
					buf.append("(");
					boolean first = true;
					for(int i = 0; i < operand.getOperator().getOperandNumber(); i++){
						if (first){
							first = false;
						}
						else{
							buf.append(", ");
						}
						buf.append(getOperandMdx(operand.getOperands()[i]));
					}
					buf.append(")");
				}
				
			}
			
		}
		
		return buf.toString();
	}
	
	
}
