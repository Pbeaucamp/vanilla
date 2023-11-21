package bpm.profiling.ui.trees;

import bpm.profiling.runtime.core.Connection;


public class TreeConnection extends TreeParent {

	private Connection connection;
		
	
	public TreeConnection(Connection connection) {
		super(connection.getName());
		this.connection = connection;
	}

	@Override
	public String toString() {
		return connection.getName();
	}
	
	public Connection getConnection(){
		return connection;
	}

	
	
}
