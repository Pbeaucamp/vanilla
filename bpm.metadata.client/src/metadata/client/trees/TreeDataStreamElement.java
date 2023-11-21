package metadata.client.trees;

import bpm.metadata.layer.logical.IDataStreamElement;

public class TreeDataStreamElement extends TreeParent {

	private IDataStreamElement dataStreamElement;
	
	public TreeDataStreamElement(IDataStreamElement dataStreamElement) {
		super(dataStreamElement.getName());
		this.dataStreamElement = dataStreamElement;
	}

	@Override
	public String toString() {
		return dataStreamElement.getName();
	}
	
	public IDataStreamElement getDataStreamElement(){
		return dataStreamElement;
	}

	@Override
	public Object getContainedModelObject() {
		return dataStreamElement;
	}
}
