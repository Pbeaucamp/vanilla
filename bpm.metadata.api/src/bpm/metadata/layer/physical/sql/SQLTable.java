package bpm.metadata.layer.physical.sql;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.query.NonCompliantDriverQueryModifier;

public class SQLTable implements ITable {
	public static final int VIEW = 0;
	public static final int TABLE = 1;
	public static final int ALIAS = 2;
	public static final int QUERY = 3;
	private static final String[] types = new String[]{"VIEW", "TABLE", "ALIAS", "QUERY"};
	
	private String name;
	private String schemaName;
	
	private int type;
	private SQLConnection connection;
	
	private List<SQLColumn> columns =  new ArrayList<SQLColumn>();
	
	
	private boolean hasAddedColumns = false;
	
	
	
	
	/**
	 * don't use it exceptt if you havent the SQLConnection information to build the database automatically 
	 * with the Factory
	 * @param type
	 * @param name
	 */
	public SQLTable(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	
	public SQLTable(SQLConnection c, int type){
		this.type = type;
		this.connection = c;
	}
	


	/**
	 * @return the schemaName
	 */
	public final String getSchemaName() {
		return schemaName;
	}


	/**
	 * @param schemaName the schemaName to set
	 */
	public final void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}


	public List<IColumn> getColumns() {
		if (!hasAddedColumns){
			getColumnsFromConnection();
		}
		
		return (List)columns;
	}


	public String getName() {
		
		String newName = name;
		
		if (schemaName != null  && !schemaName.trim().equals("")){
			newName = schemaName + "." + name;
		}
		
		return NonCompliantDriverQueryModifier.changeTableName(newName, connection.getJdbcPrefix());
	}
	
public String getNameWithoutShema() {
		
		String newName = name;
		
		return NonCompliantDriverQueryModifier.changeTableName(newName, connection.getJdbcPrefix());
	}
	
	
	
	public void setName(String name){
		this.name = name;
	}

	/**
	 * add a column to the colList
	 * @param column
	 */
	public void addColumn(SQLColumn column){
		
		for(SQLColumn c : columns){
			if (c.getName().equals(column.getName())){
				c.setClassName(column.getClassName());
				c.setSqlType(column.getSqlType());
				c.setSqlTypeCode(column.getSqlTypeCode());
				
				return;
			}
		}
		
		columns.add(column);
	}
	
	public boolean isView(){
		return type == VIEW;
	}
	public boolean isTable(){
		return type == TABLE;
	}
	public boolean isAlias(){
		return type == ALIAS;
	}
	public boolean isQuery(){
		return type == QUERY;
	}


	public IConnection getConnection() {
		return connection;
	}



	private void getColumnsFromConnection(){
		try{
			
			connection.getColumns(this);
			hasAddedColumns = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public IColumn getElementNamed(String originName) {
		if (!hasAddedColumns){
			getColumnsFromConnection();
		}
		
		
		/**
		 * ere, added synchro on columns to avoid :
java.util.ConcurrentModificationException
	at java.util.AbstractList$Itr.checkForComodification(AbstractList.java:372)
	at java.util.AbstractList$Itr.next(AbstractList.java:343)
	at bpm.metadata.layer.physical.sql.SQLTable.getElementNamed(SQLTable.java:135)
	at bpm.metadata.MetaDataBuilder.build(MetaDataBuilder.java:155)
	at bpm.metadata.birt.oda.runtime.impl.Query.executeQuery(Query.java:146)
	at org.eclipse.datatools.connectivity.oda.consumer.helper.OdaQuery.doExecuteQuery(OdaQuery.java:464)
	at org.eclipse.datatools.connectivity.oda.consumer.helper.OdaQuery.executeQuery(OdaQuery.java:428)
	at org.eclipse.birt.data.engine.odaconsumer.PreparedStatement.execute(PreparedStatement.java:575)
	at org.eclipse.birt.data.engine.executor.DataSourceQuery.execute(DataSourceQuery.java:765)
		 */
		synchronized (columns) {
			for(IColumn c : columns){
				if (schemaName != null && !schemaName.trim().equals("")){
					if (!originName.trim().startsWith(schemaName + ".")){
						if (c.getName().trim().equals(schemaName + "." + originName.trim())){
							return c;
						}
					}
					
					
					
				}
				if (c.getName().equals(originName)){
					return c;
				}
				if (("public."+c.getName()).equals(originName)){
					return c;
				}
				if ((c.getName()).equals("public."+originName)){
					return c;
				}
				
			}
		}
		return null;
	}
	
	/**
	 * return the SQL type(TABLE, VIEW, ALIAS)
	 * @return
	 */
	public String getSqlType(){
		return types[type];
	}


	public String getShortName() {
		return name;
	}


	public boolean hasColumns() {
		return hasAddedColumns;
	}
}
