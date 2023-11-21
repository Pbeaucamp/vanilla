package bpm.workflow.runtime.resources.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.resources.IResource;

/**
 * Variable used in the workspace
 * @author CHARBONNIER, MARTIN
 *
 */
public class Variable implements IResource {

	private String id;
	private int type = 0;
	private String name = "";
	private List<String> values = new ArrayList<String>();
	
	public static final int STRING = 0;
	public static final int FLOAT = 1;
	public static final int INTEGER = 2;
	public static final int BOOLEAN = 3;
	public static final int ENUMERATION = 4;
	public static final int DATE = 5;
	
	public static final String[] TYPES_NAMES = {"String", "Float", "Integer", "Boolean", "Enumeration", "Date"};
	
	public Variable(){
		
	}
	
	/**
	 * Create a variable
	 * @param prop : name, type , default (for the value)
	 */
	public Variable (Properties prop) {
		String n = prop.getProperty("name");
		if (!(name.endsWith("}") && name.startsWith("{$"))) {
			n = "{$" + n + "}";
		}
		setName(n);
		setType(new Integer(prop.getProperty("type")));
		addValue(prop.getProperty("default"));
	}
	
	/**
	 * donot use, only for parsing XML
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	

	public String getId() {
		return id;
	}

	public String getName() {
		if (name.equalsIgnoreCase("")) {
			return "";
		}
//		if (!name.endsWith("}")) {
//			name += "}";
//		}
//		if (!name.startsWith("{$")) {
//			name = "{$" + name;
//		}
		return name;
	}
	public void setName(String name) {

		this.name = name;
		setId(name.replace(" ", "_").replace("{", "").replace("$", "").replace("}", ""));
	}
	
	/**
	 * Add a value to the variable
	 * @param value
	 */
	public void addValue(String value) {
		addValue(value, false);
	}
	
	/**
	 * Add a value to the variable
	 * @param value
	 */
	public void addValue(String value, boolean forceAdd) {
		if (type == ENUMERATION)
			values.add(value);
		else {
			if (!forceAdd || values == null) {
				values = new ArrayList<String>();
			}
			values.add(value);
		}
	}
	
	/**
	 * 
	 * @return the type of the variable
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set the type of the variable
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Set the type of the variable
	 * @param type
	 */
	public void setType(String type) {
		this.type = new Integer(type);
	}

	/**
	 * 
	 * @return the values contained in the variable
	 */
	public List<String> getValues() {
		return values;
	}
	
	public String getLastValue() {
		if(values != null && !values.isEmpty()) {
			return values.get(values.size() - 1);
		}
		return null;
	}

	/**
	 * Set the values which will be contained in the variable
	 * @param values
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("variable");
		e.setName("variable");
		e.addElement("id").setText(id);
		e.addElement("name").setText(name);
		e.addElement("type").setText(type+"");
		
		if (!values.isEmpty()) {
			Element va = DocumentHelper.createElement("values");
			for (String v : values) {
				va.addElement("value").setText(v);
			}
			e.add(va);
		}
		
		return e;
	}

	public List<Element> toXPDL() {
		List<Element> l = new ArrayList<Element>();
		
		Element var =  DocumentHelper.createElement("DataField").addAttribute("Id", id).addAttribute("Name",  name);
		
		switch (type) {
		case STRING:
			var.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			var.addElement("InitialValue").setText(values.get(0));	
			break;
			
		case INTEGER:
			var.addElement("DataType").addElement("BasicType").addAttribute("Type", "INTEGER");
			try {
				Integer inte = new Integer(values.get(0));
				var.addElement("InitialValue").setText(inte + "");
			}
			catch (Exception e) {
				var.addElement("InitialValue").setText(0 + "");
			}
				
			break;
			
		case BOOLEAN:
			var.addElement("DataType").addElement("BasicType").addAttribute("Type", "BOOLEAN");
			var.addElement("InitialValue").setText(values.get(0));	
			break;	

		case DATE:
			var.addElement("DataType").addElement("BasicType").addAttribute("Type", "DATETIME");
			var.addElement("InitialValue").setText(values.get(0));	
			break;	
			
		case FLOAT:
			var.addElement("DataType").addElement("BasicType").addAttribute("Type", "FLOAT");
			var.addElement("InitialValue").setText(values.get(0));	
			break;
			
			
		default:
			break;
		}
			
		
		l.add(var);
		
		return l;
	
	}
	
	@Override
	public String toString() {
		return "varName = " + this.getName() + " -> values = " + values;
	}
}
