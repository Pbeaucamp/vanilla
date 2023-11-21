package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTable implements Serializable, IDatabaseObject {

	private static final long serialVersionUID = 1L;

	private String name;
	private String customName;

	private List<DatabaseColumn> columns;

	public DatabaseTable() { }
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCustomName() {
		return customName != null && !customName.isEmpty() ? customName : getName();
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public List<DatabaseColumn> getColumns() {
		if (columns == null) {
			columns = new ArrayList<DatabaseColumn>();
		}
		return columns;
	}

	public void setColumns(List<DatabaseColumn> columns) {
		this.columns = columns;
		if (columns != null) {
			for (DatabaseColumn column : columns) {
				column.setParent(this);
			}
		}
	}

	public void addColumn(DatabaseColumn column) {
		if (columns == null) {
			columns = new ArrayList<DatabaseColumn>();
		}
		column.setParent(this);
		this.columns.add(column);
	}

	@Override
	public String toString() {
		return getCustomName();
	}
}
