package bpm.metadata.layer.logical.sql;


import java.util.List;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.metadata.resource.IFilter;

public class SQLDataStream extends IDataStream {
	
	public static final int SQL_VIEW = 0;
	public static final int SQL_TABLE = 1;
	public static final int SQL_ALIAS = 2;
	public static final int SQL_QUERY = 3;
	
	
	private String sql;
	public void setSql(String sql){
		while(sql.trim().endsWith(";")){
			sql = sql.trim().substring(0, sql.trim().length() - 1);
		}
		this.sql = sql;
	}
	
	public String getSql(){
		return sql;
	}
	
	/**
	 * do not use
	 */
	public SQLDataStream(){}
	
	protected SQLDataStream(SQLTable origine) {
		super(origine);
		
		for(IColumn c : origine.getColumns()){
			SQLDataStreamElement col = new SQLDataStreamElement((SQLColumn)c);
			addColumn(col);
		}

	}
	
	/**
	 * return the type of the origin table 
	 * @return SQL_ALIAS, SQL_TABLE or SQL_VIEW
	 */
	public int getSQLType(){
		SQLTable t = (SQLTable)origin;
		
		if (t == null){
			return SQL_TABLE;
		}
		if (t.isAlias()){
			return SQL_ALIAS;
		}
		else if (t.isTable()){
			return SQL_TABLE;
		}
		else if (t.isQuery()){
			return SQL_QUERY;
		}
		else{
			return SQL_VIEW;
		}
	}

	@Override
	public boolean isMeasure() {
		return false;
	}
	
	public void addFilter(List groupNames, IFilter filter){
		for(Object o : groupNames){
			addFilter((String)o, filter);
		}
		addFilter(filter);
	}
}
