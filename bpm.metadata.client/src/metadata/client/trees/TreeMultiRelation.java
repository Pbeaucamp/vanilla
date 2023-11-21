package metadata.client.trees;

import bpm.metadata.layer.logical.MultiDSRelation;

public class TreeMultiRelation extends TreeParent {

	private MultiDSRelation relation;

	public TreeMultiRelation(MultiDSRelation relation) {
		super(relation.getName());
		this.relation = relation;
	}

	@Override
	public String toString() {
		return relation.getName();
	}

	public MultiDSRelation getRelation() {
		return relation;
	}

	@Override
	public Object getContainedModelObject() {
		return relation;
	}

}
