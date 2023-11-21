package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;

public interface DataServiceAttribute extends Serializable {
	
	public List<Variable> getVariables(List<?> resources);

	public List<Parameter> getParameters(List<?> resources);

	public boolean isValid();

	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables);
	
	public String getOutputName(List<Parameter> parameters, List<Variable> variables);
}
