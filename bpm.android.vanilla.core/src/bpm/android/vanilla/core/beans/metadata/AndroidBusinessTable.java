package bpm.android.vanilla.core.beans.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;

@SuppressWarnings("serial")
public class AndroidBusinessTable implements Serializable {

	private String name;
	private AndroidBusinessPackage parent;
	private List<Column> columns = new ArrayList<Column>();

	public AndroidBusinessTable() {
	}

	public AndroidBusinessTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addColumn(Column column) {
		this.columns.add(column);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public AndroidBusinessPackage getParent() {
		return parent;
	}

	public void setParent(AndroidBusinessPackage parent) {
		this.parent = parent;
	}
}
