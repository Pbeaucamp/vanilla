package metadata.client.trees;

import bpm.metadata.resource.Filter;

public class TreeFilter extends TreeResource {

	private Filter filter;

	public TreeFilter(Filter filter) {
		super(filter);
		this.filter = filter;
	}

	@Override
	public String toString() {
		return filter.getName();
	}

	public Filter getFilter() {
		return filter;
	}

	@Override
	public Object getContainedModelObject() {
		return filter;
	}
}
