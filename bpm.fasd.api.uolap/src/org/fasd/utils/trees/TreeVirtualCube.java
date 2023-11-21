package org.fasd.utils.trees;

import org.fasd.olap.virtual.VirtualCube;

public class TreeVirtualCube extends TreeParent {
	private VirtualCube cube;
	public TreeVirtualCube(VirtualCube vCube) {
		super(vCube.getName());
		cube = vCube;
	}
	
	public VirtualCube getVirtualCube(){
		return cube;
	}

}
