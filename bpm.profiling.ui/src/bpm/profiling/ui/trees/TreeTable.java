package bpm.profiling.ui.trees;

import bpm.profiling.runtime.core.Table;



public class TreeTable extends TreeParent {

	private Table table;
	
	public TreeTable(Table table) {
		super(table.getName());
		this.table = table;
	}

	@Override
	public String toString() {
		return table.getLabel();
	}
	
	public Table getTable(){
		return table;
	}

	
	
}
