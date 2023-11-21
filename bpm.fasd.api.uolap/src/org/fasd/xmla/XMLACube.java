package org.fasd.xmla;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.IConnection;
import org.fasd.olap.Drill;
import org.fasd.olap.ICube;
import org.fasd.olap.ICubeView;

public class XMLACube implements ICube {

	private String name = "";
	private String description = "";
	private XMLASchema parent;
	
	private List<ICubeView> cubeViews = new ArrayList<ICubeView>();
	
	public XMLASchema getParent(){
		return parent;
	}
	protected void setParent(XMLASchema sch){
		parent = sch;;
	}
	


	public void setName(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getFAXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <cube>\n");
		buf.append("            <name>" + name + "</name>\n");		
		buf.append("        </cube>\n");
		
		return buf.toString();
	}

	public IConnection getConnection() {
		return parent.getConnection();
	}
	
	
	public String getDescription(){
		return description;
	}
	public List<ICubeView> getCubeViews() {
		return cubeViews;
	}
	public String getFAProvider() {
		return parent.getConnection().getType();
	}
	public String getFAType() {
		
		return "xmla";
	}
	
	public List<Drill> getDrills() {
		return new ArrayList<Drill>();
	}
}
