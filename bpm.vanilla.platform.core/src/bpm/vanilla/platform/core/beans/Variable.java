package bpm.vanilla.platform.core.beans;


//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;

public class Variable {
	public static final int SCOPE_SESSION = 0;
	public static final int SCOPE_GLOBAL = 1;
	
	public static final String[] SCOPE_NAMES = {"Session", "Global"};
	
	public static final String[] TYPE_NAMES = {"String", "Integer", "Double", "Float", "Date", "Boolean", "Object", "Long"};
	
	public static final String[] JAVA_CLASSES = {"java.lang.String","java.lang.Integer","java.lang.Double","java.lang.Float","java.util.Date","java.lang.Boolean","java.lang.Object", "java.lang.Long"};
	
	public static final int TYPE_INT  = 1;
	public static final int TYPE_STRING  = 0;
	public static final int TYPE_DOUBLE  = 2;
	public static final int TYPE_FLOAT  = 3;
	public static final int TYPE_DATE  = 4;
	public static final int TYPE_BOOLEAN  = 5;
	public static final int TYPE_OBJECT = 6;
	public static final int TYPE_LONG = 7;
	
	private Integer id;
	private String name;
	private String value;
	private Integer scope;
	private Integer type;
	
	private String source;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setStringId(String id) {
		if (id != null && !id.equalsIgnoreCase("null"))
			this.id = new Integer(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return the variable value
	 * 
	 * the value come from its source
	 * by default , the source is the value stored in database
	 */
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTypeName() {
		return TYPE_NAMES[type];
	}
	
	/**
	 * 
	 * @return the Type of the variable (intgeer, string and no more its scope)
	 */
	public Integer getType() {
		return type;
	}
	public Integer getScope() {
		return scope;
	}
	public void setScope(Integer scope) {
		this.scope = scope;
	}
	public void setScope(String scope){
		this.scope = Integer.parseInt(scope);
	}
	
	public void setType(String type) {
		this.type = new Integer(type);
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	

}
