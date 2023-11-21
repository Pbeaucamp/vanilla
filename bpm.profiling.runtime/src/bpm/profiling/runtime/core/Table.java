package bpm.profiling.runtime.core;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private String name;
	private String label;
	
	private Connection connection;
	private List<Column> columns = new ArrayList<Column>();
	
	public void addColumn(Column col){
		columns.add(col);
		col.setTable(this);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	
}
