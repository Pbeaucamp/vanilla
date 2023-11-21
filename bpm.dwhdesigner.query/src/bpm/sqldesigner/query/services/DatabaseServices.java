package bpm.sqldesigner.query.services;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;

public class DatabaseServices extends AbstractDatabaseService{

	

	public DatabaseServices(DataBaseConnection dbc) {
		super(dbc);
	}

	public DatabaseServices(String host, String port, String login, String password, String dataBaseName, String driverName, String driversPath, String driversXMLFile) {

		super(host, port, login, password, dataBaseName, driverName, driversPath, driversXMLFile);
	}

	public DataBaseConnection connect(DocumentGateway document) throws ServerException {
		if (!dbc.isOpened()) {
			dbc.connect(document);
		}
		return dbc;
	}

	public List<Table> extractTables() {
		List<Table> tables = new ArrayList<Table>();
		if (isConnected()) {
			ResultSet resultSet = null;
			try {
				DatabaseMetaData md = dbc.getSocket(null).getMetaData();
				resultSet = md.getTables(null, null, "%", new String[]{"TABLE", "VIEW", "ALIAS"});
				
				while (resultSet.next()) {
					Table table = new Table();
					String shName = resultSet.getString("TABLE_SCHEM");
					if (shName!= null && !"".equals(shName)){
						table.setName(shName + "." + resultSet.getString(3));
					}
					else{
						table.setName(resultSet.getString(3));
					}
					
					tables.add(table);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try{
					resultSet.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} else
			tables = null;

		return tables;
	}

	public List<Column> extractColumns(String tableName) {
		List<Column> columns = new ArrayList<Column>();
		String schName = null;
		String tName = null;
		
		if (tableName.contains(".")){
			String[] s = tableName.split("\\.");
			schName = s[0];
			tName = s[1];
		}
		else{
			tName = tableName;
		}
		
		if (isConnected()) {
			ResultSet resultSet = null;
			
			try {
				DatabaseMetaData md = dbc.getSocket(null).getMetaData();		
				
			
				resultSet = md.getColumns(null, schName, tName, "%");
				
				while (resultSet.next()) {
					Column column = new Column();

					String name = resultSet.getString(4);
					

					column.setName(name);
					column.setType(resultSet.getString(6));

					columns.add(column);
				}
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				
				try{
					resultSet.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} else{
			columns = null;

		}
			
		return columns;
	}

	
}
