package bpm.vanilla.portal.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MapColumns implements IsSerializable {
	private String columnName;
	private String columnType;
	
	public MapColumns(String columnName, String columnType) {
		super();
		this.columnName = columnName;
		this.columnType = columnType;
	}
	
	public MapColumns() {
		
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
}
