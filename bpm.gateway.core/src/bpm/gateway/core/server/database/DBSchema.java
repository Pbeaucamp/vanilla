package bpm.gateway.core.server.database;

import java.util.ArrayList;
import java.util.List;

public class DBSchema {
	private String schemaName;
	private List<DBTable> tables = new ArrayList<DBTable>();
	private boolean tableLoaded = false;
	
	private boolean noSchema = false; //a db with no schema, like mysql
	public DBSchema(){}
	public DBSchema(String name){
		this.schemaName = name;
	}
	
	public DBSchema(String name, boolean noSchema){
		this.schemaName = name;
		this.noSchema = noSchema;
	}
	
	
	public boolean isNoSchema(){
		return this.noSchema;
	}
	public String getName(){
		return schemaName;
	}
	
	public void addDBTable(DBTable table){
		tables.add(table);
		table.setSchema(this);
	}
	
	public List<DBTable> getTables(){
		return new ArrayList<DBTable>(tables);
	}
	
	public boolean isTableLoaded(){
		return tableLoaded;
	}
	
	public void setTableLoaded(){
		this.tableLoaded = true;
	}
	/**
	 * @param schemaName the schemaName to set
	 */
	public void setName(String schemaName) {
		this.schemaName = schemaName;
		
	}
	
	public void setNoSchema(String s){
		try{
			noSchema = Boolean.parseBoolean(s);
		}catch(Exception ex){
			
		}
	}
}
