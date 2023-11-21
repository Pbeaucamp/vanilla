package bpm.sqldesigner.api.database;

import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

import bpm.studio.jdbc.management.model.ListDriver;




/**
 * This class is a standard Class to store JDBC connection information.
 * The driverName field is a key to match to the right JDBC driver java class name
 * 
 * 
 * @author LCA
 *
 */
public class DataBaseConnection {
	/*
	 * JDBC Connection informations
	 */
	private String host;
	private String port;
	private String dataBaseName;
	private String login;
	private String password;
	private String driverName;
	
	private Semaphore streamModeLocker = new Semaphore(1, true);
	
	private boolean useFullUrl = false;
	private String fullUrl;
	
	
	
	public boolean isUseFullUrl() {
		return useFullUrl;
	}
	public void setUseFullUrl(boolean useFullUrl) {
		this.useFullUrl = useFullUrl;
	}
	public void setUseFullUrl(String useFullUrl) {
		this.useFullUrl = Boolean.parseBoolean(useFullUrl);
	}
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	
	private Statement stmt;
	
	/*
	 * SQL Connection used to perform all operations
	 */
	protected Connection socket;
	
	
	
	
	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public final String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public final void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the dataBaseName
	 */
	public final String getDataBaseName() {
		return dataBaseName;
	}
	/**
	 * @param dataBaseName the dataBaseName to set
	 */
	public final void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	
	/**
	 * @return the login
	 */
	public final String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public final void setLogin(String login) {
		this.login = login;
	}
	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public final void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the driverName : key name of the driver in the driverFile
	 */
	public final String getDriverName() {
		return driverName;
	}
	/**
	 * @param driverName the driverName to set : key name of the driver in the driverFile
	 */
	public final void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	/**
	 * establish a connection to the database and store the JdbcConnection Object
	 * (the jdbc Connection stay opened until the method disconnect is called)
	 * @throws ServerException
	 */
	public void connect() throws Exception{
		if (stmt != null){
			try {
				stmt.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		if (socket != null){
			try {
				socket.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		if (!useFullUrl){
			socket = JdbcConnectionProvider.createSqlConnection(driverName, host, port, dataBaseName, login, password);
			try {
				socket.setCatalog(dataBaseName);
			} catch (SQLException e) {
				throw new ServerException("Unable to set JDBC catalog : " + e.getMessage(), e);
			}
		}
		else{
			socket = JdbcConnectionProvider.createSqlConnection(driverName, fullUrl, login, password);
		}
		
	}
	
	
	/**
	 * close the Jdbc Connection to the database 
	 * @throws ServerException
	 */
	public void disconnect() throws Exception{
		try {
			if (socket != null && !socket.isClosed()){
				socket.close();
			
			}
			socket = null;
		} catch (SQLException e) {
			socket = null;
			throw new Exception(e.getMessage() + ". Close SQLConnection problem", e);
		}
	}
	
	/**
	 * 
	 * @return true if all parameter have been set
	 */
	public boolean isSet() {
		if (driverName.equals(ListDriver.MS_ACCESS) || driverName.equals(ListDriver.MS_XLS)){
			return ! (driverName == null || driverName.equals(""));
		}
		else{
			return ! (host == null || host.trim().equals("") || login == null || password == null || driverName == null || driverName.equals(""));
		}
		
		
	}
	
	/**
	 * 
	 * @return true if the SQLConnection on the SQL Database is still opened
	 */
	public boolean isOpened() {
		try{
			return socket != null && !socket.isClosed();
		}catch(Exception e){
			
			if (stmt != null){
				try{
					stmt.close();
				}catch(Exception ex){
					
				}
				stmt = null;
			}
			
			try{
				
				socket.close();
				
			}catch(Exception ex){
				
			}
			socket = null;
			return false;
		}
		
	}
	
	/**
	 * return the socket to the database to perform some operations
	 * @return
	 */
	public Connection getSocket(){
		try {
			if (socket == null || socket.isClosed()){
				try {
					connect();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return socket;
	}
	
	synchronized public Statement getStatmentForStreamMode() throws Exception{

		
		if (stmt == null){
			try{
				stmt = socket.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				
				stmt.setFetchSize(Integer.MIN_VALUE);

			}catch(SQLException e){
				stmt = socket.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				
				try{
					stmt.setFetchSize(1);
				}catch(Exception ex){
					stmt.setFetchSize(0);
				}
				
			}
		}
		
		return stmt;
	}
	
	 public void closeStreamMode(){
		
			 try {
				if (stmt != null){
					 synchronized(stmt){
					 
						 stmt.close();
					}
					
				}
					
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			stmt = null;
			
			streamModeLocker.release();
			 
		 
		
	}
	

	
}
