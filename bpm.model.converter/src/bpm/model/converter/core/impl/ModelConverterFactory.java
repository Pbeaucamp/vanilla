package bpm.model.converter.core.impl;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;

import bpm.model.converter.core.IModelConverter;
import bpm.model.converter.core.IModelConverterFactory;

public class ModelConverterFactory implements IModelConverterFactory{
	private IConfigurationElement configElement;
	private String targetClassName;
	private String description;
	private String converterName;
	
	
	public ModelConverterFactory(IConfigurationElement configElement){
		this.configElement = configElement;
		
		targetClassName = configElement.getAttribute("targetModelClass");
		description = configElement.getAttribute("description");
		converterName = configElement.getAttribute("name");
		
	}


	@Override
	public IModelConverter createConverter() throws Exception{
		return (IModelConverter)configElement.createExecutableExtension("converterClass");
	}


	@Override
	public String getConverterDescription() {
		return description;
	}


	@Override
	public String getConverterName() {
		return converterName;
	}


	

	@Override
	public String getTargetClassName() {

		return targetClassName;
	}
}
