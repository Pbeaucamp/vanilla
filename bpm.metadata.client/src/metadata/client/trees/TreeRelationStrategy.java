package metadata.client.trees;

import bpm.metadata.layer.business.RelationStrategy;

public class TreeRelationStrategy extends TreeObject {

	private RelationStrategy strategy;

	public TreeRelationStrategy(RelationStrategy strategy) {
		super(strategy.getName());

		this.strategy = strategy;
	}

	@Override
	public Object getContainedModelObject() {
		return strategy;
	}

	@Override
	public String toString() {
		return strategy.getName();
	}
	
	public RelationStrategy getStrategy() {
		return strategy;
	}

}
