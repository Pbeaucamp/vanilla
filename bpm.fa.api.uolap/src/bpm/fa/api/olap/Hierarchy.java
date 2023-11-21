package bpm.fa.api.olap;

import java.util.ArrayList;

import bpm.fa.api.olap.unitedolap.UnitedOlapMember;
import bpm.fa.api.utils.log.Log;

public class Hierarchy extends Element {
	private ArrayList<Level> lvl = new ArrayList<Level>();
	private OLAPMember defaultmb;
	private Level first = null;
	private boolean isParentChild = false;
	
	
	public Hierarchy(String name, String uname, String caption, OLAPMember dmb/*, boolean isParentChild*/) {
		super(name, uname, caption);
//		this.isParentChild = isParentChild;
		this.defaultmb = dmb;
		
		Log.info("Loaded " + this.getClass().getName() + " uname = " + getUniqueName());
	}
	
	public boolean isParentChild(){
		return isParentChild;
	}
	
	public void setParentChild(boolean value){
		this.isParentChild = value;
	}
	
	public void setDefaultMember(OLAPMember m) {
		this.defaultmb = m;
	}
	
	public void addLevel(Level l)throws Exception {
//		if (isParentChild && !lvl.isEmpty()){
//			throw new Exception("A ParentChild Hierarchy cannot have more than one Level");
//		}
		if (first == null) {
			first = l;
			//curr = l;
		}
		else {
			first.setLastLevel(l);
		}
		
		lvl.add(l);
	}
	
	public ArrayList<Level> getLevel() {
		return lvl;
	}
	
	public Level getFirstLevel() {
		return first;
	}
	
	public OLAPMember findMember(OLAPMember mb) {
		return first.findMember(mb);
	}
	
	public OLAPMember getDefaultMember() {
		return defaultmb;
	}
	
	
	public Hierarchy clone(){
		Hierarchy h = new Hierarchy(getName(), getUniqueName(), getCaption(), null);
		h.setParentChild(isParentChild);
		
		Level curr = getFirstLevel();
		Level clone = null;
		while(curr != null){
			clone = curr.clone();
			if (first == null){
				first = clone;
			}
			try {
				h.addLevel(clone);
			} catch (Exception e) {
				
			}
			curr = curr.getNextLevel();
		}
		
		return h;
	}
}
