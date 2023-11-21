package bpm.gateway.core.server.userdefined;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IVanillaAPI;


/**
 * 
 * @author LCA
 *
 */
public class Variable {

	public static final int ENVIRONMENT_VARIABLE = 0;
	public static final int LOCAL_VARIABLE = 1;
	public static final int VANILLA_VARIABLE = 2;
	public static final String[] VARIABLES_NAME = new String[]{"ENVIRONMENT", "LOCAL", "VANILLA"};
	
	public static final String[] VARIABLES_TYPES = new String[]{"String", "Integer", "Float", "Date", "Boolean"};
	
	public static final int STRING = 0;
	public static final int INTEGER = 1;
	public static final int FLOAT = 2;
	public static final int DATE = 3;
	public static final int BOOLEAN = 4;
	
	
	
	
	private int type;
	
	private int scope = 0;
	
	private String definitionValue = "";
	private String name;
	private Object value; //for localVariable
	
	
	public enum SQLTYPE {
		LONG(0),
		INTEGER(1), 
		DOUBLE(2), 
		VARCHAR(3); 
//		DATE(4);
		
		private int type;

		private SQLTYPE(int type) {
			this.type = type;
		}
		
		public int getType() {
			return this.type;
		}
		
		public String getTypeName() {
			switch (this) {
			case INTEGER:
				return "Integer";
			
//			case DATE:
//				return "Date";
			
			case DOUBLE:
				return "Double";
			
			case VARCHAR:
				return "String";
			
			case LONG:
				return "Long";
				
			default:
				return "";
			}
		}
		
		public String getJavaClassName() {
			switch (this) {
			case INTEGER:
				return "java.lang.Integer";
			
//			case DATE:
//				return "java.util.Date";
			
			case DOUBLE:
				return "java.lang.Double";
			
			case VARCHAR:
				return "java.lang.String";
			
			case LONG:
				return "java.lang.Long";
				
			default:
				return "";
			}
		}

		public static SQLTYPE getTypeFromValue(String type) {
			if(type.equals("Integer")){
				return SQLTYPE.INTEGER;
			}
//			else if(type.equals("Date")){
//				return SQLTYPE.DATE;
//			}
			else if(type.equals("Double")){
				return SQLTYPE.DOUBLE;
			}
			else if(type.equals("String")){
				return SQLTYPE.VARCHAR;
			}
			else {
				return SQLTYPE.LONG;
			}
		}
	}
	
	public void setType(Integer type){
		this.type = type;
	}
	
	public void setType(String type){
		try{
			this.type = Integer.parseInt(type);
		}catch(NumberFormatException e){
			
		}
	}
	
	public Integer getType(){
		return type;
	}
	
	

	
	
	
	/**
	 * 
	 * @return the value of the Object 
	 */
	public Object getValue(IVanillaAPI contextualVanillaApi) throws Exception{
		String v = null;
		
		if (value != null){
			return value;
		}
		
		if (scope == ENVIRONMENT_VARIABLE ){
			v = definitionValue;
		}
		else if (scope == VANILLA_VARIABLE){
			v = contextualVanillaApi.getVanillaSystemManager().getVariable(getName()).getValue();
				
		}
		
		switch(type){
		case STRING:
			return v;
		case INTEGER:
			return Integer.parseInt(v);
		case FLOAT:
			return Float.parseFloat(v);
		case DATE:
			return null;
		}
		
		return null;
	}
	

	public void setCurrentValue(Object value) throws Exception{
		if (getScope() == ENVIRONMENT_VARIABLE || getScope() == VANILLA_VARIABLE){
			throw new Exception("The scope variable not allow to define values" );
		}
		this.value = value;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("variable");
		
		e.addElement("name").setText(getName());
		e.addElement("value").setText(definitionValue);
		e.addElement("dataType").setText(type + "");
		e.addElement("scope").setText(this.scope + "");
		
		return e;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	

	public int getScope(){
		return scope;
	}

	public void setScope(int environmentVariable) {
		this.scope = environmentVariable;
		
	}
	
	public void setScope(String environmentVariable) {
		try{
			this.scope = Integer.parseInt(environmentVariable);
		}catch(NumberFormatException e){
			
		}
		
	}

	public void setValue(String value) {
		this.definitionValue = value;
		
	}
	
	public String getValueAsString(){
		return definitionValue;
	}

	public String getOuputName() {
		switch(scope){
		case ENVIRONMENT_VARIABLE : 
			return "{$ENV_" + getName() + "}";
		}
		
		return "{$" + getName() + "}";
	}

	

	

	
	
	

}
