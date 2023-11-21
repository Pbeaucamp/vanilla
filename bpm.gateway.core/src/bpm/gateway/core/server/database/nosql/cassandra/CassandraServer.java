package bpm.gateway.core.server.database.nosql.cassandra;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;

public class CassandraServer extends GatewayObject implements Server {

	private CassandraConnection connection;

	@Override
	public void addConnection(IServerConnection connection) {
		this.connection = (CassandraConnection) connection;
		this.connection.setServer(this);
	}

	@Override
	public void addOverridenConnection(Object adapter, IServerConnection connection) { }
	
	public Element getElement() {
		Element el = DocumentHelper.createElement("cassandraServer");
		el.add(connection.getElement());
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		return el;
	}

	@Override
	public void connect(DocumentGateway document) throws ServerException {
		connection.connect(document);
	}

	@Override
	public void disconnect() throws ServerException {
		connection.disconnect();
	}

	@Override
	public List<IServerConnection> getConnections() {
		List<IServerConnection> conns = new ArrayList<IServerConnection>();
		conns.add(connection);
		return conns;
	}

	@Override
	public IServerConnection getCurrentConnection(Object adapter) {
		return connection;
	}

	@Override
	public String getType() {
		return Server.CASSANDRA_TYPE;
	}

	@Override
	public void removeConnection(IServerConnection sock) {
		this.connection = null;
	}

	@Override
	public void removeOverridenConnection(Object adapter) {	}

	@Override
	public void setCurrentConnection(IServerConnection socket) throws ServerException {
		this.connection = (CassandraConnection) socket;
		this.connection.setServer(this);
	}
	
	@Override
	public boolean testConnection(DocumentGateway document) {
		Cluster myCluster = HFactory.getOrCreateCluster("TestConnection_" + new Object().hashCode(), 
				connection.getHost() + ":" + connection.getPort());

		KeyspaceDefinition kspDef = myCluster.describeKeyspace(connection.getKeyspace());
		
		if(kspDef != null){
			return kspDef.getCfDefs() != null && !kspDef.getCfDefs().isEmpty();
		}
	
		return false;
	}
}
