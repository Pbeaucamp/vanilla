package bpm.profiling.runtime.core;

public class Column {
	private String name;
	private Table table;
	private String label;
	private String type;
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Table getTable() {
		return table;
	}
	protected void setTable(Table table) {
		this.table = table;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String realName) {
		this.label = realName;
	}
	
	
	
}
