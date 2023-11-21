package bpm.gateway.runtime2.tools;

import java.util.Calendar;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class StringParser {

	
	private IVanillaContext vanillaCtx;
	public StringParser(IVanillaContext vanillaContext){
		this.vanillaCtx = vanillaContext;
	}
	
	
	public String getValue(DocumentGateway gatewaymodel, String value) throws Exception{

		if (value.contains("{$ENV_") || value.contains("{$P_") || value.contains("{$VAN_") || value.contains("{$LOCAL_")){
			
			
			String s =  parseString(gatewaymodel, value);
			
			while(s.contains("{$ENV_") || s.contains("{$P_") || s.contains("{$VAN_") || s.contains("{$LOCAL_")){
				s =  parseString(gatewaymodel, s);
			}
			
			
			return s;
		}
		return value;
	}
	
	
	
	
	
	private String parseString(DocumentGateway gatewaymodel, String value) throws Exception{
		
		/*
		 * look for the type of variabe : Envirionment, Locale, Vanilla
		 */
		if (value.contains("{$ENV_")){
			int start = value.indexOf("{$") + 2;
			int end = value.indexOf("}", start);
			
			String variableName = value.substring(start, end);
			
			Variable v = null;

			
			if (gatewaymodel != null){
				v = gatewaymodel.getResourceManager().getVariable(Variable.ENVIRONMENT_VARIABLE, variableName.substring(4));
			}
			if ( v == null){
				v = ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, variableName.substring(4));
			}
			if (v == null){
				return value.substring(0, start - 2) +  value.substring(end + 1);
//				throw new Exception("Unable to find Environment Variable " + value);
			}
			
//			if (v.getType() == Variable.DATE || v.getType() == Variable.STRING){
//				return "'" + value.substring(0, start - 2) + v.getValue() + value.substring(end + 1) + "'";
//			}
//			else{
				return value.substring(0, start - 2) + v.getValue(null) + value.substring(end + 1);
//			}
			
			
		}
		else if (value.contains("{$P_")){
			int start = value.indexOf("{$") + 2;
			int end = value.indexOf("}", start);
			
			String variableName = value.substring(start, end);
			
			String s =  variableName.substring(2 );
			
			Parameter p = null;
			
			if (gatewaymodel != null){
				p = gatewaymodel.getResourceManager().getParameter(s);
			}
			
			if (p == null){
				return value.substring(0, start - 2) + value.substring(end + 1);
//				throw new Exception("Unable to find Environment Variable " + value);
			}
			
//			if (p.getType() == Parameter.STRING || p.getType() == Parameter.DATE){
//				return "'" + value.substring(0, start - 2) + p.getValue() + value.substring(end + 1) + "'";
//			}
//			else{
				return value.substring(0, start - 2) + p.getValue() + value.substring(end + 1);
//			}
			
			
		}
		else if (value.contains("{$VAN_")){
			int start = value.indexOf("{$") + 2;
			int end = value.indexOf("}");
			
			String variableName = value.substring(start, end);
			
			String s = variableName.substring(4);
			
			if (vanillaCtx == null){
				throw new Exception("Cannot get a Vanilla Variable without a VanillaContext ");
			}
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx.getVanillaUrl(), vanillaCtx.getLogin(), vanillaCtx.getPassword());
			
			return value.substring(0, start - 2) + vanillaApi.getVanillaSystemManager().getVariable(s).getValue() + value.substring(end + 1);
			
			
			
		}
		else if (value.contains("{$LOCAL_") && gatewaymodel != null){
			int start = value.indexOf("{$") + 2;
			int end = value.indexOf("}");
			
			String variableName = value.substring(start, end);
			
			String s = variableName.substring(6);
			for(Variable v : gatewaymodel.getVariables()){
				if ( v.getName().equals(s)){
					return value.substring(0, start - 2) + v.getValue(null) + value.substring(end + 1);
				}
			}
			
			
		}
		
		throw new Exception("Wrong Variable Origin for " + value);
		
	}
	
	public static void main(String[] args){
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("function dateDifference(date1, date2){\n");
		buf.append("     var d1 = new Date();\n");
		buf.append("     d1.setTime(date1);\n");
		buf.append("     var d2 = new Date();\n");
		buf.append("     d1.setTime(date2);\n");
		buf.append("     var delta = d1.getTime() - d2.getTime();\n");
		buf.append("     var finalDate = new Date();\n");
		buf.append("     finalDate.setTime(delta);\n");
		buf.append("     return finalDate.getTime();\n");
		
		buf.append("}\n");
		buf.append("dateDifference('"+ (Calendar.getInstance().getTimeInMillis() + 1000) + "', '" + Calendar.getInstance().getTimeInMillis() +  "');");
		
		
		
		Object result = cx.evaluateString(scope, buf.toString(), "<cmd>", 1, null);
; 
		System.out.println(result.toString());
	}
}
