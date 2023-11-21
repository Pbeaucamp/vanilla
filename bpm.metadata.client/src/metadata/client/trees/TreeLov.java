package metadata.client.trees;

import bpm.metadata.resource.ListOfValue;

public class TreeLov extends TreeResource {

	private ListOfValue lov;
	
	public TreeLov(ListOfValue lov) {
		super(lov);
		this.lov = lov;
	}

	@Override
	public String toString() {
		return lov.getName();
	}
	
	public ListOfValue getLov(){
		return lov;
	}
	@Override
	public Object getContainedModelObject() {
		return lov;
	}

}
