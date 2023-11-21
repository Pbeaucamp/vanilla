package bpm.sqldesigner.query.model.selected;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.sqldesigner.query.model.Column;

public class SelectedColumns {

	private List<Column> columns = new ArrayList<Column>();

	public void addColumn(Column c) {
		if (columns.contains(c)){
			return;
		}
		columns.add(c);
	}
	
	public void addAllColumns(Collection<Column> collection) {
		columns.addAll(collection);
	}

	public void removeColumn(Column c) {
		columns.remove(c);
	}
	
	public boolean contains(Column c){
		return columns.contains(c);
	}

	public List<Column> getColumns() {
		return columns;
	}
}
