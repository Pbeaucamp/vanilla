package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

public class Variable extends Resource {
	
	//This pattern is use to stock the source file name
	public static final String FILE_NAME_PATTERN = "$NOM_DE_FICHIER";

	private VariableString value;
	private String runtimeValue;

	private boolean isDate;
	private VariableString datePattern;

	public Variable() {
		super("", TypeResource.VARIABLE);
	}

	public Variable(String name) {
		super(name, TypeResource.VARIABLE);
	}

	public boolean containsVariables() {
		return (value != null && value.containsVariables()) || (datePattern != null && datePattern.containsVariables());
	}

	public VariableString getValueVS() {
		return value;
	}

	public String getValueDisplay() {
		return value.getStringForTextbox();
	}

	public void setValue(VariableString value) {
		this.value = value;
	}

	public String getValue(List<Parameter> parameters, List<Variable> variables) {
		return value.getString(parameters, variables);
	}

	public String getVariableName() {
		return "{$V_" + getName() + "}";
	}

	public void setRuntimeValue(String runtimeValue) {
		this.runtimeValue = runtimeValue;
	}

	public String getRuntimeValue() {
		return runtimeValue;
	}

	public boolean isDate() {
		return isDate;
	}

	public void setDate(boolean isDate) {
		this.isDate = isDate;
	}

	public VariableString getDatePatternVS() {
		return datePattern;
	}

	public String getDatePatternDisplay() {
		return datePattern.getStringForTextbox();
	}

	public void setDatePattern(VariableString datePattern) {
		this.datePattern = datePattern;
	}

	public String getDatePattern(List<Parameter> parameters, List<Variable> variables) {
		return datePattern != null ? datePattern.getString(parameters, variables) : null;
	}
	
	public boolean isFileNameVariable() {
		return value.getStringForTextbox().equals(FILE_NAME_PATTERN);
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(value != null ? value.getVariables() : new ArrayList<Variable>());
		variables.addAll(datePattern != null ? datePattern.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(value != null ? value.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(datePattern != null ? datePattern.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

	public void updateInfo(String name, VariableString value, VariableString datePattern) {
		setName(name);
		this.value = value;
		this.datePattern = datePattern;
	}
}
