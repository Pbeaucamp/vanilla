package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Report of type FWR
 * @author CHARBONNIER, MARTIN
 *
 */
public class FwrObject extends ReportObject {

	
	/**
	 * do not use, only for XML parsing 
	 */
	public FwrObject(){
		
	}
	
	/**
	 * Create a FWR Object
	 * @param id of the directory item
	 * @param repository Server
	 */
	public FwrObject(int id) {
		super(id);
		
	}

	public int getType() {
		return FWR; 
	}

	@Override
	protected void setParameterNames() {
		
		
	}

	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("fwrObject");
		return e;
	}
}
