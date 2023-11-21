package bpm.vanilla.repository.beans;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class JdbcConnectionProvider {

	private static JdbcConnectionProvider instance = null;
	
	private HashMap<Integer, InfoConnections> infoMap = new HashMap<Integer, InfoConnections>();
	
	private static ListDriver listDriver;
	
	private HashMap<InfoConnections, JdbcConnectionPool> pools = new HashMap<InfoConnections, JdbcConnectionPool>();

	public static JdbcConnectionProvider getInstance() throws Exception {
		if (instance == null) {
			instance = new JdbcConnectionProvider();
		}
		return instance;
	}

	public JdbcConnectionProvider() throws Exception {
		listDriver = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile());
	}
	
	public InfoConnections getInfoConnection(int repositoryId) throws Exception {
		if(infoMap.get(repositoryId) == null) {
			String filePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_REPOSITORY_FILE + repositoryId);

			FileInputStream fis = new FileInputStream(filePath);
			
			Properties props = new Properties();
			props.load(fis);

			String driver = props.getProperty(VanillaConfiguration.P_REPOSITORY_DB_DRIVERCLASSNAME);
			
			String repositoryDBUrl = props.getProperty(VanillaConfiguration.P_REPOSITORY_DB_JDBCURL);
			String login = props.getProperty(VanillaConfiguration.P_REPOSITORY_DB_USERNAME);
			String password = props.getProperty(VanillaConfiguration.P_REPOSITORY_DB_PASSWORD);
			
			fis.close();
			
			InfoConnections info = new InfoConnections(login, password, repositoryDBUrl, driver);
			infoMap.put(repositoryId, info);
			
			return info;
		}
		else {
			return infoMap.get(repositoryId);
		}
	}

	
	
	public Connection createSqlConnection(int repositoryId) throws Exception {
		InfoConnections info = getInfoConnection(repositoryId);
		registerDriver(info);
		
		if(pools.get(info) == null) {
			pools.put(info, new JdbcConnectionPool(info));
		}
		
		return pools.get(info).getAvailableConnection();
	}
	

	public void returnConnection(int repositoryId, Connection connection) throws Exception {
		InfoConnections info = getInfoConnection(repositoryId);
		pools.get(info).returnConnection(connection);
	}

	/**
	 * register the JDBC Driver class if not already
	 * 
	 * @param info
	 *            the jdbc driver java class name
	 * @throws Exception
	 */
	private static void registerDriver(InfoConnections info) throws Exception {
		try {
			Class.forName(info.getDriver());
			return;
		} catch (ClassNotFoundException e) {
//			throw new Exception("JDBCConnectionProvider : registering driver : JDBC Driver " + info + " yet loaded");
		}
		DriverInfo dInfo = null;
		Iterator<DriverInfo> it = listDriver.getDriversInfo().iterator();
		while(it.hasNext()) {
			dInfo = it.next();
			if(dInfo.getClassName().equals(info.getDriver())) {
				break;
			}
		}
		
		URL u;
		File f = new File(IConstants.getJdbcJarFolder() + "/" + dInfo.getFile());
		u = f.toURL();

		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver d = (Driver)Class.forName(info.getDriver(), true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(d));
	}

}
