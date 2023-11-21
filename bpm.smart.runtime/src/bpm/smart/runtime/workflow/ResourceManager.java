package bpm.smart.runtime.workflow;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.resources.Cible;

public class ResourceManager {

	private List<Cible> cibles;
	private List<Variable> variables;
	private List<Parameter> parameters;

	public List<Cible> getCibles() {
		return cibles;
	}

	public void setCibles(List<Cible> cibles) {
		this.cibles = cibles;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

}
