package bpm.workflow.runtime.databases;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.workflow.runtime.resources.servers.DataBaseServer;

public class DataBaseHelper {
	
	public static List<StreamElement> getDescriptor(DataBaseServer server, String query) throws Exception {
		List<StreamElement> res = null;
		
		String jdbcDriver = server.getJdbcDriver();
		String url = server.getUrl();
		String port = server.getPort();
		String dataBaseName = server.getDataBaseName();
		String login = server.getLogin();
		String password = server.getPassword();
		
		VanillaJdbcConnection connection;

		connection = JdbcConnectionProvider.createSqlConnection(jdbcDriver, url, port, dataBaseName, login, password);
		
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		try{
			
			stmt = connection.createStatement();
			stmt.setMaxRows(1);			
			
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			
			res = new ArrayList<StreamElement>();
			
			for(int i = 1; i<= rsmd.getColumnCount(); i++){
				
				StreamElement e = new StreamElement();
				e.className = rsmd.getColumnClassName(i);
				e.name = rsmd.getColumnName(i);
				e.tableName = rsmd.getTableName(i);
				e.typeName = rsmd.getColumnTypeName(i);
				e.precision = rsmd.getPrecision(i);
				e.decimal = rsmd.getScale(i);

				
				res.add(e);
				
			}

		}finally{
			try{
				if (rs != null ){
					rs.close();
				}
				
				if (stmt != null){
					stmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			if (!connection.isClosed()){
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
			
		}

		return res;
	}
	
	public static boolean testConnection(DataBaseServer server, String query) {
		String jdbcDriver = server.getJdbcDriver();
		String url = server.getUrl();
		String port = server.getPort();
		String dataBaseName = server.getDataBaseName();
		String login = server.getLogin();
		String password = server.getPassword();
		
		VanillaJdbcConnection connection = null;
		boolean test = false;
		try {
			connection = JdbcConnectionProvider.createSqlConnection(jdbcDriver, url, port, dataBaseName, login, password);
			
			VanillaPreparedStatement stmt = null;
			ResultSet rs = null;
				
			stmt = connection.createStatement();
			stmt.setMaxRows(1);			

			rs = stmt.executeQuery(query);
			
			return rs != null;
		} catch (Exception e) {
			e.printStackTrace();
			test = false;
		}
		finally {
			if (connection != null)
				try {
					ConnectionManager.getInstance().returnJdbcConnection(connection);
				} catch (Exception e) {
					e.printStackTrace();
					test = false;
				}
		}
		return test;	
		
	}


}
