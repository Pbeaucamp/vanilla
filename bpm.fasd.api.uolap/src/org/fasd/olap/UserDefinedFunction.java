package org.fasd.olap;

public class UserDefinedFunction {
	private String name = "";
	private String className = "";
	public UserDefinedFunction() {
		super();
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("    <UserDefinedFunction name=\"" + name + "\" class=\"" + className + "\"/>\n");
		return buf.toString();
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <UserDefinedFunction-item>\n");
		buf.append("            <name>" +  name + "</name>\n");
		buf.append("            <className>" +  className + "</className>\n");
		buf.append("        </UserDefinedFunction-item>\n");

		return buf.toString();
	}

}
