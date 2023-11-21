package metadata.client.trees;

import bpm.metadata.resource.ComplexFilter;

public class TreeComplexFilter extends TreeResource {

	private ComplexFilter filter;
	
	public TreeComplexFilter(ComplexFilter filter) {
		super(filter);
		this.filter = filter;
	}

	@Override
	public String toString() {
		return filter.getName();
	}
	
	public ComplexFilter getFilter(){
		return filter;
	}
	@Override
	public Object getContainedModelObject() {
		return filter;
	}
}
