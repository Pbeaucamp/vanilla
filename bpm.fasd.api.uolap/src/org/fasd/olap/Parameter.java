package org.fasd.olap;

public class Parameter {
	private String name = "";
	private String desc = "";
	private String type = "";
	private boolean modifable = false;
	private String defaultValue = "";
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isModifable() {
		return modifable;
	}
	public void setModifable(boolean modifable) {
		this.modifable = modifable;
	}
	
	public void setModifable(String modifable) {
		this.modifable = Boolean.parseBoolean(modifable);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                <Parameter name=\""+name+"\"");
		if (!type.equals("")){
			buf.append(" type=\""+type+"\"");
		}
		if (!desc.equals("")){
			buf.append(" description=\"" + desc + "\"");
		}
		if(!modifable){
			buf.append(" modifiable=\"false\"");
		}
		if (!defaultValue.equals(""))
			buf.append(" defaultValue=\"" + defaultValue+ "\"");
		buf.append("/>\n");
		
		
		
		return buf.toString();
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("            <Parameter>\n"); 
		buf.append("                <name>" + name + "</name>\n");
		buf.append("                <description>" + desc + "</description>\n");
		buf.append("                <type>" + type + "</type>\n");
		buf.append("                <modifiable>" + modifable + "</modifiable>\n");
		buf.append("                <defaultValue>" + defaultValue + "</defaultValue>\n");
		buf.append("            </Parameter>\n");
		
		return buf.toString();
	}
	
}
