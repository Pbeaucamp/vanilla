package bpm.vanilla.repository.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class JdbcConnectionPool {

	private List<Connection> connections = new ArrayList<Connection>();
	private InfoConnections infoConnections;
	
	private List<Integer> inUse = new ArrayList<Integer>();
	
	private int poolSize = 5;
	
	public JdbcConnectionPool(InfoConnections infoConnections) {
		this.infoConnections = infoConnections;
		try {
			this.poolSize = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_REPOSITORY_POOL_SIZE));
		} catch(NumberFormatException e) {
			this.poolSize = 5;
		}
	}
	
	public synchronized Connection getAvailableConnection() throws Exception {
		while(inUse.size() == connections.size() && connections.size() == poolSize)  {
			Thread.sleep(50);
		}
		
		if(connections.size() < poolSize) {
			return createConnection(-1);
		}
		
		else {
			for(int i = 0 ; i < poolSize ; i++) {
				if(!inUse.contains(new Integer(i))) {
					Connection c = connections.get(i);
					if(c.isClosed()) {
						connections.remove(c);
						return createConnection(i);
					}
					return c;
				}
			}
		}
		throw new Exception("Impossible to get a connection");
	}
	
	private Connection createConnection(int position) throws Exception {
		Connection c = DriverManager.getConnection(infoConnections.getRepositoryDBUrl(), infoConnections.getLogin(), infoConnections.getPassword());
		if(position < 0) {
			connections.add(c);
		}
		else {
			connections.add(position, c);
		}
		inUse.add(connections.indexOf(c));
		return c;
	}

	public synchronized void returnConnection(Connection connection) {
		int index = connections.indexOf(connection);
		inUse.remove(new Integer(index));
	}
}
