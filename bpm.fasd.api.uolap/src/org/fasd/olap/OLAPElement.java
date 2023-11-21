package org.fasd.olap;


import org.fasd.export.IExportable;

public class OLAPElement implements IExportable{
	private String name;
	protected String id;
	
	public OLAPElement(){
		name = "";
	}
	
	public OLAPElement(String name) {
		this.name = "" + name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = "" + name.replace(".", "");

	}

	public String getFAXML() {
		return null;
	}

	public String getId() {
		return id;
	}
}
