package org.fasd.utils.trees;

import org.fasd.datasource.DataObjectItem;

public class TreeColumn extends TreeParent {
	DataObjectItem col;
	
	public TreeColumn(DataObjectItem col) {
		super(col.getName());
		this.col = col;
	}
	
	public DataObjectItem getColumn() {
		return col;
	}
}