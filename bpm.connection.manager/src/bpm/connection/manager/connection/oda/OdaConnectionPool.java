package bpm.connection.manager.connection.oda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.connection.manager.connection.oda.driver.DriverManager;

public class OdaConnectionPool {

	private HashMap<Properties, List<IConnection>> connections = new HashMap<Properties, List<IConnection>>();

	public IConnection getConnection(Properties publicProperties, Properties privateProperties, String datasourceId) throws Exception {
		IDriver odaDriver = DriverManager.getOdaDriver(datasourceId);
		Properties dataSourceProperties = new Properties();
		dataSourceProperties.putAll(publicProperties);
		dataSourceProperties.putAll(privateProperties);
		for(Properties p : connections.keySet()){
			if (p.equals(dataSourceProperties)){				
				for(IConnection con : connections.get(p)) {
					if(con.isOpen()) {
						return con;
					}
				}
			}
		}
		
		IConnection c = odaDriver.getConnection(datasourceId);
		c.open(dataSourceProperties);
		if(connections.get(dataSourceProperties) == null) {
			connections.put(dataSourceProperties, new ArrayList<IConnection>());
		}
		connections.get(dataSourceProperties).add(c);
		
		return c;
	}

	public void returnOdaConnection(IConnection odaConnection) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
