package bpm.gateway.core.server.userdefined;

import java.text.SimpleDateFormat;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * This class is for define a Parameter for the Parameter.
 * The Parameetr value can be used inside Transformation as name for file output, 
 * or as value on some Transformation(Calculation, Filters, ...)
 * @author LCA
 *
 */
public class Parameter {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String[] PARAMETER_TYPES = new String[]{"String", "Integer", "Float", "Date", "Boolean"};
	
	public static final int STRING = 0;
	public static final int INTEGER = 1;
	public static final int FLOAT = 2;
	public static final int DATE = 3;
	public static final int BOOLEAN = 4;
	
	
	private String defaultValue = "";
	private String value = null;
	private String name;
	private int type;
	
	/**
	 * 
	 * @return the Default Value fro this Parameter
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Set the value .
	 * Must be used just before starting the Runtime of a Gateway model
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @return the parameter name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the parameter Name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return on of the Parameter.Constants
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * set the parametr type
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	
	public void setType(String type) {
		this.type = Integer.parseInt(type);
	}
	
	/**
	 * 
	 * @return the value of this parameter (for runtime)
	 * @throws Exception
	 */
	public Object getValue() throws Exception{
		
		String v = value;
		
		if (value == null){
			v = defaultValue;
		}
		switch(type){
		case STRING:
			return v;
		case INTEGER:
			return Integer.parseInt(v);
		case FLOAT:
			return Float.parseFloat(v);
		case DATE:
			return sdf.parse(v);
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @return the outputName of this parameter : 
	 * if the parameter is used inside a Transformation Field, this one should be used
	 * otherwise it wont be parsed rightly at runtime to replace it by its value
	 */
	public String getOuputName() {
		return "{$P_" + getName() + "}";

	}
	public Element getElement() {
		Element parameter = DocumentHelper.createElement("parameter");
		parameter.addElement("name").setText(getName());
		parameter.addElement("type").setText(type + "");
		parameter.addElement("defaultValue").setText(defaultValue);
		
		return parameter;
	}
	
	
	/**
	 * 
	 * @return the value as A String
	 */
	public String getValueAsString() {
		return value == null ? defaultValue : value;
	}
	
}
