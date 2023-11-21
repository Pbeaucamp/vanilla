package bpm.gateway.core.server.database;

import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;

public class DataBaseFactory {

	public static DataBaseServer create(String name, String description, DataBaseConnection connection) throws ServerException{
		DataBaseServer dataBaseServer = new DataBaseServer();
		
		dataBaseServer.addConnection(connection);
		dataBaseServer.setCurrentConnection(connection);
		dataBaseServer.setName(name);
		dataBaseServer.setDescription(description);
		
		return dataBaseServer;
	}

	public static Server createFreemetrics(String fmLogin, String fmPassword, String name, String description,	DataBaseConnection connection ) throws ServerException{
		FreemetricServer dataBaseServer = new FreemetricServer();
		dataBaseServer.setFmLogin(fmLogin);
		dataBaseServer.setFmPassword(fmPassword);
		dataBaseServer.addConnection(connection);
		dataBaseServer.setCurrentConnection(connection);
		dataBaseServer.setName(name);
		dataBaseServer.setDescription(description);
		
		return dataBaseServer;
	}
	
	
	
}
