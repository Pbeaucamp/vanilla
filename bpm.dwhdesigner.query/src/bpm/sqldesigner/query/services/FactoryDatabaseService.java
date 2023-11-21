package bpm.sqldesigner.query.services;

import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.dwhview.DwhDbConnection;

public class FactoryDatabaseService {
	
	public static AbstractDatabaseService getDataBaseService(DataBaseConnection connection){
		if (connection instanceof DwhDbConnection){
			return new DwhDatabaseService((DwhDbConnection)connection);
		}
		else{
			return new DatabaseServices(connection);
		}
	}
	
}
