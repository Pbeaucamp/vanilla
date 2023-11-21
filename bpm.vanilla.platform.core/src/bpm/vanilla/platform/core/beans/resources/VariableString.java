package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VariableString implements Serializable {

	private static final long serialVersionUID = 1L;

	private String string = "";

	private List<Variable> variables;
	private List<Parameter> parameters;

	public VariableString() {
	}

	public VariableString(String value) {
		this.string = value;
	}

	public VariableString(int value) {
		this.string = String.valueOf(value);
	}

	public String getStringForTextbox() {
		return string;
	}

	public boolean containsVariables() {
		return variables != null && !variables.isEmpty();
	}

	private boolean containsParameters() {
		return parameters != null && !parameters.isEmpty();
	}

	public String getString(List<Parameter> parameters, List<Variable> variables) {
		String value = string;
		if (variables != null && containsVariables()) {
			for (Variable var : variables) {
				if (value != null && var.getRuntimeValue() != null) {
					value = value.replace(var.getVariableName(), var.getRuntimeValue());
				}
			}
		}

		if (parameters != null && containsParameters()) {
			for (Parameter param : parameters) {
				if (value != null && param.getValue() != null) {
					value = value.replace(param.getParameterName(), param.getValue());
				}
			}
		}

		return value;
	}

	public void setString(String string, List<Parameter> parameters, List<Variable> variables) {
		this.string = string;

		this.parameters = parameters;
		this.variables = variables;
	}

	public void addVariable(String value, Variable variable) {
		if (variables == null) {
			this.variables = new ArrayList<Variable>();
		}
		this.variables.add(variable);

		this.string = value + variable.getVariableName();
	}

	public void addParameter(String value, Parameter parameter) {
		if (parameters == null) {
			this.parameters = new ArrayList<Parameter>();
		}
		this.parameters.add(parameter);

		this.string = value + parameter.getParameterName();
	}

	public List<Variable> getVariables() {
		return variables != null ? variables : new ArrayList<Variable>();
	}

	public List<Parameter> getParameters() {
		List<Parameter> parametersResult = new ArrayList<Parameter>();
		parametersResult.addAll(parameters != null ? parameters : new ArrayList<Parameter>());

		if (variables != null) {
			for (Variable var : variables) {
				parametersResult.addAll(var.getParameters());
			}
		}
		return parametersResult;
	}
}
