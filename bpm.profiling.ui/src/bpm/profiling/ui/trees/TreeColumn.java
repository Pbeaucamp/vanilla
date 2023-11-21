package bpm.profiling.ui.trees;

import bpm.profiling.runtime.core.Column;


public class TreeColumn extends TreeParent {

	private Column column;
	
	public TreeColumn(Column column) {
		super(column.getName());
		this.column = column;
	}

	@Override
	public String toString() {
		return column.getLabel();
	}
	
	public Column getColumn(){
		return column;
	}

	
}
