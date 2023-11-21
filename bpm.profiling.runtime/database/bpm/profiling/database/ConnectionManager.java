package bpm.profiling.database;

import java.util.List;

import bpm.profiling.database.dao.ConnectionDao;
import bpm.profiling.runtime.core.Connection;

public class ConnectionManager {
	private ConnectionDao connectionDao;

	public ConnectionDao getConnectionDao() {
		return connectionDao;
	}

	public void setConnectionDao(ConnectionDao connectionDao) {
		this.connectionDao = connectionDao;
	}
	
	/**
	 * add and the database generated id
	 * @param c
	 */
	public void addConnection(Connection c){
		connectionDao.add(c);
	}
	
	public void deleteConnection(Connection c){
		
//		Helper.getInstance().getAnalysisManager().getAllAnalysisContentFor(analysisInfo)
		
		
		connectionDao.delete(c);
	}
	
	public void updateConnection(Connection c){
		connectionDao.update(c);
	}
	
	public List<Connection> getConnections(){
		return connectionDao.getAll();
	}

	public Connection getConnection(int id) {
		return connectionDao.getById(id);
	}
}
