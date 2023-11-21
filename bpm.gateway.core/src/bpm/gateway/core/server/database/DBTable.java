package bpm.gateway.core.server.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class DBTable {

	private String name;
	private String type;
	private DBSchema schema; 
	
	private List<DBColumn> columnsData = new ArrayList<DBColumn>();
	private List<DBColumn> columnsId = new ArrayList<DBColumn>();
	
	private LinkedHashMap<String, DBColumn> hashedColumns = new LinkedHashMap<String, DBColumn>();
	
	private Object privateObject;
	public DBTable(){}
	public DBTable(DBSchema schema, String name, String type) {
		this.schema = schema;
		this.name = name;
		this.type = type;
		
		if (schema != null){
			schema.addDBTable(this);
		}	
	}
	
	/**
	 * Data storer for bpm.tango
	 * @param privateObject
	 */
	public void setPrivateObject(Object privateObject) {
		this.privateObject = privateObject;
	}
	
	/**
	 * Data storer for bpm.tango
	 * @return
	 */
	public Object getPrivateObject() {
		return privateObject;
	}
	
	public String getName(){
		return name;
	}
	public String getFullName() {
		if (schema != null && !schema.getName().equals("%") && !schema.getName().equals("")){
			return schema.getName() + "." + getName();
		}
//		else if (schema != null && schema.getName().equals("")) {
//			return  getName();
//		}
		else {
			return  getName();
		}
		
	}
	public String getType(){
		return type;
	}

	public DBSchema getDBSchema() {
		return schema;
	}
	
	public void addColumn(DBColumn dbColumn) {
		columnsData.add(dbColumn);
		dbColumn.setTable(this);
		hashedColumns.put(dbColumn.getName(), dbColumn);
	}
	
	public void addIdColumn(DBColumn dbColumn) {
		dbColumn.setType(DBColumn.typeId);
		columnsId.add(dbColumn);
		hashedColumns.put(dbColumn.getName(), dbColumn);
	}
	
	public void removeColumnData(DBColumn col) {
		columnsData.remove(col);
		hashedColumns.remove(col.getName());
	}
	
	public void removeColumnAllCases(DBColumn col) {
		col.setType("");
		columnsId.remove(col);
		hashedColumns.remove(col.getName());
		//columnsData.remove(col);
		
		DBColumn targetDelete = null;
		
		for (DBColumn dataCol : columnsData) {
			if (dataCol.getName().equals(col.getName()))
				targetDelete = dataCol;
			//columnsData.remove(dataCol);
		}
		if (targetDelete != null)
			columnsData.remove(targetDelete);
	}
	
	public void removeColumnId(DBColumn col) {
		col.setType("");
		columnsId.remove(col);
		columnsData.add(col);
	}
	
	public List<DBColumn> getAllColumns() {
		List<DBColumn> allCols = new ArrayList<DBColumn>();
		
		allCols.addAll(columnsId);
		
		allCols.addAll(columnsData);
		
 		return allCols;
	}
	
	public List<DBColumn> getIdColumns() {
		return columnsId;
	}
	
	public List<DBColumn> getDataColumns() {
		return columnsData;
	}
	public void setSchema(DBSchema dbSchema) {
		this.schema = dbSchema;
		
	}
	
	public void setName(String name) {
		this.name = name;
		
	}
	
//	public LinkedHashMap<String, DBColumn> getHashedColumns() {
//		return hashedColumns;
//	}
}
