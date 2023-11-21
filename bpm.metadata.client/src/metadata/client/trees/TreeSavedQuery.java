package metadata.client.trees;

import bpm.metadata.query.SavedQuery;

public class TreeSavedQuery extends TreeObject {

	private SavedQuery savedQuery;

	public TreeSavedQuery(SavedQuery savedQuery) {
		super(savedQuery.getName());
		this.savedQuery = savedQuery;
	}

	@Override
	public String toString() {
		return savedQuery.getName();
	}

	public SavedQuery getQuery() {
		return savedQuery;
	}

	@Override
	public Object getContainedModelObject() {
		return savedQuery;
	}
}
