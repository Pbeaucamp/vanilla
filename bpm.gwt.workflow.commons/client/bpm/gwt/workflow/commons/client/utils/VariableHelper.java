package bpm.gwt.workflow.commons.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class VariableHelper {

	public static void setVariableStringValue(VariableString variableString, String string, List<Parameter> parameters, List<Variable> variables) {
		List<Parameter> newParams = new ArrayList<Parameter>();
		List<Variable> newVariables = new ArrayList<Variable>();

		RegExp regExp = RegExp.compile("\\{(.*?)\\}");
		MatchResult m = regExp.exec(string);
		if (m != null) {
			for (int i = 0; i < m.getGroupCount(); i++) {
				String group = m.getGroup(i);
				if (group.contains("$P")) {
					Parameter parameter = findParameter(parameters, group);
					if (parameter != null) {
						newParams.add(parameter);
					}
				}
				else {
					Variable variable = findVariable(variables, group);
					if (variable != null) {
						newVariables.add(variable);
					}
				}
			}
		}
		
		variableString.setString(string, newParams, newVariables);
	}

	private static Parameter findParameter(List<Parameter> parameters, String group) {
		if (parameters != null) {
			for (Parameter param : parameters) {
				if (param.getParameterName() != null && param.getParameterName().equals(group)) {
					return param;
				}
			}
		}
		return null;
	}

	private static Variable findVariable(List<Variable> variables, String group) {
		if (variables != null) {
			for (Variable variable : variables) {
				if (variable.getVariableName() != null && variable.getVariableName().equals(group)) {
					return variable;
				}
			}
		}
		return null;
	}
}
