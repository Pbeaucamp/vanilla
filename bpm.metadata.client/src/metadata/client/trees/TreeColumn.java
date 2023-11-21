package metadata.client.trees;

import bpm.metadata.layer.physical.IColumn;

public class TreeColumn extends TreeParent {

	private IColumn column;
	
	public TreeColumn(IColumn column) {
		super(column.getName());
		this.column = column;
	}

	@Override
	public String toString() {
		return column.getName();
	}
	
	public IColumn getColumn(){
		return column;
	}

	@Override
	public Object getContainedModelObject() {
		return column;
	}
}
