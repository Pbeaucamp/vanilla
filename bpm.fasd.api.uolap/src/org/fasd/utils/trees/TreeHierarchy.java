package org.fasd.utils.trees;

import org.fasd.olap.OLAPHierarchy;

public class TreeHierarchy extends TreeParent {
	private OLAPHierarchy hierarchy;
	
	public TreeHierarchy(OLAPHierarchy hiera) {
		super(hiera.getName());
		hierarchy = hiera;
	}
	
	public OLAPHierarchy getOLAPHierarchy() {
		return hierarchy;
	}
}
