package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Object of type Workflow
 * @author Charles MARTIN
 *
 */
public class BIWObject extends BiRepositoryObject{

	public int id;
	/**
	 * do not use, only for parsing XML
	 */
	public BIWObject(){super();}
	
	public BIWObject(int id) {
		super(id);
		this.id = id;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("BIWObject");

		return e;
	}
	
	/**
	 * 
	 * @return the id of the directory item
	 */
	public int getIDItem(){
		return id;
	}
	
	@Override
	protected void setParameterNames() {
		
		
	}





}
