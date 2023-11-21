package bpm.gateway.runtime2.transformation.calculation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

import common.Logger;

public class RuntimeCalculation extends RuntimeStep{
	private Class<?>[] integerClasses = {Integer.class, Long.class, BigDecimal.class};
	
	private static String PREBUILT_FUNCTIONS = "";
	
//	private List<ScriptMeta> metas = new ArrayList<ScriptMeta>();
	private List<String> columnNames = new ArrayList<String>();
	private List<String> inputColumnNames = new ArrayList<String>();
	
//	private static ScriptEngine mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	
	static{
		StringBuffer buf = new StringBuffer();
		
		buf.append("function dateDifference(date1, date2){\n");
		buf.append("     var d1 = new Date();\n");
		buf.append("     d1.setTime(date1);\n");
		buf.append("     var d2 = new Date();\n");
		buf.append("     d2.setTime(date2);\n");
		buf.append("     var d3 = new Date();\n");
		buf.append("     d3.setTime(date1-date2);\n");
		buf.append("     return d3;\n");
		
		buf.append("}\n");
		
		
		
		
		PREBUILT_FUNCTIONS = buf.toString();
	}
	
	
	
	public RuntimeCalculation(Calculation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		Calculation calcul = (Calculation)getTransformation();
		
		StringBuffer jsFunction = new StringBuffer();
		jsFunction.append(PREBUILT_FUNCTIONS);
		jsFunction.append("\n\n");
		
//		for(Script sc : calcul.getScripts()){
//			try{
//				ScriptMeta m = ScriptMeta.getScriptMeta(sc);
//				metas.add(m);
//				debug(" prepared JavaScript function : " + m.getFunctionDefinition());
//			}catch(Exception ex){
//				error(" error when creating ScriptMeta", ex);
//			}
//		}
//		info(" functions parsed");
//		
		if (!calcul.getInputs().isEmpty()){
			for(StreamElement e : calcul.getInputs().get(0).getDescriptor(calcul).getStreamElements()){
				inputColumnNames.add(e.name);
			}
		}
		
		for(StreamElement e : calcul.getDescriptor(calcul).getStreamElements()){
			columnNames.add(e.name);
		}
		
		
		
		info(" inited");
		isInited = true;
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
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this, row);
		
//		context = ContextFactory.getGlobal().enterContext();
		int k =0;
		
		for(Script sc : ((Calculation)getTransformation()).getScripts()){
			
			String test = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), sc.getScriptFunction());
			String formula = PREBUILT_FUNCTIONS + "\r\n" + test;
			try{
				/*
				 * replace the columns Field name owned by this Step 
				 */
				for(int i = 0; i < newRow.getMeta().getSize(); i++){
					if (Date.class.isAssignableFrom(newRow.getMeta().getJavaClasse(i)) && newRow.get(i) instanceof Date){
						if (newRow.get(i) != null){
							formula = formula.replace("{$" + columnNames.get(i) + "}", "new Date(" + ((Date)newRow.get(i)).getTime() + ")");
						}

						
					}
					else if (String.class.isAssignableFrom(newRow.getMeta().getJavaClasse(i))){
						if (newRow.get(i) == null ){
							formula = formula.replace("{$" + columnNames.get(i) + "}", "undefined");
						}
						else if(newRow.get(i).equals("") ){
							formula = formula.replace("{$" + columnNames.get(i) + "}", "''");
						}
						else{
							if (newRow.get(i).toString().contains("'")){
								formula = formula.replace("{$" + columnNames.get(i) + "}", "\""+ URLEncoder.encode(newRow.get(i).toString(), "UTF-8") + "\"");
							}
							else{
								formula = formula.replace("{$" + columnNames.get(i) + "}", "'"+ URLEncoder.encode(newRow.get(i).toString(), "UTF-8") + "'");
							}
						}
						
					}
					else{
						if (newRow.get(i) == null ){
							formula = formula.replace("{$" + columnNames.get(i) + "}", "undefined");
						}
						else if(newRow.get(i).equals("") ){
							formula = formula.replace("{$" + columnNames.get(i) + "}", "''");
						}
						formula = formula.replace("{$" + columnNames.get(i) + "}", newRow.get(i) + "");	
					}
				}
				
				/*
				 * replace the columns Field name coming from the input
				 */
				if (!getTransformation().getInputs().isEmpty()){
					for(int i = 0; i < row.getMeta().getSize(); i++){
						if (Date.class.isAssignableFrom(row.getMeta().getJavaClasse(i)) && row.get(i) instanceof Date){
							if (row.get(i) != null ){
								formula = formula.replace("{$" + inputColumnNames.get(i) + "}", "new Date(" + ((Date)row.get(i)).getTime() + ")");
							}
							
						}
						else if (String.class.isAssignableFrom(row.getMeta().getJavaClasse(i))){
							if (row.get(i) == null ){
								formula = formula.replace("{$" + inputColumnNames.get(i) + "}", "undefined");
							}
							else if(newRow.get(i).equals("") ){
								formula = formula.replace("{$" + columnNames.get(i) + "}", "''");
							}
							else{
								if (row.get(i).toString().contains("'")){
									formula = formula.replace("{$" + inputColumnNames.get(i) + "}", "\""+ URLEncoder.encode(row.get(i).toString(), "UTF-8") + "\"");
								}
								else{
									formula = formula.replace("{$" + inputColumnNames.get(i) + "}", "'"+ URLEncoder.encode(row.get(i).toString(), "UTF-8") + "'");
								}
							}
							
						}
						else{
							if (newRow.get(i) == null ){
								formula = formula.replace("{$" + columnNames.get(i) + "}", "undefined");
							}
							else if(newRow.get(i).equals("") ){
								formula = formula.replace("{$" + columnNames.get(i) + "}", "''");
							}
							formula = formula.replace("{$" + inputColumnNames.get(i) + "}", row.get(i) + "");	
						}
					}
				}
				
				if(formula != null && formula.contains("new Date()")) {
					formula = formula.replace("new Date()", "new java.util.Date()");
				}
				
				Context cx = ContextFactory.getGlobal().enterContext();
				
				Object result = null;
				try {
					Scriptable scope = cx.initStandardObjects();
					result = cx.evaluateString(scope, formula, "<cmd>", 1, null);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					Context.exit();
				}
				
				
				Class<?> c = newRow.getMeta().getJavaClasse(row.getMeta().getSize() + k);

				
				Object o = null;
				if(!(result instanceof Undefined)) {
						
					
					try{
						if (c == BigInteger.class){
							
							for(int i = 0 ; i < integerClasses.length &&  o==null; i++){
								try{
									o = JavaAdapter.convertResult(result, integerClasses[i]);
								}catch(EvaluatorException ex){
									
								}
							}
							if ( o == null){
								try{
									o = JavaAdapter.convertResult(result, c);
								}catch(EvaluatorException ex){
									
								}
								
								if ( o instanceof Double){
									o = ((Double)o).longValue();
								}
							}
						}
						else{
							o = JavaAdapter.convertResult(result, c);
						}
						
					}catch(EvaluatorException ex){
						try{
							if (Number.class.isAssignableFrom(c)){
								o = JavaAdapter.convertResult(result, Date.class);
								o = ((Date)o).getTime();
							}
						}catch(Exception ex2){
							
						}
						
					}
					if (o instanceof String){
						try{
							o = URLDecoder.decode((String)o, "UTF-8");
						}catch(Exception ex){
							warn("UTF-8 decryption problems encountered on "  + o + " : "+ ex.getMessage(), ex);
						}
						
					}
				}
				if (o instanceof String){
					try{
						o = URLDecoder.decode((String)o, "UTF-8");
					}catch(Exception ex){
						o = (String)o;
//						warn("UTF-8 decryption problems encountered on "  + o + " : "+ ex.getMessage(), ex);
					}
					
				}
				newRow.set(row.getMeta().getSize() + k, o);

				
				k++;
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Error when evaluting formula :" + formula.substring((PREBUILT_FUNCTIONS + "\r\n").length()), ex);
			}
				
			
		}
		
		
		
		writeRow(newRow);
//		context.exit();
	}

	@Override
	public void releaseResources() {
		
		
		info(" resources released");
		
	}

}
