package org.fasd.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPElement;


@Deprecated
public class SecurityDim extends OLAPElement {
	private static int counter = 0;
	
	private String desc ="";
	private OLAPDimension dim;
	private String dimId;
	private HashMap<String, View> views = new HashMap<String,View>();
	
	
	public SecurityDim(){
		super("");
		counter++;
		id = "j" + String.valueOf(counter);
		
	}
	
	public SecurityDim(String name){
		super(name);
		counter++;
		id = "j" + String.valueOf(counter);
		
	}
		
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public OLAPDimension getDim() {
		return dim;
	}
	public void setDim(OLAPDimension dim) {
		this.dim = dim;
	}
	public String getDimId() {
		return dimId;
	}
	public void setDimId(String dimId) {
		this.dimId = dimId;
	}
	
	public Set<String> getViewsId(){
		return views.keySet();
	}
	
	public List<View> getListViews(){
		List<View> result = new ArrayList<View>();
		for(View v : views.values()){
			result.add(v);
		}
		return result;
	}
	
	public Collection<View> getViews(){
		return views.values();
	}
	
	public void addView(View v){
		v.setParent(this);
		views.put(v.getId(), v);
	}
	
	public void removeView(View v){
		views.remove(v.getId());
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		if (dim == null)
			return "";
		buf.append("    <DimensionView-item>\n");
		buf.append("        <id>" + getId() + "</id>\n");
		buf.append("        <name>" + getName() + "</name>\n");
		buf.append("        <description>" + getDesc() + "</description>\n");
		buf.append("        <dimension-id>" + dim.getId() + "</dimension-id>\n");
		for(View v : getListViews()){
			buf.append(v.getFAXML());
		}
		buf.append("    </DimensionView-item>\n");
		return buf.toString();
	}

	public View findView(String string) {
		return views.get(string);
	}
	

}
