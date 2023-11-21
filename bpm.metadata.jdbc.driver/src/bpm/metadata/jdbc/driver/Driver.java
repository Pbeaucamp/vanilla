package bpm.metadata.jdbc.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;



public class Driver implements java.sql.Driver {
	
	@Override
	public Connection connect(String url, Properties info) throws SQLException { 
		
		return new bpm.metadata.jdbc.driver.Connection(url,info);

		/*try {
			con = new Connection(url, info);
		} catch (SQLException e) {  
			e.printStackTrace();		   
			  }
		return con;*/
		
		//return null;
	}
	
	

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		if (url == null) {
            return false;
    }
    return url.toLowerCase().startsWith("jdbc:fmdt");		
	}

}
