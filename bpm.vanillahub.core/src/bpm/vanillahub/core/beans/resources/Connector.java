package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;

public class Connector extends Resource {

	public Connector() {
		super("", TypeResource.CONNECTOR);
	}
	
	public Connector(String name) {
		super(name, TypeResource.CONNECTOR);
	}

	@Override
	public List<Variable> getVariables() {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

}
