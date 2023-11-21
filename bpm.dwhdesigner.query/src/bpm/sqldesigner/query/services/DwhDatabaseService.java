package bpm.sqldesigner.query.services;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.server.database.DBColumn;
import bpm.gateway.core.server.database.DBSchema;
import bpm.gateway.core.server.database.DBTable;
import bpm.gateway.core.server.database.dwhview.DwhDbConnection;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;

public class DwhDatabaseService extends AbstractDatabaseService{
	
	public DwhDatabaseService(DwhDbConnection dbc) {
		super(dbc);
	}

	
	private DwhDbConnection getDwhConnection(){
		return (DwhDbConnection)dbc;
	}

	@Override
	public List<Column> extractColumns(String tableName) {
		List<Column> cols = new ArrayList<Column>();
		DBSchema sch = getDwhConnection().getSchema();
		
		for(DBTable t : sch.getTables()){
			if (tableName.equals(t.getName())){
				
				for(DBColumn col : t.getDataColumns()){
					
					Column c = new Column();
					c.setName(col.getName());
					c.setType(col.getType());
					c.setKey(col.isPrimaryKey());
					cols.add(c);
				}
				
			}
		}
		
		
		return cols;
	}

	@Override
	public List<Table> extractTables() {
		List<Table> tables = new ArrayList<Table>();
		
		DBSchema sch = getDwhConnection().getSchema();
			
		for(DBTable t : sch.getTables()){
			Table tb = new Table();
			if (sch.isNoSchema()){
				
				tb.setName(t.getName());
			}
			else{
				tb.setName(sch.getName() + "." + t.getName());
			}
			tables.add(tb);
		}
			
		
		
		return tables;
	}

	

}
