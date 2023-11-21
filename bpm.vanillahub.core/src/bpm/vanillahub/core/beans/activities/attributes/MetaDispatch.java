package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class MetaDispatch implements Serializable {

	private static final long serialVersionUID = 1L;

	private String metaKey;
	private VariableString value = new VariableString();
	
	public MetaDispatch() {
	}
	
	public MetaDispatch(String metaKey, VariableString value) {
		this.metaKey = metaKey;
		this.value = value;
	}
	
	public String getMetaKey() {
		return metaKey;
	}
	
	public VariableString getValue() {
		return value;
	}
	
	public String getValue(List<Parameter> parameters, List<Variable> variables) {
		return value.getString(parameters, variables);
	}
}
