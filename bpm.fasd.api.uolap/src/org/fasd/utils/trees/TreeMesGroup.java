package org.fasd.utils.trees;

import org.fasd.olap.OLAPMeasureGroup;

public class TreeMesGroup extends TreeParent{
	private OLAPMeasureGroup mg;
	public TreeMesGroup(OLAPMeasureGroup mg){
		super(mg.getName());
		this.mg = mg;
	}
	public OLAPMeasureGroup getOLAPMeasureGroup(){
		return mg;
	}
	
}
