package org.fasd.utils.trees;

import org.fasd.olap.OLAPCube;


public class TreeCube extends TreeParent {
	private OLAPCube cube;
	
	public TreeCube(OLAPCube c) {
		super(c.getName());
		cube = c;
	}
	
	public OLAPCube getOLAPCube() {
		return cube;
	}
}