package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;

public class ParameterManager extends ResourceManager<Parameter> {

	private static final String PARAMETER_FILE_NAME = "parameters.xml";

	public ParameterManager(String filePath) {
		super(filePath, PARAMETER_FILE_NAME, "Parameters");
	}

	@Override
	protected void manageResourceForAdd(Parameter resource) {
	}

	@Override
	protected void manageResourceForModification(Parameter newResource, Parameter oldResource) {
		String name = oldResource.getName();
		String question = oldResource.getQuestion();
		boolean useListOfValues = oldResource.useListOfValues();
		String value = oldResource.getValue();
		String defaultValue = oldResource.getDefaultValue();
		ListOfValues lov = oldResource.getListOfValues();
		
		newResource.updateInfo(name, question, useListOfValues, value, defaultValue, lov);
	}
}
