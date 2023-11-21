package org.fasd.utils.trees;

import org.fasd.olap.OLAPLevel;

public class TreeLevel extends TreeParent {
	private OLAPLevel level;
	
	public TreeLevel(OLAPLevel lvl) {
		super(lvl.getName());
		level = lvl;
	}
	
	public OLAPLevel getOLAPLevel() {
		return level;
	}
}
