package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Object of type Gateway
 * @author CAMUS, CHARBONNIER, MARTIN
 *
 */
public class GatewayObject extends BiRepositoryObject {
	
	/**
	 * do not use, only for parsing XML
	 */
	public GatewayObject(){
		super();
	}
	
	/**
	 * Create a gateway object
	 * @param id of the directory item
	 * @param repository Server
	 */
	public GatewayObject(int id) {
		super(id);
		
	}
	
	
	

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("gatewayObject");

		return e;
	}


	@Override
	protected void setParameterNames() {
		
		
	}

}
