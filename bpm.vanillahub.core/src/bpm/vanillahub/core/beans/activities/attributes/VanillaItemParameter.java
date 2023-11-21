package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class VanillaItemParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeParameter {
		TYPE_INT, TYPE_STRING, TYPE_DOUBLE, TYPE_FLOAT, TYPE_DATE, TYPE_BOOLEAN, TYPE_OBJECT, TYPE_LONG;
	}

	private String name;
	private TypeParameter type;
	private VariableString value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeParameter getType() {
		return type;
	}

	public void setType(TypeParameter type) {
		this.type = type;
	}

	public VariableString getValue() {
		return value;
	}

	public void setValue(VariableString value) {
		this.value = value;
	}
	
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(value != null ? value.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(value != null ? value.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

}
