package org.fasd.sql;

import java.util.ArrayList;

import org.fasd.datasource.DataObjectItem;


/**
 * SQLTable :
 * Internal representation of a sql Table.
 * 
 * @author manu
 *
 */
public class SQLTable {
	private String name;
	private ArrayList<DataObjectItem> columns;
	
	public SQLTable(String name) {
		this.name = name;
	}
	
	public void setColumns(ArrayList<DataObjectItem> cols) {
		this.columns = cols;
	}
	
	public void addColumn(DataObjectItem col) {
		columns.add(col);
	}
	
	public ArrayList<DataObjectItem> getColumns() {
		return columns;
	}
	
	public String getTableName() {
		return name;
	}
}
