package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public class OLAPMeasureGroup extends OLAPGroup {
	private static int counter = 0;
	public static void resetCounter(){
		counter = 0;
	}
	
	private List<OLAPMeasure> mes = new ArrayList<OLAPMeasure>();
	
	public OLAPMeasureGroup(){counter ++; setId("f" + String.valueOf(counter));}
	public OLAPMeasureGroup(String n){
		super(n);
		counter ++;
		setId("f" + String.valueOf(counter));
		
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public void addMeasure(OLAPMeasure m){
		mes.add(m);
//		update(this, m);
	}
	
	public void removeMes(OLAPMeasure m){
		mes.remove(m);
//		update(this, m);
	}
	

	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <Measure-group-item>\n");
		buf.append("            <name>" + getName() + "</name>\n");
		buf.append("            <id>" + getId() + "</id>\n");
		buf.append("            <description>" + getDesc() + "</description>\n");
		buf.append("            <level>" + getLevel() + "</level>\n");
		
		if (getParent() != null)
			buf.append("            <parent-id>" + getParent().getId() + "</parent-id>\n");
		else
			buf.append("            <parent-id></parent-id>\n");
		
		buf.append("        </Measure-group-item>\n");
		
		return buf.toString();
	}
	
	public List<OLAPMeasure> getMeasures(){
		return mes;
	}

}
