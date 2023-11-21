package bpm.gateway.core.transformations.inputs.odaconsumer;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.gateway.core.transformations.inputs.OdaInput;


public class ConnectionManager {
	private static ConnectionManager instance;
	private static HashMap<Properties, IConnection> openedConnection = new HashMap<Properties, IConnection>();
	
	private ConnectionManager(){}
	
	
	private static ConnectionManager getInstance(){
		if (instance == null){
			instance = new ConnectionManager();
		}
		return instance;
	}
	

	public static Connection openConnection(OdaInput input) throws Exception{
		IDriver odaDriver = DriverManager.getOdaDriver(input);
		Properties dataSourceProperties = new Properties();
		dataSourceProperties.putAll(input.getDatasourcePublicProperties());
		dataSourceProperties.putAll(input.getDatasourcePrivateProperties());
		for(Properties p : openedConnection.keySet()){
			if (p.equals(dataSourceProperties)){
				if (openedConnection.get(p).isOpen()){
					return new Connection(input.getOdaExtensionDataSourceId(), openedConnection.get(p)); 
				}
			}
		}
		
		IConnection c = odaDriver.getConnection(input.getName());
		c.open(dataSourceProperties);
		openedConnection.put(dataSourceProperties, c);
		
		return new Connection(input.getOdaExtensionDataSourceId(), c);
		
		
	}
}
