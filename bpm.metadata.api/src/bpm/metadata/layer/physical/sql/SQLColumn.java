package bpm.metadata.layer.physical.sql;

import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;

public class SQLColumn implements IColumn {

	private String name;
	private String label;
	private String className;
	private String sqlType;
	private SQLTable table;
	private int sqlTypeCode;
	
	/**
	 * don't use it exceptt if you havent the SQLConnection information to build the database automatically 
	 * with the Factory
	 * @param type
	 * @param name
	 */
	public SQLColumn(String name, String className, String sqlType){
		this.name = name;
		this.className = className;
		this.sqlType = sqlType;
	}
	
	public SQLColumn(SQLTable table){
		this.table = table;
	}
	

	public String getSqlType() {
		return sqlType;
	}


	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}


	public String getClassName() {
		return className;
	}

	public Class<?> getJavaClass() throws Exception {
		if (className != null){
			return Class.forName(className);
		}
		
		throw new Exception("className argument not set");
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setClassName(String className){
		this.className = className;
	}


	public ITable getTable() {
		return table;
	}


	public List<String> getValues() {
		return ((SQLConnection)table.getConnection()).getColumnValues(this);
	}

	public void setSqlTypeCode(int sqlTypeCode) {
		this.sqlTypeCode = sqlTypeCode;
		
	}
	
	public int getSqlTypeCode(){
		return sqlTypeCode;
	}

	public String getShortName() {
		if (getName().contains(".")){
			return getName().substring(getName().lastIndexOf(".") + 1);
		}
		else{
			return getName();
		}
		
	}

	@Override
	public List<String> getValues(HashMap<String, String> parentValues) {
		return ((SQLConnection)table.getConnection()).getColumnValues(this, parentValues);
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		if(label == null || label.isEmpty()) {
			return name;
		}
		return label;
	}

}
