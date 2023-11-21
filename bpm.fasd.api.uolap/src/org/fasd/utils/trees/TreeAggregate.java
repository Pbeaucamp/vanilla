package org.fasd.utils.trees;

import org.fasd.olap.aggregate.AggregateTable;

public class TreeAggregate extends TreeParent {
	private AggregateTable table;
	
	public TreeAggregate(AggregateTable table) {
		super(table.getTable().getName());
		this.table = table;
	}
	
	public AggregateTable getAgg() {
		return table;
	}
}
