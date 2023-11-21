package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public class OLAPDimensionGroup extends OLAPGroup {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
//	private String desc;
//	private int level = 0;
//	private OLAPDimensionGroup parent;
//	private String parentId;
//
//	private List<OLAPDimensionGroup> childs = new ArrayList<OLAPDimensionGroup>();
	private List<OLAPDimension> dim = new ArrayList<OLAPDimension>();
	
	public OLAPDimensionGroup(){counter ++; setId("e" + String.valueOf(counter));}
	public OLAPDimensionGroup(String n){
		super(n);
		counter ++;
		setId("e" + String.valueOf(counter));
		
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <Dimension-group-item>\n");
		buf.append("            <name>" + getName() + "</name>\n");
		buf.append("            <id>" + getId() + "</id>\n");
		buf.append("            <description>" + getDesc() + "</description>\n");
		buf.append("            <level>" + getLevel() + "</level>\n");
		
		if (getParent() != null)
			buf.append("            <parent-id>" + getParent().getId() + "</parent-id>\n");
		else
			buf.append("            <parent-id></parent-id>\n");
		
		buf.append("        </Dimension-group-item>\n");
		
		return buf.toString();
	}
	
	
	public List<OLAPDimension> getDimensions(){
		return dim;
	}
	
	public void addDimension(OLAPDimension m){
		dim.add(m);
//		update(this, m);
	}
	
	public void removeDim(OLAPDimension m){
		dim.remove(m);
//		update(this, m);
	}

}
