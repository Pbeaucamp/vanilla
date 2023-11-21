package bpm.gateway.runtime2.transformation.calculation;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.calcul.Script;

/**
 * Class to represent a JavaScript Function stored in a Rhino Scope
 * Used at runtime when calling the different functions
 * @author ludo
 *
 */
public class ScriptMeta {
	private String functionName;
	private int parameterNumbers;
	private String[] parametersNames;
	private String functionDefinition;
	

	
	private ScriptMeta(String functionName, String[] parameterNames, String functionDefinition){
		this.functionName = functionName;
		this.parametersNames = parameterNames;
		this.parameterNumbers = parameterNames.length;
		this.functionDefinition = functionDefinition;
	}
	public String getFunctionName() {
		return functionName;
	}
	public int getParameterNumbers() {
		return parameterNumbers;
	}
	public String[] getParametersNames() {
		return parametersNames;
	}
	public String getFunctionDefinition() {
		return functionDefinition;
	}
	
	/**
	 * create a script Meta from a Script Object
	 * @param script
	 * @return
	 */
	public static ScriptMeta getScriptMeta(Script script) throws Exception{
		String toParse = new String(script.getScriptFunction());
		List<String> parametersName = new ArrayList<String>();
		
		int currentPos = 0;
				
		
		
		
		// look for parameters
		while(toParse.substring(currentPos).contains("{$")){
			int first = toParse.indexOf("{$",currentPos);
			int last = toParse.indexOf("}", first);
			String fieldName = toParse.substring(first + 2, last);
			parametersName.add(fieldName);
			last = last + 1;
			currentPos = last;

		}
		
		StringBuffer func = new StringBuffer();
		func.append("function _f_" + script.getName().replace(" ", "_") + "(");
		
		boolean first = true;
		for(String p : parametersName){
			if (first){
				first = false;
			}
			else{
				func.append(", ");
			}
			func.append( "_" + p.replace(" ", "_"));
		}
		func.append("){\n   ");
		
		String code = toParse;
		
		for(String p : parametersName){
			code = code.replace("{$" + p + "}", "_" + p.replace(" ", "_"));
		}
		
		code = code.replace("\n", "");
		func.append(code);
		func.append("}\n");
		
		ScriptMeta meta = new ScriptMeta("_" + script.getName().replace(" ", "_"), 
				parametersName.toArray(new String[parametersName.size()]), 
				func.toString());
		
		
		return meta;
	}
}
