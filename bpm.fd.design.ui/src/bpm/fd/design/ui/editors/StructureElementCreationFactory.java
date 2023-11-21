package bpm.fd.design.ui.editors;

import org.eclipse.gef.requests.CreationFactory;

import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.IStructureElement;

public class StructureElementCreationFactory implements CreationFactory{

	private Class<?> structureClass;
	private FactoryStructure factory ;
	public StructureElementCreationFactory(FactoryStructure factory, Class<?> structureClass) {
		this.structureClass = structureClass;
		this.factory = factory;
	}

	public Object getNewObject() {
		try{
			
			return factory.create(structureClass);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return null;
		
	}

	public Object getObjectType() {
		return IStructureElement.class;
	}

}
