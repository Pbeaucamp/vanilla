package bpm.workflow.commons.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.i18n.Labels;

public class VariableHelper {

	public static List<Variable> initVariables(IVanillaLogger logger, Locale locale, List<Parameter> parameters, List<Variable> variables, List<Variable> currentVariables) {
		List<Variable> variablesResult = new ArrayList<Variable>();
		for (Variable var : variables) {
			try {
				variablesResult.add(initVariable(logger, locale, var, parameters, currentVariables, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return variablesResult;
	}

	private static Variable initVariable(IVanillaLogger logger, Locale locale, Variable variable, List<Parameter> parameters, List<Variable> currentVariables, boolean useCurrentVariable) throws Exception {
		if (!useCurrentVariable) {
			variable = findVariable(logger, variable, currentVariables);
		}

		List<Variable> childVariables = new ArrayList<Variable>();
		if (variable.containsVariables()) {
			for (Variable childVariable : variable.getVariables()) {
				childVariables.add(initVariable(logger, locale, childVariable, parameters, currentVariables, false));
			}
		}

		Object value = getValue(locale, variable, parameters, childVariables);
		variable.setRuntimeValue(value.toString());
		return variable;
	}

	/**
	 * We check if the variables exist in the resource file, if not we return
	 * the variable from the workflow
	 * 
	 * @param variable
	 * @param currentVariables
	 * @return
	 */
	private static Variable findVariable(IVanillaLogger logger, Variable variable, List<Variable> currentVariables) {
		for (Variable var : currentVariables) {
			if (var.getId() == variable.getId()) {
				return var;
			}
		}

		logger.warn("The variable '" + variable.getName() + "' is not available in resources. We take the one from the workflow.");

		return variable;
	}

	public static String getValue(Locale locale, Variable variable, List<Parameter> parameters, List<Variable> variables) throws Exception {
		if (variable.isFileNameVariable())  {
			return variable.getValueDisplay();
		}
		
		String formula = variable.getValue(parameters, variables);
		if (formula != null && formula.contains("new Date()")) {
			formula = formula.replace("new Date()", "new java.util.Date()");
		}

		Context cx = ContextFactory.getGlobal().enterContext();
		try {
			Scriptable scope = cx.initStandardObjects();
			Object value = cx.evaluateString(scope, formula, "<cmd>", 1, null);
			if (variable.isDate()) {
				value = (Date) Context.jsToJava(value, Date.class);

				String pattern = variable.getDatePattern(parameters, variables);
				if (pattern == null || pattern.isEmpty()) {
					throw new Exception(Labels.getLabel(locale, Labels.DatePatternNotDefine));
				}

				SimpleDateFormat df = new SimpleDateFormat(pattern);
				return df.format(value);
			}
			else {
				return value.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Labels.getLabel(locale, Labels.ScriptNotValid) + " : " + e.getMessage());
		} finally {
			Context.exit();
		}
	}

	public static void testVariable(IVanillaLogger logger, Locale locale, Variable variable, List<Variable> currentVariables) throws Exception {
		List<Parameter> parameters = variable.getParameters();
		if (parameters != null) {
			for (Parameter param : parameters) {
				param.setValueString(param.getDefaultValue());
			}
		}

		initVariable(logger, locale, variable, parameters, currentVariables, true);
	}
}
