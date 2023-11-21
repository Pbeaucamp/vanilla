package bpm.connection.manager.connection.jdbc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * A connection pool which doesn't use c3p0.
 * Some Jdbc drivers aren't working with it.
 * 
 * 
 * @author Marc
 *
 */
public class BasicJdbcConnectionPool {

	public static List<String> drivers = new ArrayList<String>();
	
	private URLClassLoader classLoader;
	
	static {
		drivers.add("org.apache.drill.jdbc.Driver");
		drivers.add("org.postgresql.Driver");
		drivers.add("org.relique.jdbc.csv.CsvDriver");
		drivers.add("oracle.jdbc.driver.OracleDriver");
		drivers.add("com.ibm.as400.access.AS400JDBCDriver");
		drivers.add("com.mysql.jdbc.Driver");
	}

	private String jdbcDriverFolder;
	
	public BasicJdbcConnectionPool() throws Exception {
//		jdbcDriverFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_JDBC_DRIVER_FOLDER);
//		Logger.getLogger(getClass()).debug("Creating a new Basic connectionPool");
//		addDriversToClasspath();

	}
	
	public Connection getConnection(String url, String user, String password, String driverClass) throws Exception {
		
		try {
			Class.forName(driverClass, true, classLoader);
//			Driver d = (Driver) Class.forName(driverClass, true, classLoader).newInstance();
//			DriverManager.registerDriver(new DriverShim(d));
		} catch(Exception e) {
			loadJdbcDriver(driverClass);
		}
		
		Connection c = DriverManager.getConnection(url, user, password);
		
		return c;
	}
	
	public void returnConnection(Connection connection) throws Exception {
		connection.close();
	}
	
	private void loadJdbcDriver(String driverClass) throws Exception {
		addDriversToClasspath();
	}
//
	private static final Class[] parameters = new Class[] { URL.class };

	/**
	 * Add the driver jars to the classPath. This allows to remove the
	 * driverJdbc.xml file and should allow the drivers with dependencies.
	 */
	private void addDriversToClasspath() throws Exception {
//		Logger.getLogger(getClass()).debug("Adding the Jdbc drivers to the classpath");
		
		List<URL> newUrls = new ArrayList<URL>();
		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {

			URL u;
			File f = new File(IConstants.getJdbcJarFolder() + "/" + info.getFile());
			u = f.toURL();
			newUrls.add(u);
		}
		
		classLoader = new URLClassLoader(newUrls.toArray(new URL[newUrls.size()]));
		
		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
			Driver d = (Driver) Class.forName(info.getClassName(), true, classLoader).newInstance();
			DriverManager.registerDriver(new DriverShim(d));
		}

	}

	private void registerDriver(String path, String driverClassName) throws Exception {

	}
}
