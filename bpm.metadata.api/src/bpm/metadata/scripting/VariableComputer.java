package bpm.metadata.scripting;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;


public class VariableComputer {

	
	
	
	public  HashMap<Variable, String> computeVariablesValues(IVanillaContext vanillaCtx, List<Variable> variables, Script script, String group, HashMap<Variable, String> variableValues){
		
		
		HashMap<Variable, String> result = new HashMap<Variable, String>();
		
		String code = new String(script.getDefinition());
		
		String[] codeLines = code.split(";");
		
		for(String s : codeLines){
			Variable var = null;
			
			for(Variable _v : variables){
				if (s.trim().startsWith(_v.getSymbol())){
					var = _v;
					break;
				}
			}
			
			String operation = s.substring(s.indexOf("=") + 1).trim();
//			operation = operation.substring(0, operation.indexOf(';'));
			
			try {
				result.put(var, evaluateOperation(vanillaCtx, operation, group, result, script, variableValues));
			} catch (Exception e) {
				
//				e.printStackTrace();
			}
		}
		
		
		return result;
		
		
	}
	
	
	private static String evaluateOperation(IVanillaContext vanillaCtx, String operation, String groupName, HashMap<Variable, String> values, Script script, HashMap<Variable, String> variableValues) throws Exception{
		
		if(script.getConnection() != null) {
		
			VanillaJdbcConnection connection = script.getConnection().getJdbcConnection();
			
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx.getVanillaUrl(), vanillaCtx.getLogin(), vanillaCtx.getPassword());
			
			User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaCtx.getLogin());
			
			operation = operation.replaceAll(PrebuiltFunction.User_Function.name() + "\\(\\)", user.getFunction());
			operation = operation.replaceAll(PrebuiltFunction.User_Id.name() + "\\(\\)", user.getId() + "");
			operation = operation.replaceAll(PrebuiltFunction.User_SkypeName.name() + "\\(\\)", user.getSkypeName() + "");
			
			for(Variable var : variableValues.keySet()) {
				if(!variableValues.get(var).startsWith("(")) {
					operation = operation.replace(var.getSymbol(), " '" + variableValues.get(var) + "' ");
				}
				else {
					operation = operation.replace(var.getSymbol(), " " + variableValues.get(var) + " ");
				}
			}
			
			VanillaPreparedStatement stmt = connection.prepareQuery(operation); 
			ResultSet rs = stmt.executeQuery();
			
			StringBuilder result = new StringBuilder();
			
			boolean first = true;
			
			boolean isString = rs.getMetaData().getColumnType(1) == Types.VARCHAR || rs.getMetaData().getColumnType(1) == Types.NVARCHAR;
			while(rs.next()) {
				
				String val = rs.getString(1);
				if(first) {
					first = false;
				}
				else {
					result.append(",");
				}
				result.append(isString ? "'" + val + "'" : val);
			}
			
			stmt.close();
			
			ConnectionManager.getInstance().returnJdbcConnection(connection);
			
			return "(" + result.toString() + ")";
		
		}
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx.getVanillaUrl(), vanillaCtx.getLogin(), vanillaCtx.getPassword());
		
		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaCtx.getLogin());
		operation = operation.replaceAll(PrebuiltFunction.User_Function.name() + "\\(\\)", user.getFunction());
		operation = operation.replaceAll(PrebuiltFunction.User_Id.name() + "\\(\\)", user.getId() + "");
		operation = operation.replaceAll(PrebuiltFunction.User_SkypeName.name() + "\\(\\)", user.getSkypeName() + "");
		
		for(Variable var : variableValues.keySet()) {
			if(!variableValues.get(var).startsWith("(")) {
				operation = operation.replace(var.getSymbol(), " '" + variableValues.get(var) + "' ");
			}
			else {
				operation = operation.replace(var.getSymbol(), " " + variableValues.get(var) + " ");
			}
		}
		
		if(operation.toLowerCase().startsWith("select")) {
			return "(" + operation + ")";
		}
		
		return "('" + operation + "')";
		
//		/*
//		 * getting group value
//		 */
//		if (operation.startsWith(PrebuiltFunction.Group_Value.name())){
//			String[] args = operation.substring(operation.indexOf("(") + 1, operation.lastIndexOf(")")).split(",");
//			
//			String[] argsValues = new String[args.length];
//			
//			for(int i = 0; i < args.length; i++){
//				argsValues[i] = evaluateOperation(vanillaCtx, args[i].trim(), groupName, values);
//			}
//			
//			if (vanillaCtx == null){
//				throw new Exception("The VanillaContext is null and one is required because the prebuilt scripting function for VanillaGroup custo value is used ");
//			}
//			else{
//				try{
//					IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx.getVanillaUrl(), vanillaCtx.getLogin(), vanillaCtx.getPassword());
//					Group g = vanillaApi.getVanillaSecurityManager().getGroupByName(argsValues[1]);
//					
//					return g.getCustom1();
//				}catch(Exception ex){
//					throw new Exception("Error when getting the VanillaGroup custom1 value for GroupName=" + groupName + " - " + ex.getMessage(), ex);
//				}
//			}
//			
//			
//			
//		}
//		/*
//		 * getting group Name
//		 */
//		else if (operation.startsWith(PrebuiltFunction.Group_Name.name()+"()")){
//			return groupName;
//		}
//		 /*
//		 * string literal
//		 */
//		else if (operation.startsWith("'") && operation.endsWith("'")){
//				return operation.substring(1, operation.length() - 1);
//		}
//		
//		else{
//			for(Variable v : values.keySet()){
//				if (operation.trim().equals(v.getSymbol())){
//					return values.get(v);
//				}
//			}
//			
//			return operation;
//		}
	}
}
