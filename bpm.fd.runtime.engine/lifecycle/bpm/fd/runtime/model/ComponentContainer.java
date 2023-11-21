package bpm.fd.runtime.model;

import bpm.fd.api.core.model.structure.IStructureElement;

/**
 * This class must be used by all the element of a ashboard taht act as a container.
 * @author ludo
 *
 * @param <T>
 */
public class ComponentContainer<T extends IStructureElement> extends ComponentRuntime{

	public ComponentContainer(T container){
		super(container);
	}

	
	public T getComponentDefinition(){
		return (T)getElement();
	}
}
