package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public abstract class OLAPGroup extends OLAPElement{
	private String desc = "";
	private int level = 0;
	private OLAPGroup parent;
	private String parentId = "";

	private List<OLAPGroup> childs = new ArrayList<OLAPGroup>();
	
	public OLAPGroup(String n){
		super(n);
	}
	public OLAPGroup(){
		super();
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
//		update(this, desc);
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
//		update(this, level);
	}
	
	public void setLevel(String level){
		this.level = Integer.valueOf(level);
//		update(this, level);
	}
	
	public OLAPGroup getParent() {
		return parent;
	}
	
	public void setParent(OLAPGroup parent) {
		this.parent = parent;
		if (parent != null){
			parentId = parent.getId();
			if (!parent.getChilds().contains(this))
				parent.addChild(this);
		}
//		update(this, parent);
	}
	
	
	public List<OLAPGroup> getChilds() {
		return childs;
	}

	public void addChild(OLAPGroup child){
		childs.add(child);
		child.setParent(this);
		child.setLevel(level + 1);
	}
	
	public void removeChild(OLAPGroup child){
		childs.remove(child);
	}
	
	

	
	public boolean inGroup(OLAPElement e){
		return childs.contains(e);
	}
	

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
//		update(this, desc);
	}
	
	public abstract String getFAXML();
}
