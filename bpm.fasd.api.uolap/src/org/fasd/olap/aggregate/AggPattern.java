package org.fasd.olap.aggregate;

import java.util.ArrayList;
import java.util.List;

public class AggPattern extends AggregateTable{
	private String pattern = "";
	private List<AggExclude> excluded = new ArrayList<AggExclude>();
	
	
	public void addExcluded(AggExclude a){
		excluded.add(a);
	}
	
	public List<AggExclude> getExcluded(){
		return excluded;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("            <AggPattern pattern=\"" + pattern + "\">\n");
		for(AggExclude a : excluded)
			buf.append(a.getXML());
		buf.append("            </AggPattern>\n");
		return buf.toString();
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("            <AggPattern>\n");
		buf.append("                <pattern>"+pattern+"</pattern>\n");
		for(AggExclude a : excluded)
			buf.append(a.getFAXML());
		buf.append("            </AggPattern>\n");
		return buf.toString();
	}
	
}
