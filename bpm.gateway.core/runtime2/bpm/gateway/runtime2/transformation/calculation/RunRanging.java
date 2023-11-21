package bpm.gateway.runtime2.transformation.calculation;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.calcul.Range;
import bpm.gateway.core.transformations.calcul.RangingTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunRanging extends RuntimeStep{
	private static final String SYMBOL_VALUE = "{$VALUE}";
	
	private Integer targetIndex ;
	private String script ;
	private Context context;
	private Scriptable scope;
	

	public RunRanging(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		RangingTransformation transfo = (RangingTransformation)getTransformation();
		
		targetIndex = transfo.getTargetIndex();
		if (targetIndex == null || targetIndex.intValue() >= transfo.getDescriptor(transfo).getColumnCount()){
			String s = " cannot perform a ranging operation if no Field to test is defined or if its index is bigger than its fields number";
			info(s);
			
			throw new Exception(s);
		}
		
		try{
			script = parse();
			info(" comparaison Script is built");
			debug(" script :\r\n" + script);
		}catch(Exception e){
			error(" unable to build the comparaison script",e);
			throw e;
		}
		context = Context.enter();
		info(" context created");
		
		scope = context.initStandardObjects();
		info(" scope created");
		
		isInited = true;
		info(" inited");
		
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			Thread.sleep(10);
			return;
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this, row);
		context = ContextFactory.getGlobal().enterContext();
		scope = context.initStandardObjects();
		String parsed = script.replace(SYMBOL_VALUE, row.get(targetIndex) + "");
		
		Object result = context.evaluateString(scope, parsed, "<cmd>", 1, null);
		newRow.set(newRow.getMeta().getSize() - 1, result);
		
		writeRow(newRow);
		context.exit();
	}

	@Override
	public void releaseResources() {
		Context.exit();
		info("context closed");
		info(" resources released");
		
	}

	
	private String parse() throws Exception{
		RangingTransformation transfo = (RangingTransformation)getTransformation();
		StringBuffer buf = new StringBuffer();

		
		String value = SYMBOL_VALUE;
	
		boolean isFirst = true;
		
		for(Range r : transfo.getRanges()){
			
			if (isFirst){
				isFirst = false;
			}
			else{
				buf.append(" else ");
			}
			
			String output = (transfo.getType() == Variable.STRING || transfo.getType() == Variable.DATE) ?  "'" + r.getOutput() + "'" : r.getOutput();
			
			boolean isMinFixed = true;
			try{
				Double.parseDouble(r.getFirstValue());
			}catch(Exception e){
				isMinFixed = false;
			}
			
			
			boolean isMaxFixed = true;
			try{
				Double.parseDouble(r.getSecondValue());
			}catch(Exception e){
				isMaxFixed = false;
			}
			
			switch(r.getIntervalType()){
			//]a,b[
			case 0:
				if (isMinFixed && isMaxFixed){
					buf.append("if (" + r.getFirstValue() + "<" + value + "&&" + value + "<" + r.getSecondValue() + ") " + output + ";");
				}
				else if (isMinFixed){
					buf.append("if (" + r.getFirstValue() + "<" + value + ")" + output + ";");
				}
				else if (isMaxFixed){
					buf.append("if (" +  value + "<" + r.getSecondValue() + ")" + output + ";");
				}
				else{
					buf.append(" " + output);
				}

				break;
			//]a,b]
			case 1:
				if (isMinFixed && isMaxFixed){
					buf.append("if (" + r.getFirstValue() + "<" + value + "&&" + value + "<=" + r.getSecondValue() + ") " + output + ";");
				}
				else if (!isMaxFixed){
					buf.append("if (" +  value + "<=" + r.getSecondValue() + ") " + output + ";");
				}

				break;
			//[a,b]
			case 2:
				buf.append("if (" + r.getFirstValue() + "<=" + value + "&&" + value + "<=" + r.getSecondValue() + ") " + output + ";");
				break;
				
			//[a,b[
			case 3:
				if (isMinFixed && isMaxFixed){
					buf.append("if (" + r.getFirstValue() + "<=" + value + "&&" + value + "<" + r.getSecondValue() + ") " + output + ";");
				}
				else if (!isMaxFixed){
					buf.append("if (" + r.getFirstValue() + "<=" + value + ") " + output + ";");
				}
				
				break;
			//default
			case 4:	
				buf.append(" " + output + ";");
			}
			
			
		}
		
		
		
	
		return buf.toString();
		
	}
}
