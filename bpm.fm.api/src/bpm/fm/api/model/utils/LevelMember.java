package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Level;

/**
 * A class representing a levelMember.
 * 
 * @author Marc
 * 
 */
public class LevelMember implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String value;
	private String label;
	private String geoId;
	
	/**
	 * used to map the parent child on elements from the same table
	 * we need to keep a list because we have to distinct the parent members
	 */
	private List<String> keys = new ArrayList<String>();

	private Level level;

	private LevelMember parent;

	private List<LevelMember> children = new ArrayList<LevelMember>();

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public LevelMember getParent() {
		return parent;
	}

	public void setParent(LevelMember parent) {
		this.parent = parent;
	}

	public List<LevelMember> getChildren() {
		return children;
	}

	public void setChildren(List<LevelMember> children) {
		for(LevelMember mem : children) {
			mem.setParent(this);
		}
		this.children = children;
	}

	public void addChild(LevelMember child) {
		child.setParent(this);
		children.add(child);
	}
	
	@Override
	public boolean equals(Object obj) {
		LevelMember member = (LevelMember) obj;
		
		if(this.value.equals(member.getValue()) && 
				this.level.getId() == member.getLevel().getId() && 
				this.parent == member.getParent()) {
			return true;
		}
		
		return false;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	
	public void addKey(String key) {
		this.keys.add(key);
	}

	public String getGeoId() {
		return geoId;
	}

	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
