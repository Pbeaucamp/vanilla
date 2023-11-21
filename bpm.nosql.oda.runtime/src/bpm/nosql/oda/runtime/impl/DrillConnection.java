package bpm.nosql.oda.runtime.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

import com.ibm.icu.util.ULocale;

public class DrillConnection implements IConnection{

	public static String URL = "URL";
	public static String USER = "USER";
	public static String PASSWORD = "PASSWORD";
	
	private static String DRIVER = "org.apache.drill.jdbc.Driver";
	
	private VanillaJdbcConnection connection;
	
	@Override
	public void close() throws OdaException {
		try {
			ConnectionManager.getInstance().returnJdbcConnection(connection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commit() throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxQueries() throws OdaException {
		return 0;
	}

	@Override
	public IDataSetMetaData getMetaData(String arg0) throws OdaException {
		return new DataSetMetaData(new Connection());
	}

	@Override
	public boolean isOpen() throws OdaException {
		try {
			if(connection != null && !connection.isClosed()) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public IQuery newQuery(String arg0) throws OdaException {
		return new DrillQuery(this);
	}

	@Override
	public void open(Properties props) throws OdaException {
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(props.getProperty(URL), props.getProperty(USER), props.getProperty(PASSWORD), DRIVER);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void rollback() throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAppContext(Object arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocale(ULocale arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	public VanillaJdbcConnection getVanillaConnection() {
		return connection;
	}

}
