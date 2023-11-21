package bpm.vanilla.platform.core.runtime.alerts.conditions;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import bpm.dataprovider.odainput.consumer.OdaHelper;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class ConditionParser {
	
	public static ConditionResult parseCondition(String condition, OdaInput input) throws Exception {
		String c = condition;
		List<String> fields = new ArrayList<String>();
		
		for (int i = 0; i < condition.length(); i++) {	
			if (condition.charAt(i) == "$".charAt(0) && i+1 < condition.length()
					&& condition.charAt(i+1) == "F".charAt(0)) {				
				String field = condition.substring(i+3);
				field = field.substring(0, field.indexOf("}"));
				fields.add(field);
			}
		}
		int currentType = -1;
		for (String f : fields) {
			List<List<Object>> values = OdaHelper.getValues(input, -1, f);
			Object value = values.get(values.size() - 1).get(0);
			int type = OdaHelper.getType(input, f);
			if (currentType == -1) {
				currentType = type;
			}
			else if (currentType != type) {
				throw new Exception("Unable to compare " + OdaHelper.getJavaClassName(currentType) + " and " + OdaHelper.getJavaClassName(type) + " in condition " + c);
			}
			condition = condition.replace("$F{" + f + "}", "'" +  value + "'");
		}
		
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		Object result = cx.evaluateString(scope, condition, "<cmd>", 1, null);
		
		
		ConditionResult cr = new ConditionResult();
		cr.setResult(result);
		cr.setType(currentType);
		
		return cr;
	}
	
	
	public static void main(String[] args) {

		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		Object result = cx.evaluateString(scope, "34.25", "<cmd>", 1, null);
		System.out.println(result);
	}

}
