package bpm.gateway.core.server.database;

public class DBColumn {
	
	public static String typeId = "type_id";

	private String name;
	private String type; 
	private DBTable table; 
	private boolean primaryKey = false;
	public DBColumn(){}
	public DBColumn(DBTable table, String name, String type) {
		this.table = table;
		this.name = name;
		this.type = type;
		
		if (table != null)
			table.addColumn(this);
	}
	
	public DBColumn(DBTable table, String name, String type, boolean primaryKey) {
		this.table = table;
		this.name = name;
		this.type = type;
		this.primaryKey = primaryKey;
		if (table != null)
			table.addColumn(this);
	}
	
	
	
	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public String getName(){
		return name;
	}
	
	public void setTable(DBTable table) {
		this.table = table;
	}
	
	public String getFullName(){
		if (table != null){
			return table.getName() + "." + getName();
		}
		else{
			return  getName();
		}
		
	}
	
	public void setPrimaryKey(String s){
		try{
			primaryKey = Boolean.parseBoolean(s);
		}catch(Exception ex){
			
		}
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType(){
		return type;
	}

	public DBTable getDBTable() {
		return table;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
