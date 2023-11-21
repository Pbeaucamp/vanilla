package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Report of type BIRT
 * @author CHARBONNIER, MARTIN
 *
 */
public class BirtReport extends ReportObject {

	
	/**
	 * do not use, only for XML parsing 
	 */
	public BirtReport(){
		
	}
	
	/**
	 * Create a BIRT Report Object
	 * @param id of the directory item
	 * @param repository Server
	 */
	public BirtReport(int id) {
		super(id);
		
	}
	
	@Override
	public int getType() {
		return BIRT; 
	}

	@Override
	protected void setParameterNames() {
		

	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("birtObject");
		return e;
	}

}
