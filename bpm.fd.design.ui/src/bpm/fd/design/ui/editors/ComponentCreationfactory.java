package bpm.fd.design.ui.editors;

import org.eclipse.gef.requests.CreationFactory;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class ComponentCreationfactory implements CreationFactory{
	
	private IComponentDefinition component;
	
	public ComponentCreationfactory(IComponentDefinition def){
		this.component = def;
	}
	
	public Object getNewObject() {
		return component;
	}

	public Object getObjectType() {
		
		return null;
	}

	public IComponentDefinition getComponent(){
		return component;
	}
}
