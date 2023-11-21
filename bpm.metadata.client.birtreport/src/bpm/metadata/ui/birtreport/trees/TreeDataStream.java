package bpm.metadata.ui.birtreport.trees;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IFilter;

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
		
		for(IFilter f : dataStream.getGenericFilters()){
			addChild(new TreeResource(f));
		}
	}
	
	public void refresh(){
		removeAll();
		buildChild();
	}
	
}
