package bpm.metadata.birt.oda.ui.trees;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;

public class TreeDataStream extends TreeParent {

	private IDataStream dataStream;
	
	public TreeDataStream(IDataStream dataStream) {
		super(dataStream.getName());
		this.dataStream = dataStream;
		buildChild();
	}

	@Override
	public String toString() {
		return dataStream.getName();
	}
	
	public IDataStream getDataStream(){
		return dataStream;
	}

	private void buildChild(){
		for(IDataStreamElement e : dataStream.getElements()){
			addChild(new TreeDataStreamElement(e));
	
		}
	}
	
}
