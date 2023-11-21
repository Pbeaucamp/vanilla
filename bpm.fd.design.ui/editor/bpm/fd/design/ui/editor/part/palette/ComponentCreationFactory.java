package bpm.fd.design.ui.editor.part.palette;

import org.eclipse.gef.requests.CreationFactory;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class ComponentCreationFactory implements CreationFactory {
		
	private Class<?> template;
	private IComponentDefinition def;
	
	ComponentCreationFactory(Class<?> template){
		this.template = template;
	}
	public ComponentCreationFactory(IComponentDefinition def) {
		this.def = def;
	}
	@Override
	public Object getNewObject() {
		if (template == null){
			return def;
		}
		return template;
	}
	
	@Override
	public Object getObjectType() {
		if (template != null){
			return template;
		}
		return def.getClass();
	}

}
