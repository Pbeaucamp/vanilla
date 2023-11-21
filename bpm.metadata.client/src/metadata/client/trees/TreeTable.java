package metadata.client.trees;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;

public class TreeTable extends TreeParent {

	private ITable table;

	public TreeTable(ITable table) {
		super(table.getName());
		this.table = table;
	}

	@Override
	public String toString() {
		return table.getName();
	}

	public ITable getTable() {
		return table;
	}

	public void buildChild() {
		for (IColumn e : table.getColumns()) {
			addChild(new TreeColumn(e));

		}
	}

	@Override
	public Object getContainedModelObject() {
		return table;
	}
}
