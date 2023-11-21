package org.fasd.utils.trees;

import org.fasd.olap.OLAPRelation;

public class TreeRelation extends TreeObject{
	OLAPRelation rel;
	public TreeRelation(OLAPRelation r){
		super(r.getName());
		this.rel = r;
	}
	public OLAPRelation getOLAPRelation(){
		return rel;
	}
}
