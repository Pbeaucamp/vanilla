package org.fasd.palo;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.IConnection;
import org.fasd.olap.Drill;
import org.fasd.olap.ICube;
import org.fasd.olap.ICubeView;

public class PALOCube implements ICube {

	private String name = "";
	private PALOSchema parent;
	private String description = "";
	
	public PALOSchema getParent(){
		return parent;
	}
	protected void setParent(PALOSchema sch){
		parent = sch;;
	}
	
	public IConnection getConnection() {
		return parent.getConnection();
	}

	public String getFAXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <cube>\n");
		buf.append("            <name>" + name + "</name>\n");		
		buf.append("        </cube>\n");
		
		return buf.toString();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return description;
	}
	public List<ICubeView> getCubeViews() {
		
		return null;
	}
	public String getFAProvider() {
		
		return null;
	}
	public String getFAType() {
		
		return "palo";
	}
	
	public List<Drill> getDrills() {
		return new ArrayList<Drill>();
	}
}
