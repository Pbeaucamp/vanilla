package bpm.sqldesigner.api.database;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
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
	private ListDriver listDriver;
	
	private static String jdbcDriverFolder;
	private static String xmlJdbcDriverFile;
	
	private static boolean isInited = false;
	
	/**
	 * ERE:
	 * @param driverClass
	 * @return the driver name, null if not found
	 * @throws JdbcException 
	 */
	public static String findDriverForDriverClass(String driverClass) throws Exception {
		if (!isInited){
			throw new Exception("This method is not usable until the instance is inited");
		}
		
		for (String driverName : getInstance().listDriver.getDriversName()) {
			DriverInfo info = getInstance().listDriver.getInfo(driverName);
			if (info.getClassName().equals(driverClass))
				return driverName;
		}
		
		return null;
	}
	
	/**
	 * init some external files Location taht contains JDBC drivers
	 * @param jdbcDriverFolder : folder containing JDBC jars
	 * @param xmlJdbcDriverFile : xml file containing the JDBC driver description 
	 */
	public static void init(String jdbcDriverFolder, String xmlJdbcDriverFile){
		JdbcConnectionProvider.jdbcDriverFolder = jdbcDriverFolder;
		JdbcConnectionProvider.xmlJdbcDriverFile = xmlJdbcDriverFile;
		isInited = true;
	}
	
	public static ListDriver getListDriver() throws Exception{
		return getInstance().listDriver;
	}
	
	private static JdbcConnectionProvider getInstance() throws Exception{
		if (instance == null){
			instance = new JdbcConnectionProvider();
			try {
				instance.listDriver = ListDriver.getInstance(jdbcDriverFolder);
			} catch (Exception e) {
				throw new Exception("Unable to get the JDBC driver list from the xml file", e);
			}
		}
		return instance;
	}
	
	private JdbcConnectionProvider() throws Exception{
		if (!isInited){
			throw new Exception("Cannot instantiate the JdbcConnectionDriver because the Class have not been inited");
		}
		
		try {
			ListDriver.getInstance(xmlJdbcDriverFile);
		} catch (Exception e) {
			throw new Exception("Unable to parse Jdbc Driver file set as :" + xmlJdbcDriverFile, e);
		}
		
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
	 * @throws JdbcException
	 */
	public static Connection createSqlConnection(String driverName, String host, String port, String databaseName,  String login, String password) throws Exception{
		

		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		if (driverInfo == null){
			throw new Exception("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		try {
			registerDriver(jdbcDriverFolder + "/" + driverInfo.getFile(), driverInfo.getClassName());
			
			
			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)){
				String url = driverInfo.getUrlPrefix() + databaseName ;
				if (!url.trim().endsWith(";")){
					url = url.trim() + ";";
				}
				if (login.equals("")){
					return DriverManager.getConnection(url, null, null);
				}
				else{
					return DriverManager.getConnection(url, login, password);
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
				return DriverManager.getConnection(url, null, null);
			}
			else{
				return DriverManager.getConnection(url, login, password);
			}
			
		}catch (SQLException e) {
			throw new Exception("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * register the JDBC Driver class if not already 
	 * @param path : the jdbc driver jar file
	 * @param driverClassName the jdbc driver java class name
	 * @throws Exception
	 */
	private static void registerDriver(String path, String driverClassName) throws Exception {
		try{
			Class.forName(driverClassName);
			return;
		}catch(ClassNotFoundException e){
//			System.out.println();
		}
		
		
		URL u;
		File f = new File(path);
		u = f.toURL();

		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver d = (Driver)Class.forName(driverClassName, true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(d));
	}

	public static Connection createSqlConnection(String driverName, String url, String login, String password) throws Exception{
		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		
		if (driverInfo == null){
			throw new Exception("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		
		try {
			registerDriver(jdbcDriverFolder + "/" + driverInfo.getFile(), driverInfo.getClassName());
			
			
			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)){
				
				if (!url.trim().endsWith(";")){
					url = url.trim() + ";";
				}
				if (login.equals("")){
					return DriverManager.getConnection(url, null, null);
				}
				else{
					return DriverManager.getConnection(url, login, password);
				}
				
				
			}
			
			if (driverInfo.getUrlPrefix().startsWith("jdbc:derby://")){
				return DriverManager.getConnection(url, null, null);
			}
			else{
				return DriverManager.getConnection(url, login, password);
			}
			
		}catch (SQLException e) {
			throw new Exception("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}
}
