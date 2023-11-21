package bpm.connection.manager.connection.jdbc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * A connection pool used to avoid opening thousands connections.
 * 
 * @author Marc
 * 
 */
public class JdbcConnectionPool {

	private HashMap<String, ComboPooledDataSource> connections = new HashMap<String, ComboPooledDataSource>();
	private URLClassLoader classLoader;

	public JdbcConnectionPool() throws Exception {
		Logger.getLogger(getClass()).debug("Creating a new Jdbc connectionPool");
	}

	/**
	 * Return a connection
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @param driverClass
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (connections.get(key) != null) {
			Connection con = connections.get(key).getConnection();
			if (con.isClosed()) {
				returnConnection(con);
				return getConnection(url, user, password, driverClass);
			}
			return con;
		}

		ComboPooledDataSource pool = createConnectionPool(url, user, password, driverClass);
		connections.put(key, pool);
		return pool.getConnection();
	}

	private String generateConnectionKey(String url, String user, String password, String driverClass) {
		StringBuffer buf = new StringBuffer();
		buf.append(url);
		buf.append(user);
		buf.append(password);
		buf.append(driverClass);
		String key = UUID.nameUUIDFromBytes(buf.toString().getBytes()).toString();
		return key;
	}

	public void returnConnection(Connection connection) throws Exception {
		connection.close();
	}

	public ComboPooledDataSource createConnectionPool(String url, String user, String password, String driverClass) throws Exception {

		loadJdbcDriver(driverClass);

		ComboPooledDataSource pool = new ComboPooledDataSource();

		pool.setAutoCommitOnClose(false);

		if(driverClass.equalsIgnoreCase("org.apache.derby.jdbc.EmbeddedDriver")) {
			pool.setPreferredTestQuery("VALUES 1");
		}
		else if(driverClass.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")) {
			
			pool.setPreferredTestQuery("SELECT 1 FROM DUAL");

			pool.setUser(user);
			pool.setPassword(password);
		}
		else if(driverClass.equalsIgnoreCase("org.apache.hive.jdbc.HiveDriver")) {
			
			pool.setPreferredTestQuery("show tables");

			pool.setUser(user);
			pool.setPassword(password);
		}
		else {
			pool.setPreferredTestQuery("SELECT 1");

			pool.setUser(user);
			pool.setPassword(password);
		}
		pool.setMaxIdleTime(21600);
		pool.setIdleConnectionTestPeriod(10800);

		pool.setUsesTraditionalReflectiveProxies(false);
		
		String poolSize;
		try {
			poolSize = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_CONNECTION_MANAGER_JDBC_POOLS_SIZE);
			if (poolSize == null || poolSize.equals("")) {
				poolSize = "25";
			}
		} catch(Exception e) {
			poolSize = "25";
		}
		
		pool.setInitialPoolSize(1);
		pool.setMinPoolSize(1);

		pool.setMaxPoolSize(Integer.parseInt(poolSize));

		pool.setJdbcUrl(url);
		pool.setDriverClass(driverClass);

		pool.setAcquireRetryAttempts(5);
		pool.setAcquireRetryDelay(100);
		pool.setBreakAfterAcquireFailure(false);
		pool.setTestConnectionOnCheckin(true);
		pool.setTestConnectionOnCheckout(true);

		return pool;
	}

	/**
	 * Load a jdbc driver class
	 * 
	 * @param driverClass
	 * @throws Exception 
	 */
	private void loadJdbcDriver(String driverClass) throws Exception {

		try {
			Class.forName(driverClass, true, classLoader);
//			classLoader.loadClass(driverClass);
		} catch (Exception e) {
			addDriversToClasspath();
//			throw new Exception("The driver file containing the class : " + driverClass + " cannot be found in the classPath");
		}
	}
//
	private static final Class[] parameters = new Class[] { URL.class };

	/**
	 * Add the driver jars to the classPath. This allows to remove the
	 * driverJdbc.xml file and should allow the drivers with dependencies.
	 */
	private void addDriversToClasspath() throws Exception {
		
		Logger.getLogger(getClass()).debug("Adding the Jdbc drivers to the classpath");
		
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
		
//		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
//
//			try {
//				registerDriver(IConstants.getJdbcJarFolder() + "/" + info.getFile(), info.getClassName());
//			} catch(Exception e) {
////				e.printStackTrace();
//			}
//		}
		
//		URLClassLoader ucl = new URLClassLoader(urls, osgiClassLoader);
//		Driver d = (Driver) Class.forName(driverClassName, true, ucl).newInstance();
		
//		Class sysclass = URLClassLoader.class;
//
//		Method method = sysclass.getDeclaredMethod("addURL", parameters);
//		method.setAccessible(true);
//
//		// loop on the files
//		File jdbcFolder = new File(jdbcDriverFolder);
//		for (File jarFile : jdbcFolder.listFiles()) {
//			URL jarFileUrl = jarFile.toURI().toURL();
//			try {
//				method.invoke(URLClassLoader.newInstance(), new Object[] { jarFileUrl });
//			} catch (Throwable t) {
//				t.printStackTrace();
//				throw new Exception("Error, cannot load the jar : " + jarFileUrl.toString());
//			}
//		}

	}

	private void registerDriver(String path, String driverClassName) throws Exception {
//		try {
//			Class.forName(driverClassName);
//			return;
//		} catch (ClassNotFoundException e) {
////			Activator.getLogger().warn("JDBCConnectionProvider : registering driver : JDBC Driver " + driverClassName + " yet loaded");
//		}
//
//		URL u;
//		File f = new File(path);
//		u = f.toURL();
//
//		if(classLoader == null) {
//			classLoader = new URLClassLoader(new URL[] { u });
//		}
//		else {
//			URL[] urls = classLoader.getURLs();
//			List<URL> newUrls = Arrays.asList(urls);
//			newUrls.add(u);
//			
//			classLoader = new URLClassLoader(newUrls.toArray(new URL[newUrls.size()]));
//		}
//		Driver d = (Driver) Class.forName(driverClassName, true, classLoader).newInstance();
//		DriverManager.registerDriver(d);
		List<URL> newUrls = new ArrayList<URL>();
		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {

//			try {
//				registerDriver(IConstants.getJdbcJarFolder() + "/" + info.getFile(), info.getClassName());
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
			
			try {
				Class.forName(info.getClassName());
				return;
			} catch (ClassNotFoundException e) {
//				Activator.getLogger().warn("JDBCConnectionProvider : registering driver : JDBC Driver " + driverClassName + " yet loaded");
			}

			URL u;
			File f = new File(IConstants.getJdbcJarFolder() + "/" + info.getFile());
			u = f.toURL();
			newUrls.add(u);
		}
		
		classLoader = new URLClassLoader(newUrls.toArray(new URL[newUrls.size()]));
		
		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
			Driver d = (Driver) Class.forName(info.getClassName(), true, classLoader).newInstance();
			DriverManager.registerDriver(d);
		}
	}
	
}
