package metadata.client.trees;

import bpm.metadata.resource.IResource;

public class TreeResource extends TreeObject {

	private IResource r;

	public TreeResource(IResource r) {
		super(r.getName());
		this.r = r;
	}

	@Override
	public String toString() {
		return r.getName();
	}

	public IResource getResource() {
		return r;
	}

	@Override
	public Object getContainedModelObject() {
		return r;
	}
}
