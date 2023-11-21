package org.fasd.utils.trees;

import org.fasd.olap.OLAPSchema;

public class TreeRoot extends TreeParent {
	private OLAPSchema sch;
	
	public TreeRoot(String lbl, OLAPSchema s) {
		super(lbl);
		this.sch = s;
	}
	
	public OLAPSchema getOLAPSchema() {
		return sch;
	}
}