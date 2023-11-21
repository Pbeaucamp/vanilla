package org.fasd.olap;

import org.fasd.export.IExportable;

public class NamedSet implements IExportable {
	private static int counter = 0; 
	private String name = "";
	private String formulas = "";
	private String id;
	private boolean global = false;
	
	public NamedSet(){
		counter ++;
		id = String.valueOf(counter);
	}
	
	public void setFormula(String formula){
		formulas = formula;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <NamedSet name=\"" + name + "\">\n");
	
			buf.append("            <Formula>\n");
			buf.append("                " + formulas + "\n");
			buf.append("            </Formula>\n");
	
		buf.append("        </NamedSet>\n");
		return buf.toString();
	}
	
	
	public String getFAXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <NamedSet-item>\n");
		buf.append("            <id>" + id + "</id>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <global>" + global + "</global>\n");
		buf.append("            <Formula>\n");
			buf.append("                " + formulas + "\n");
			buf.append("            </Formula>\n");
		
		buf.append("        </NamedSet-item>\n");
		return buf.toString();
	}

	public String getId() {
		return id;
	}

		
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGlobal(){
		global = true;
	}
	
	public void setGlobal(String global){
		this.global = Boolean.valueOf(global);
	}
	
	public boolean isGlobal(){
		return global;
	}
}
