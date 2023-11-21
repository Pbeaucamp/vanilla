package bpm.fd.design.ui.editors;

import org.eclipse.gef.requests.CreationFactory;

public class ComponentFactory implements CreationFactory{

	private Class<?> template ;
	
	public ComponentFactory(Class<?> template){
		this.template = template;
	}
	public Object getNewObject() {
				
		return template;
	}

	public Object getObjectType() {
		
		return null;
	}

}
