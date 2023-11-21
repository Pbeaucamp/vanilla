package bpm.connection.manager.connection.jdbc;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import bpm.connection.manager.connection.ConnectionManager;

/**
 * Wrap a jdbc preparedStatement
 * @author Marc
 *
 */
public class VanillaPreparedStatement {

	private PreparedStatement preparedStatement;
	//Because why put it in the jdbc spec ? Get back the query from a statement is totally useless right ?
	private String actualQuery;
	
	private int scroll = -1;
	private int concur = -1;
	
	private VanillaJdbcConnection connection;
	
	public VanillaPreparedStatement(VanillaJdbcConnection connection, String query) throws Exception {
		actualQuery = query;
		if(query == null || query.isEmpty()) {
			if(connection.getDriverClass().equals("org.apache.derby.jdbc.EmbeddedDriver")) {
				query = "values 1";
			}
			else if(connection.getDriverClass().equals("com.ibm.as400.access.AS400JDBCDriver")) {
				query = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
			}
			else {
				query = "select 1";
			}
		}
		preparedStatement = connection.getJdbcConnection().prepareStatement(query);
		this.connection = connection;
	}
	
	public VanillaPreparedStatement(VanillaJdbcConnection vanillaJdbcConnection, String query, int resultSetType, int resultSetConcurrency) throws Exception {
		actualQuery = query;
		if(query == null || query.isEmpty()) {
			if(vanillaJdbcConnection.getDriverClass().equals("org.apache.derby.jdbc.EmbeddedDriver")) {
				query = "values 1";
			}
			else if(vanillaJdbcConnection.getDriverClass().equals("com.ibm.as400.access.AS400JDBCDriver")) {
				query = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
			}
			else {
				query = "select 1";
			}
		}
		concur = resultSetConcurrency;
		scroll = resultSetType;
		preparedStatement = vanillaJdbcConnection.getJdbcConnection().prepareStatement(query, resultSetType, resultSetConcurrency);
		this.connection = vanillaJdbcConnection;
	}

	/**
	 * Get the actual query.
	 * The parameters won't be replace on the query and since JDBC don't provide a way to get the sql...
	 * @return the actualQuery String (can be null).
	 */
	public String getActualQuery() {
		return actualQuery;
	}
	
	/**
	 * Use this to execute the query.
	 * If you just want the query metadata use getQueryMetadata(String query) or getParameterMetadata(String query) instead.
	 * @param query the sql query or "" if you've already called metadatas methods
	 * @return
	 */
	public ResultSet executeQuery(String query) throws Exception {
		try {
			if(isQueryChanged(query)) {
				prepareStatement(query);
			}
			
			Date start = new Date();
			
			ResultSet rs = preparedStatement.executeQuery();
			
			Date end = new Date();
			
//			Logger.getLogger(VanillaPreparedStatement.class.getName()).debug("Query : \n" + query + "\nexecuted in " + (end.getTime() - start.getTime()) + "ms");
			
			return rs;
		} catch(Exception e) {
			
//			Logger.getLogger(VanillaPreparedStatement.class.getName()).debug("Query : \n" + actualQuery);
			
			preparedStatement.close();
			ConnectionManager.getInstance().returnJdbcConnection(connection);
			connection = ConnectionManager.getInstance().getJdbcConnection(connection.getUrl(), connection.getUser(), connection.getPassword(), connection.getDriverClass());
			Statement stmt = null;
			if(concur != -1) {
				stmt = connection.getJdbcConnection().createStatement(scroll, concur);
			}
			else {
				stmt = connection.getJdbcConnection().createStatement();
			}
			
			ResultSet rs = stmt.executeQuery(actualQuery);
			return rs;
//			e.printStackTrace();
//			throw e;
		}
	}
	
	/**
	 * Execute the actual query
	 * @return
	 * @throws Exception
	 */
	public ResultSet executeQuery() throws Exception {
		return executeQuery("");
	}
	
	private void prepareStatement(String query) throws Exception {
		actualQuery = query;
		
		if(concur != -1) {
			preparedStatement = connection.getJdbcConnection().prepareStatement(query, scroll, concur);
		}
		else {
			preparedStatement = connection.getJdbcConnection().prepareStatement(query);
		}
		
		
	}
	
	private boolean isQueryChanged(String query) {
		if(preparedStatement != null) {
			//If the actualQuery isn't null, we need to check if it changed
			if(actualQuery != null && !actualQuery.isEmpty()) {
				//If the parameter query is empty, we'll use the actualquery
				if(query.isEmpty()) {
					return false;
				}
				else {
					if(actualQuery.equals(query)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Return the query metadata
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public ResultSetMetaData getQueryMetadata(String query) throws Exception {
		if(isQueryChanged(query)) {
			prepareStatement(query);
		}
		
		Date start = new Date();
		
		ResultSetMetaData rs = preparedStatement.getMetaData();
		
		Date end = new Date();
		
		Logger.getLogger(VanillaPreparedStatement.class.getClass()).debug("Metadata for query : " + query + " fetched in " + (end.getTime() - start.getTime()) + "ms");
		
		return rs;
	}
	
	/**
	 * Return the parameter metadata
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public ParameterMetaData getParameterMetadata(String query) throws Exception {
		if(isQueryChanged(query)) {
			prepareStatement(query);
		}
		
		Date start = new Date();
		
		ParameterMetaData rs = preparedStatement.getParameterMetaData();
		
		Date end = new Date();
		
		Logger.getLogger(VanillaPreparedStatement.class.getClass()).debug("ParameterMetadata for query : " + query + " fetched in " + (end.getTime() - start.getTime()) + "ms");
		
		return rs;
	}
	
	public void setParameter(int index, Object value) throws Exception {
		preparedStatement.setObject(index, value);
	}
	
	public void setParameter(int index, Integer value) throws Exception {
		preparedStatement.setInt(index, value);
	}
	
	public void setParameter(int index, String value) throws Exception {
		preparedStatement.setString(index, value);
	}

	public void close() throws Exception {
		preparedStatement.close();
		preparedStatement = null;
		actualQuery = null;
	}

	public void setFetchSize(int size) throws Exception {
		preparedStatement.setFetchSize(size);
	}

	public void setMaxRows(int maxRows) throws Exception {
		preparedStatement.setMaxRows(maxRows);
	}
	
	public int executeUpdate() throws Exception {
		try {
			return preparedStatement.executeUpdate();
		} catch(Exception e) {
			preparedStatement.close();
			ConnectionManager.getInstance().returnJdbcConnection(connection);
			connection = ConnectionManager.getInstance().getJdbcConnection(connection.getUrl(), connection.getUser(), connection.getPassword(), connection.getDriverClass());
			Statement stmt = null;
			stmt = connection.getJdbcConnection().createStatement();
			
			return stmt.executeUpdate(actualQuery);
		}
	}
}
