package bpm.fa.api.olap;

import java.util.ArrayList;
import java.util.Iterator;

import bpm.fa.api.utils.log.Log;

public class Level extends Element {
	private Level nextlvl = null;
	private ArrayList<OLAPMember> elems = new ArrayList<OLAPMember>();
	private int lvlnb;
	
	public Level(String name, String uname, String caption, int nb) {
		super(name, uname, caption);
		
		lvlnb = nb;
		
		Log.info("Loaded " + this.getClass().getName() + " uname = " + getUniqueName());
	}
	
	public int getLevelNumber(){
		return lvlnb;
	}
	
	public void addMember(OLAPMember e) {
		elems.add(e);
	}
	
	public ArrayList<OLAPMember> getMembers() {
		return elems;
	}
	
	public Level getNextLevel() {
		return nextlvl;
	}
	
	public void setLastLevel(Level lvl) {
		if (nextlvl == null)
			nextlvl = lvl;
		else
			nextlvl.setLastLevel(lvl);
		//nextlvl = lvl;
	}
	
	public OLAPMember findMember(OLAPMember mb) {
		Iterator<OLAPMember> it = elems.iterator();
		
		while (it.hasNext()) {
			OLAPMember buf = it.next().findMember(mb);
			
			if (buf != null)
				return buf;
		}
		
		return null;
	}

	/**
	 * dunot use, just in cas of hyperionProvider because its XMLA sucks
	 * @param level
	 */
	public void setNextLevel(Level level) {
		this.nextlvl = level;
	}
	
	public Level clone(){
		Level l = new Level(getName(), getUniqueName(), getCaption(), lvlnb);

		return l;
	}
}
