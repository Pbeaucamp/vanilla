package bpm.vanillahub.core.beans.resources.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class WebServiceParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeParameter {
		TYPE_INT, TYPE_STRING, TYPE_DOUBLE, TYPE_FLOAT, TYPE_DATE, TYPE_BOOLEAN, TYPE_OBJECT, TYPE_LONG;
	}

	private String name;
	private String parentPath;
	
	private TypeParameter type;
	private String definition;

	private Date dateValue;
	private Boolean booleanValue;
	private VariableString variableStringValue;

	public WebServiceParameter() {
	}

	public WebServiceParameter(String name, String parentPath, TypeParameter type, String definition) {
		this.name = name;
		this.parentPath = parentPath;
		this.type = type;
		this.definition = definition;
	}

	public String getName() {
		return name;
	}
	
	public String getParentPath() {
		return parentPath;
	}

	public TypeParameter getType() {
		return type;
	}

	public String getDefinition() {
		return definition;
	}

	public void setValue(Object parameterValue) {
		if (parameterValue instanceof Date) {
			 this.dateValue = (Date) parameterValue;
		}
		else if (parameterValue instanceof Boolean) {
			 this.booleanValue = (Boolean) parameterValue;
		}
		else if (parameterValue instanceof VariableString) {
			 this.variableStringValue = (VariableString) parameterValue;
		}
	}

	public Object getParameterValue() {
		switch (type) {
		case TYPE_BOOLEAN:
			return booleanValue;
		case TYPE_DATE:
			return variableStringValue != null ? variableStringValue : dateValue;
		case TYPE_DOUBLE:
		case TYPE_FLOAT:
		case TYPE_INT:
		case TYPE_LONG:
		case TYPE_OBJECT:
		case TYPE_STRING:
			return variableStringValue;
		default:
			break;
		}
		return null;
	}

	public Object getParameterValue(List<Parameter> parameters, List<Variable> variables) {
		switch (type) {
		case TYPE_BOOLEAN:
			return booleanValue;
		case TYPE_DATE:
			return variableStringValue != null ? variableStringValue.getString(parameters, variables) : dateValue;
		case TYPE_DOUBLE:
		case TYPE_FLOAT:
		case TYPE_INT:
		case TYPE_LONG:
		case TYPE_OBJECT:
		case TYPE_STRING:
			return variableStringValue.getString(parameters, variables);
		default:
			break;
		}
		return null;
	}

	public List<Parameter> getWorkflowParameters() {
		return variableStringValue != null ? variableStringValue.getParameters() : new ArrayList<Parameter>();
	}
	
	public List<Variable> getWorkflowVariables() {
		return variableStringValue != null ? variableStringValue.getVariables() : new ArrayList<Variable>();
	}
}
