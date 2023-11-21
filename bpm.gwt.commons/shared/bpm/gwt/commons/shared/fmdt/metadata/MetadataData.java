package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

public class MetadataData implements IsSerializable {

	private List<DatabaseColumn> columns;
	private List<Row> rows;
	
	public MetadataData() { }

	public MetadataData(List<DatabaseColumn> columns) {
		this.columns = columns;
	}
	
	public List<DatabaseColumn> getColumns() {
		return columns;
	}
	
	public void addRow(Row row) {
		if (rows == null) {
			rows = new ArrayList<Row>();
		}
		this.rows.add(row);
	}
	
	public List<Row> getRows() {
		return rows;
	}
}
