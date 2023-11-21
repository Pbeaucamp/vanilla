package org.fasd.olap.aggregate;

public class AggExclude {
	private String name = "";
	private String pattern = "";
	private boolean ignoreCase = true;
	
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	public void setIgnoreCase(String ignoreCase) {
		this.ignoreCase = Boolean.parseBoolean(ignoreCase);
	}
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                <AggExclude pattern=\"" + pattern + "\"");
		if (!name.trim().equals(""))
			buf.append(" name=\"" + name+ "\"");
		if (!ignoreCase)
			buf.append(" ignorecase=\"false\"");
		buf.append("/>\n");
		return buf.toString();
	}

	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                <AggregateExclude>\n");
		buf.append("                    <name>" + name + "</name>\n");
		buf.append("                    <pattern>" + pattern + "</pattern>\n");
		buf.append("                    <ignoreCase>" + ignoreCase + "</ignoreCase>\n");
		buf.append("                </AggregateExclude>\n");
		return buf.toString();
	}
}
