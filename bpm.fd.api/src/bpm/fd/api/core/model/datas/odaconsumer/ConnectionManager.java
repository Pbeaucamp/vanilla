package bpm.fd.api.core.model.datas.odaconsumer;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.fd.api.core.model.datas.DataSource;


public class ConnectionManager {
	private static HashMap<Properties, IConnection> openedConnection = new HashMap<Properties, IConnection>();
	
	private ConnectionManager(){}

	public static Connection openConnection(DataSource dataSource) throws Exception{
		IDriver odaDriver = DriverManager.getOdaDriver(dataSource);
		Properties dataSourceProperties = dataSource.getProperties(); 
		for(Properties p : openedConnection.keySet()){
			if (p.equals(dataSourceProperties)){
				if (openedConnection.get(p).isOpen()){
					IConnection c = openedConnection.get(p);
					
//					try {
//						if(dataSource.getOdaExtensionId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
//							c.close();
//							return openConnection(dataSource);
//						}
//					} catch(Exception e) {
//						c.close();
//						return openConnection(dataSource);
//					}
					return new Connection(dataSource.getOdaExtensionDataSourceId(), c); 
				}
			}
		}
		
		IConnection c = odaDriver.getConnection(dataSource.getOdaExtensionDataSourceId());
		c.open(dataSourceProperties);
		openedConnection.put(dataSource.getProperties(), c);
		
		return new Connection(dataSource.getOdaExtensionDataSourceId(), c);
		
		
	}
}
