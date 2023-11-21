package org.fasd.utils.trees;

import org.fasd.olap.OLAPDimensionGroup;

public class TreeDimGroup extends TreeParent{
	private OLAPDimensionGroup mg;
	public TreeDimGroup(OLAPDimensionGroup mg){
		super(mg.getName());
		this.mg = mg;
	}
	public OLAPDimensionGroup getOLAPDimensionGroup(){
		return mg;
	}
	
}
