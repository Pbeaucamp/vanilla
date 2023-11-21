package bpm.metadata.scripting;

/**
 * Represent a Variable defined by a script
 * an usable in SQLFilter 
 * 
 * @author ludo
 *
 */
public class Variable {
	private VariableType type;
	private String name;
	private String symbol;
	
	public Variable(){}

	/**
	 * @return the type
	 */
	public VariableType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(VariableType type) {
		this.type = type;
	}
	
	/**
	 * @param typeName the type to set
	 */
	public void setType(String typeName){
		this.type = VariableType.valueOf(typeName);
	}

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
		this.symbol = "{$" + getName() + "}";
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append("<variable>\n");
		buf.append("    <name>" + getName() + "</name>\n");
		buf.append("    <type>" + getType() + "</type>\n");
		buf.append("</variable>\n");
		
		return buf.toString();
	}
	
}
