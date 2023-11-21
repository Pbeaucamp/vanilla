package bpm.connection.manager.connection;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;

import bpm.connection.manager.JdbcUrlHelper;
import bpm.connection.manager.connection.jdbc.BasicJdbcConnectionPool;
import bpm.connection.manager.connection.jdbc.JdbcConnectionPool;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.oda.OdaConnectionPool;
import bpm.connection.manager.connection.oda.VanillaOdaConnection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;


/**
 * A class providing vanilla jdbc and oda connection
 * The purpose of this is to securize connection to handle concurrency, timeout, etc...
 * @author Marc
 *
 */
public class ConnectionManager {

	private static ConnectionManager instance;
	private JdbcConnectionPool jdbcPool;
	private OdaConnectionPool odaPool;
	private BasicJdbcConnectionPool basicPool;
	
	private ConnectionManager() throws Exception {
		jdbcPool = new JdbcConnectionPool();
		odaPool = new OdaConnectionPool();
		basicPool = new BasicJdbcConnectionPool();
	}
	
	public static ConnectionManager getInstance() throws Exception {
		if(instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	public VanillaJdbcConnection getJdbcConnection(DatasourceJdbc datasource) throws Exception {
		VanillaJdbcConnection connection;
		if(datasource.getFullUrl()){
			return connection = ConnectionManager.getInstance().getJdbcConnection(datasource.getUrl(), datasource.getUser(), datasource.getPassword(), datasource.getDriver());
		} else {
			return connection = ConnectionManager.getInstance().getJdbcConnection(JdbcUrlHelper.getJdbcUrl(datasource.getHost(), datasource.getPort(), datasource.getDatabaseName(), datasource.getDriver()), datasource.getUser(), datasource.getPassword(), datasource.getDriver());
		}
	}
	
	public VanillaJdbcConnection getJdbcConnection(String url, String user, String password, String driverClass) throws Exception {
		if(BasicJdbcConnectionPool.drivers.contains(driverClass)) {
			Connection c = basicPool.getConnection(url, user, password, driverClass);
			return new VanillaJdbcConnection(url, user, password, driverClass, c);
		}
		
		Connection c = jdbcPool.getConnection(url, user, password, driverClass);
		return new VanillaJdbcConnection(url, user, password, driverClass, c);
	}
	
	public void returnJdbcConnection(VanillaJdbcConnection connection) throws Exception {
		if(BasicJdbcConnectionPool.drivers.contains(connection.getDriverClass())) {
			basicPool.returnConnection(connection.getJdbcConnection());
		}
		else {
			jdbcPool.returnConnection(connection.getJdbcConnection());
		}
	}
	
	public VanillaOdaConnection getOdaConnection(Properties publicProperties, Properties privateProperties, String datasourceId) throws Exception {
		IConnection c = odaPool.getConnection(publicProperties, privateProperties, datasourceId);
		return new VanillaOdaConnection(c, publicProperties, privateProperties, datasourceId);
	}
	
	public void returnOdaConnection(VanillaOdaConnection connection) {
		odaPool.returnOdaConnection(connection.getOdaConnection());
	}
}
