package bpm.oda.driver.reader.model.odaconsummer;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.oda.driver.reader.model.datasource.DataSource;


public class ConnectionManager {
	private static ConnectionManager instance;
	public static HashMap<Properties, IConnection> openedConnection = new HashMap<Properties, IConnection>();
	
	private ConnectionManager(){}
	
	
	private static ConnectionManager getInstance(){
		if (instance == null){
			instance = new ConnectionManager();
		}
		return instance;
	}
	

	public static Connection openConnection(DataSource dataSource) throws Exception{
		IDriver odaDriver = DriverManager.getOdaDriver(dataSource);
		Properties dataSourceProperties = dataSource.getProperties(); 
		
		for(Properties p : openedConnection.keySet()){
			if (p.equals(dataSourceProperties)){
				if (openedConnection.get(p).isOpen()){
					return new Connection(dataSource.getOdaExtensionDataSourceId(), openedConnection.get(p)); 
				}
			}
		}
		
		IConnection c = odaDriver.getConnection(dataSource.getName());
		c.open(dataSourceProperties);
		openedConnection.put(dataSource.getProperties(), c);
		
		return new Connection(dataSource.getOdaExtensionDataSourceId(), c);
	}
	
	public static Connection buildConnection(DataSource dataSource) throws Exception{
		IDriver odaDriver = DriverManager.getOdaDriver(dataSource);
		Properties dataSourceProperties = dataSource.getProperties(); 
		
		IConnection c = odaDriver.getConnection(dataSource.getName());
		c.open(dataSourceProperties);
		
		return new Connection(dataSource.getOdaExtensionDataSourceId(), c);
		
		
	}
	
}
