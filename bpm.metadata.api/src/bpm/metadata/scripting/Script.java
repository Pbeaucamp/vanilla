package bpm.metadata.scripting;

import com.thoughtworks.xstream.XStream;

import bpm.metadata.layer.physical.sql.SQLConnection;

/**
 * A script that will be executed before to construct the SQL query
 * The variable used by a script may be used to customize a query
 * @author ludo
 *
 */
public class Script {
	private String definition = "";
	private String name;
	private String description = "";
	
	private SQLConnection connection;
	
	public Script(){}

	/**
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
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
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append("<script>\n");
		buf.append("    <name>" + getName() + "</name>\n");
		buf.append("    <description>" + getDescription() + "</description>\n");
		buf.append("    <definition><![CDATA[" + getDefinition() + "]]></definition>\n");
		buf.append("    <datasource><![CDATA[" + new XStream().toXML(getConnection()) + "]]></datasource>\n");
		buf.append("</script>\n");
		
		return buf.toString();
	}
	
	public void setDatasource(String ds) {
		setConnection((SQLConnection) new XStream().fromXML(ds));
	}
	
	/**
	 * 
	 * @param v
	 * @return true if the script use the given variable
	 */
	public boolean isUsing(Variable v){
		return getDefinition().contains(v.getSymbol());
	}

	public SQLConnection getConnection() {
		return connection;
	}

	public void setConnection(SQLConnection connection) {
		this.connection = connection;
	}
}
