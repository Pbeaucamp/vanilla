package org.fasd.utils.trees;

import org.fasd.olap.OLAPDimension;

public class TreeDim extends TreeParent {
	private OLAPDimension dimension;
			
	public TreeDim(OLAPDimension dim) {
		super(dim.getName());
		dimension = dim;
	}
	
	public OLAPDimension getOLAPDimension() {
		return dimension;
	}
}