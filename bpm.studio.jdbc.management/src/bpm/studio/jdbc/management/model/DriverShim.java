package bpm.studio.jdbc.management.model;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {
	private Driver driver;
	
	public DriverShim(Driver driver){
		this.driver = driver;
	}

	public boolean acceptsURL(String url) throws SQLException {
		return this.driver.acceptsURL(url);
	}

	public Connection connect(String url, Properties info) throws SQLException {
		return this.driver.connect(url, info);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return this.driver.getPropertyInfo(url, info);
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}
	
	public Driver getMappedJdbcDriver(){
		return driver;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
