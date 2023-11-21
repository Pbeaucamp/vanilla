package bpm.gateway.core.server.database.jdbc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import bpm.gateway.core.Activator;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.JdbcException;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;

/**
 * This class is an helper class to load dynamically JDBC drivers and to provide
 * a Jdbc Connection
 * 
 * 
 * If you try to use this class without running the init method before, a
 * JdbcException will be thrown
 * 
 * @author LCA
 * 
 */
public class JdbcConnectionProvider {

	private static JdbcConnectionProvider instance = null;
	private ListDriver listDriver;

	// private static String jdbcDriverFolder;
	// private static String xmlJdbcDriverFile;

	/**
	 * ERE:
	 * 
	 * @param driverClass
	 * @return the driver name, null if not found
	 * @throws JdbcException
	 */
	public static String findDriverForDriverClass(String driverClass) throws JdbcException {

		for (String driverName : getInstance().listDriver.getDriversName()) {
			DriverInfo info = getInstance().listDriver.getInfo(driverName);
			if (info.getClassName().equals(driverClass))
				return driverName;
		}

		return null;
	}

	// /**
	// * init some external files Location taht contains JDBC drivers
	// * @param jdbcDriverFolder : folder containing JDBC jars
	// * @param xmlJdbcDriverFile : xml file containing the JDBC driver
	// description
	// */
	// public static void init(String jdbcDriverFolder, String
	// xmlJdbcDriverFile){
	// JdbcConnectionProvider.jdbcDriverFolder = jdbcDriverFolder;
	// JdbcConnectionProvider.xmlJdbcDriverFile = xmlJdbcDriverFile;
	// isInited = true;
	// }

	public static ListDriver getListDriver() throws JdbcException {
		return getInstance().listDriver;
	}

	private static JdbcConnectionProvider getInstance() throws JdbcException {
		if (instance == null) {
			instance = new JdbcConnectionProvider();
			try {
				instance.listDriver = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile());
			} catch (Exception e) {
				throw new JdbcException("Unable to get the JDBC driver list from the xml file", e);
			}
		}
		return instance;
	}

	private JdbcConnectionProvider() throws JdbcException {

		try {
			ListDriver.getInstance(IConstants.getJdbcDriverXmlFile());
		} catch (Exception e) {
			Activator.getLogger().error(e);
			throw new JdbcException("Unable to parse Jdbc Driver file set as :" + IConstants.getJdbcDriverXmlFile(), e);
		}

	}

	/**
	 * 
	 * @param driverName
	 *            : the key from the jdbcdriver file that reference the wnated
	 *            driver class
	 * @param host
	 *            : host of the database
	 * @param port
	 *            : database port
	 * @param databaseName
	 *            : database name
	 * @param login
	 *            :
	 * @param password
	 * @return
	 * 
	 * @throws JdbcException
	 */
	public static Connection createSqlConnection(String driverName, String host, String port, String databaseName, String login, String password) throws JdbcException {

		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		if (driverInfo == null) {
			throw new JdbcException("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		try {
			registerDriver(IConstants.getJdbcJarFolder() + "/" + driverInfo.getFile(), driverInfo.getClassName());

			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)) {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				String url = driverInfo.getUrlPrefix() + databaseName;
				if (!url.trim().endsWith(";")) {
					url = url.trim() + ";";
				}

				if (login.equals("")) {
					return DriverManager.getConnection(url, null, null);
				}
				else {
					return DriverManager.getConnection(url, login, password);
				}

			}
			String url = null;
			if (port == null || "".equals(port)) {
				url = driverInfo.getUrlPrefix() + host + "/" + databaseName;
			}
			else {
				url = driverInfo.getUrlPrefix() + host + ":" + port + "/" + databaseName;
			}
			
			if (driverInfo.getUrlPrefix().startsWith("jdbc:derby://")) {
				return DriverManager.getConnection(url, null, null);
			}
			else {
				return DriverManager.getConnection(url, login, password);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new JdbcException("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			throw new JdbcException("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}

	public static String getFullUrl(String driverName, String host, String port, String databaseName) throws JdbcException {
		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		if (driverInfo == null) {
			throw new JdbcException("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		try {
			registerDriver(IConstants.getJdbcJarFolder() + "/" + driverInfo.getFile(), driverInfo.getClassName());

			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)) {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				String url = driverInfo.getUrlPrefix() + databaseName;
				if (!url.trim().endsWith(";")) {
					url = url.trim() + ";";
				}
				return url;
			}

			String url = null;
			if (port == null || "".equals(port)) {
				url = driverInfo.getUrlPrefix() + host + "/" + databaseName;
			}
			else {
				url = driverInfo.getUrlPrefix() + host + ":" + port + "/" + databaseName;
			}

			return url;
		} catch (Exception e) {
			throw new JdbcException("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}

	public static String createUrlConnection(String driverName, String host, String port, String databaseName) throws JdbcException {
		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		if (driverInfo == null) {
			throw new JdbcException("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		try {
			registerDriver(IConstants.getJdbcJarFolder() + "/" + driverInfo.getFile(), driverInfo.getClassName());

			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)) {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				String url = driverInfo.getUrlPrefix() + databaseName;
				if (!url.trim().endsWith(";")) {
					url = url.trim() + ";";
				}

				return url;
			}

			String url = null;
			if (port == null || "".equals(port)) {
				url = driverInfo.getUrlPrefix() + host + "/" + databaseName;
			}
			else {
				url = driverInfo.getUrlPrefix() + host + ":" + port + "/" + databaseName;
			}

			return url;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new JdbcException("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			throw new JdbcException("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}

	public static DriverInfo getDriver(String driverName) throws JdbcException {
		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);
		if (driverInfo == null) {
			throw new JdbcException("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}
		return driverInfo;
	}

	private static String manageUrl(DocumentGateway document, String url) {
		try {
			if (document != null) {
				return document.getStringParser().getValue(document, url);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * register the JDBC Driver class if not already
	 * 
	 * @param path
	 *            : the jdbc driver jar file
	 * @param driverClassName
	 *            the jdbc driver java class name
	 * @throws Exception
	 */
	private static void registerDriver(String path, String driverClassName) throws Exception {
		try {
			Class.forName(driverClassName);
			return;
		} catch (ClassNotFoundException e) {
			Activator.getLogger().warn("JDBCConnectionProvider : registering driver : JDBC Driver " + driverClassName + " yet loaded");
		}

		URL u;
		File f = new File(path);
		u = f.toURL();

		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver d = (Driver) Class.forName(driverClassName, true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(d));
	}

	public static Connection createSqlConnection(DocumentGateway document, String driverName, String url, String login, String password) throws JdbcException {
		DriverInfo driverInfo = getInstance().listDriver.getInfo(driverName);

		if (driverInfo == null) {
			throw new JdbcException("No JDBC driver with name " + driverName + " found in the driverjdbc.xml file");
		}

		try {
			registerDriver(IConstants.getJdbcJarFolder() + "/" + driverInfo.getFile(), driverInfo.getClassName());

			if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)) {

				if (!url.trim().endsWith(";")) {
					url = url.trim() + ";";
				}
				
				url = manageUrl(document, url);
				
				if (login.equals("")) {
					return DriverManager.getConnection(url, null, null);
				}
				else {
					return DriverManager.getConnection(url, login, password);
				}

			}
			
			url = manageUrl(document, url);

			if (driverInfo.getUrlPrefix().startsWith("jdbc:derby://")) {
				return DriverManager.getConnection(url, null, null);
			}
			else {
				return DriverManager.getConnection(url, login, password);
			}

		} catch (SQLException e) {
			throw new JdbcException("Unable to create SqlConnection :" + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdbcException("Error while registering Jdbc Driver for " + driverName + " : " + e.getMessage(), e);
		}
	}
}
