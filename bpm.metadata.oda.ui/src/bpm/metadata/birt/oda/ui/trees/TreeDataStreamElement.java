package bpm.metadata.birt.oda.ui.trees;

import java.util.Locale;

import bpm.metadata.layer.logical.IDataStreamElement;

public class TreeDataStreamElement extends TreeParent {

	private IDataStreamElement dataStreamElement;
	
	public TreeDataStreamElement(IDataStreamElement dataStreamElement) {
		super(dataStreamElement.getName());
		this.dataStreamElement = dataStreamElement;
	}

	@Override
	public String toString() {
		return dataStreamElement.getOuputName(Locale.getDefault());
	}
	
	public IDataStreamElement getDataStreamElement(){
		return dataStreamElement;
	}

	
}
