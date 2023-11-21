package bpm.dataprovider.odainput.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.vanilla.platform.core.beans.data.OdaInput;

public class QueryHelper {
	private static HashMap<IConnection, List<IQuery>> connections = new HashMap<IConnection, List<IQuery>>(); 
	
	public static IQuery buildquery(OdaInput input) throws Exception{
		IConnection c = ConnectionManager.openConnection(input).getOdaConnection();
		
		synchronized(connections){
			if (connections.get(c) == null ){
				connections.put(c, new ArrayList<IQuery>());
			}
			
			IQuery q = null; 			
			try {
				q = c.newQuery("");
			} catch (Exception e) {
				if(c.getMaxQueries() == 1) {
					c.close();
					connections.remove(c);
					c = ConnectionManager.openConnection(input).getOdaConnection();
					if (connections.get(c) == null ){
						connections.put(c, new ArrayList<IQuery>());
					}
					q = c.newQuery("");
				}
				else {
					e.printStackTrace();
					throw e;
				}
				
			}
			q.prepare(input.getQueryText());
			connections.get(c).add(q);
			return q;
		}
		
		
	}
	
	/**
	 * Close the Connection holding teh given query
	 * if the HashMap contains one unique IQuery for the Connection
	 * 
	 * use removeQuery once the query is Closed to remove the closed query
	 * from the HashMap
	 */
	public static void closeConnectionFor(IQuery query) throws Exception{
//		synchronized(connections){
//			IConnection toRm = null;
//			for(IConnection c : connections.keySet()){
//				if (connections.get(c).contains(query) && connections.get(c).size() == 1){
//					c.close();
//					toRm = c;
//					
//				}
//			}
//			connections.remove(toRm);
//		}
		
	}
	
	public static void removeQuery(IQuery q){
		synchronized(connections){
			for(IConnection c : connections.keySet()){
				connections.get(c).remove(q);
			}
		}
		
	}

	public static void closeAllConnections() throws OdaException {
		for(IConnection c : connections.keySet()){
//			if (connections.get(c).contains(query) && connections.get(c).size() == 1){
				c.close();
//				toRm = c;
//				
//			}
		}
		
		connections.clear();
		
	}
}
