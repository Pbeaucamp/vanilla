package bpm.workflow.runtime.model;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Represent a Parameter for the execution of the Model.
 * 
 * The main purpose is to use those to provide parameter value to sub-workflow
 * @author ludo
 *
 */
public class WorkfowModelParameter {
	private String name = "";
	private String defaultValue = "";
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Element getXmlNode(){
		Element e = DocumentHelper.createElement("workflowModelParameter");
		e.addElement("name").setText(name);
		e.addElement("defaultValue").setText(defaultValue);
		return e;
	}

	public Element toXPDL() {
		Element e =  DocumentHelper.createElement("DataField").addAttribute("Id",  getName()).addAttribute("Name",   getName());
		e.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		e.addElement("InitialValue").setText(getDefaultValue());
		return e;
	}
	
}
