package bpm.connection.manager.connection.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import bpm.connection.manager.connection.VanillaConnection;

/**
 * A jdbc Connection wrapper
 * @author Marc
 *
 */
public class VanillaJdbcConnection implements VanillaConnection {

	private Connection jdbcConnection;
	
	private String url;
	private String user;
	private String password;
	private String driverClass;
	
	public VanillaJdbcConnection(String jdbcUrl, String user, String password, String driverClass, Connection jdbcConnection) {
		this.url = jdbcUrl;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass;
		this.jdbcConnection = jdbcConnection;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * You better have a really good reason to use this
	 * @return the jdbc connection
	 */
	public Connection getJdbcConnection() {
		return jdbcConnection;
	}
	
	/**
	 * Just to prepare the query
	 * @param query
	 * @return 
	 * @throws Exception
	 */
	public VanillaPreparedStatement prepareQuery(String query) throws Exception {
		return new VanillaPreparedStatement(this, query);
	}

	public DatabaseMetaData getMetaData() throws Exception {
		return jdbcConnection.getMetaData();
	}

	public String getCatalog() throws Exception {
		return jdbcConnection.getCatalog();
	}

	public VanillaPreparedStatement createStatement() throws Exception {
		return new VanillaPreparedStatement(this, "");
	}

	public VanillaPreparedStatement createStatement(int typeScrollInsensitive, int concurReadOnly) throws Exception {
		return new VanillaPreparedStatement(this, "", typeScrollInsensitive, concurReadOnly);
	}

	public boolean isClosed() throws Exception {
		return jdbcConnection.isClosed();
	}
	
	public boolean testConnection() throws Exception {
		try {
			boolean res = jdbcConnection.isValid(10);
			return res;
		} catch (Exception e) {
			//handle the shitty sgbds which though it was a good idea to not implement the test connection method
			//but I don't know how to do it so...
			throw e;
		}
	}

	public VanillaPreparedStatement createStatement(String parsedQuery, int typeScrollInsensitive, int concurReadOnly) throws Exception {
		return new VanillaPreparedStatement(this, parsedQuery, typeScrollInsensitive, concurReadOnly);
	}
	
}
