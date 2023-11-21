package metadata.client.trees;

import bpm.metadata.layer.logical.Join;

public class TreeJoin extends TreeObject {

	private Join join;
	
	public TreeJoin(Join join) {
		super(join.toString());
		this.join = join;
	}
	
	
	@Override
	public String toString() {
		return join.toString();
	}
	
	public Join getJoin(){
		return join;
	}
	@Override
	public Object getContainedModelObject() {
		return join;
	}
}
