package org.fasd.security;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPGroup;

@Deprecated
public class SecurityGroup extends OLAPGroup {
	private List<View> views = new ArrayList<View>();
	private static int counter = 0;
	//cube that use tjis role
	private List<OLAPCube> cubes = new ArrayList<OLAPCube>();
	
	
	public SecurityGroup(){
		counter ++;
		id = String.valueOf(counter);
	}
	public SecurityGroup(String n){
		super(n);
		counter++;
		id = String.valueOf(counter);
		
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void addView(View m){
		views.add(m);
//		update(this, m);
	}
	
	public void removeView(View m){
		views.remove(m);
//		update(this, m);
	}
	

	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <Security-group-item>\n");
		buf.append("            <name>" + getName() + "</name>\n");
		buf.append("            <id>" + getId() + "</id>\n");
		buf.append("            <description>" + getDesc() + "</description>\n");
		buf.append("            <level>" + getLevel() + "</level>\n");
		
		if (getParent() != null)
			buf.append("            <parent-id>" + getParent().getId() + "</parent-id>\n");
		else
			buf.append("            <parent-id></parent-id>\n");
		
		buf.append("        </Security-group-item>\n");
		
		return buf.toString();
	}
	
	public List<View> getViews(){
		return views;
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("    <Role name=\"" + getName() + "\">\n");
//		buf.append("        <SchemaGrant access=\"none\">\n");
//		
//		//getting secu for all cube using this role
//		for(OLAPCube c : cubes){
//			
//			for(SecurityGroup s : c.getSecurityGroups()){
//				if (s == this){
//					buf.append("           <CubeGrant cube=\"" + c.getName() + "\" access=\"all\">\n");
//					for(View v : s.getViews()){
//						buf.append(v.getXML());
//					}
//					buf.append("           </CubeGrant>\n");
//					break;
//				}
//			}
//			
//			
//		}
//		buf.append("        </SchemaGrant>\n");
		buf.append("    </Role>\n");
		
		return buf.toString();
	}

	
	public void addCube(OLAPCube cube){
		cubes.add(cube);
	}
	
	public void removeCube(OLAPCube cube){
		cubes.remove(cube);
	}
}
