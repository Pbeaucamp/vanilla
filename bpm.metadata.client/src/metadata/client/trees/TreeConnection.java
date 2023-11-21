package metadata.client.trees;

import bpm.metadata.layer.physical.IConnection;

public class TreeConnection extends TreeParent {

	private IConnection connection;
		
	
	public TreeConnection(IConnection connection) {
		super(connection.getName());
		this.connection = connection;
	}

	@Override
	public String toString() {
		return connection.getName();
	}
	
	public IConnection getConnection(){
		return connection;
	}

	@Override
	public Object getContainedModelObject() {
		return connection;
	}
	
}
