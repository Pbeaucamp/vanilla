package bpm.nosql.oda.runtime.impl;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class ConnectionManager {
	private static HashMap<Properties, IConnection> openedConnection = new HashMap<Properties, IConnection>();

	public static IConnection getConnection(Properties p) throws OdaException{
		for(Properties key : openedConnection.keySet()){
			if (key.equals(p)){
				IConnection c = openedConnection.get(key);
				if (c != null && c.isOpen()){

					/*
					 * we check if the model have been updated
					 */

					return c;
				}
			}
		}
		IConnection conn;
		if(p.getProperty("configfile") != null){
			conn = new HbaseDriver().getConnection(null);
		}else{
			conn = new Driver().getConnection(null);
		}
		
		conn.open(p);
		openedConnection.put(p, conn);

		return conn;			
	}
	
	public static void closeAll(){
		for(IConnection c : openedConnection.values()){
			try{
				if (c != null &&  c.isOpen()){
					c.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}
}
