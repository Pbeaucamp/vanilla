package bpm.workflow.runtime.databases;

import java.sql.SQLException;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;


/**
 * This class is an helper class to load dynamically JDBC drivers
 * and to provide a Jdbc Connection
 * 
 * 
 * If you try to use this class without running the init method before,
 * a JdbcException will be thrown
 * 
 * @author LCA
 *
 */
public class JdbcConnectionProvider {
	
	private static JdbcConnectionProvider instance = null;
	
	

	
//	private static boolean isInited = false;
	
	
	
	public static ListDriver getListDriver() throws Exception{
		try {
			return ListDriver.getInstance(IConstants.getJdbcDriverXmlFile());
		} catch (Exception e) {
			throw new Exception("Unable to parse Jdbc Driver file set as :" + IConstants.getJdbcDriverXmlFile(), e);
		}
	}
	
	private static JdbcConnectionProvider getInstance() throws Exception{
		if (instance == null){
			instance = new JdbcConnectionProvider();
			
		}
		return instance;
	}
	
	private JdbcConnectionProvider() throws Exception{
				
		
		
	}
	
	
	/**
	 * 
	 * @param driverName : the key from the jdbcdriver file that reference the wnated driver class
	 * @param host : host of the database
	 * @param port : database port
	 * @param databaseName : database name
	 * @param login : 
	 * @param password
	 * @return
	 * 
	 * @throws Exception
	 */
	public static VanillaJdbcConnection createSqlConnection(String driverName, String host, String port, String databaseName,  String login, String password) throws Exception{
		

		DriverInfo driverInfo =  ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName);
		String driverClass = driverInfo.getClassName();
		try {
			
			
			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)){
				String url = driverInfo.getUrlPrefix() + databaseName ;
				if (!url.trim().endsWith(";")){
					url = url.trim() + ";";
				}
				if (login.equals("")){
					return ConnectionManager.getInstance().getJdbcConnection(url, null, null, driverClass);
				}
				else{
					return ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
				}
				
				
			}
			String url = null;
			if (port == null || "".equals(port)){
				url = driverInfo.getUrlPrefix() + host  + "/" + databaseName;
			}
			else{
				url = driverInfo.getUrlPrefix() + host + ":" + port + "/" + databaseName;
			}
			
			if (driverInfo.getUrlPrefix().startsWith("jdbc:derby://")){
				return ConnectionManager.getInstance().getJdbcConnection(url, null, null, driverClass);
			}
			else{
				return ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			}
			
		}catch (SQLException e) {
			throw new Exception("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}

	public static VanillaJdbcConnection createSqlConnection(String driverName, String url, String login, String password) throws Exception{
		DriverInfo driverInfo =  ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName);
		String driverClass = driverInfo.getClassName();
		try {
			
			
			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)){
				
				if (!url.trim().endsWith(";")){
					url = url.trim() + ";";
				}
				if (login.equals("")){
					return ConnectionManager.getInstance().getJdbcConnection(url, null, null, driverClass);
				}
				else{
					return ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
				}
				
				
			}
			
			if (driverInfo.getUrlPrefix().startsWith("jdbc:derby://")){
				return ConnectionManager.getInstance().getJdbcConnection(url, null, null, driverClass);
			}
			else{
				return ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			}
			
		}catch (SQLException e) {
			throw new Exception("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}
}
