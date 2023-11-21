package bpm.metadata.birt.oda.ui.trees;

import bpm.metadata.resource.IFilter;

public class TreeFilter extends TreeObject {
	private IFilter filter;
	
	public TreeFilter(IFilter filter) {
		super(filter.getOutputName());
		this.filter = filter;
	}

	@Override
	public String toString() {
		return filter.getOutputName();
	}
	
	public IFilter getFilter(){
		return filter;
	}
}
