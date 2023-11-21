package metadata.client.trees;

import bpm.metadata.layer.logical.Relation;

public class TreeRelation extends TreeParent {

	private Relation relation;
	
	public TreeRelation(Relation relation) {
		super(relation.getName());
		this.relation = relation;
	}

	@Override
	public String toString() {
		return relation.getName();
	}
	
	public Relation getRelation(){
		return relation;
	}
	@Override
	public Object getContainedModelObject() {
		return relation;
	}
	
}
