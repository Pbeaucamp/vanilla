package metadata.client.trees;

import bpm.metadata.layer.physical.olap.UnitedOlapConnection;

public class TreeCubeOlap extends TreeParent {

	private UnitedOlapConnection olap;
	
	public TreeCubeOlap(UnitedOlapConnection olap) {
		super(olap.getCubeName());
		this.olap = olap;
	}

	@Override
	public String toString() {
		return olap.getCubeName();
	}
	
	public UnitedOlapConnection getColumn(){
		return olap;
	}

	@Override
	public Object getContainedModelObject() {
		return olap;
	}
}
