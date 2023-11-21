package bpm.gwt.workflow.commons.client.workflow.properties;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;

public interface PanelWithVariables {

	public void setVariables(List<Variable> variables, List<Parameter> parameters);
}
