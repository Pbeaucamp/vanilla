package bpm.workflow.runtime.resources;

import java.util.List;

import org.dom4j.Element;

/**
 * Interface of a resources
 * @author CHARBONNIER, CAMUS, MARTIN
 *
 */
public interface IResource {
	/**
	 * 
	 * @return the id of the resource
	 */
	public String getId();
	/**
	 * 
	 * @return the name of the resource
	 */
	public String getName();
	/**
	 * 
	 * @return the XPDL generated for the resource
	 */
	public List<Element> toXPDL();
	/**
	 * 
	 * @return the XML generated for the resource
	 */
	public Element getXmlNode();
}
