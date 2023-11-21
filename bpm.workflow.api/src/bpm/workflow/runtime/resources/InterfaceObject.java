package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Create an object which is an interface (Currently : Orbeon or FD)
 * @author CHARBONNIER, MARTIN
 *
 */
public class InterfaceObject extends BiRepositoryObject {
	
	public InterfaceObject() {
		super();
	}
	
	public InterfaceObject(int id) {
		super(id);	
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("interfaceObject");
		return e;
	}
	
	@Override
	protected void setParameterNames() {
		
		
	}

}
