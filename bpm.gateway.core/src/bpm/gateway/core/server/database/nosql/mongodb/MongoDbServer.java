package bpm.gateway.core.server.database.nosql.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;

public class MongoDbServer extends GatewayObject implements Server{

	private MongoDbConnection connection;
	
	@Override
	public void addConnection(IServerConnection connection) {		
		this.connection = (MongoDbConnection) connection;
		this.connection.setServer(this);
	}

	@Override
	public void addOverridenConnection(Object adapter,
			IServerConnection connection) {	
		
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
	public Element getElement() {
		
		Element el = DocumentHelper.createElement("mongoDbServer");
		el.add(connection.getElement());
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		return el;
	}

	@Override
	public String getType() {
		return Server.MONGODB_TYPE;
	}

	@Override
	public void removeConnection(IServerConnection sock) {
		this.connection = null;
	}

	@Override
	public void removeOverridenConnection(Object adapter) {
		
	}

	@Override
	public void setCurrentConnection(IServerConnection socket)
			throws ServerException {
		this.connection = (MongoDbConnection) socket;
		this.connection.setServer(this);
	}

	@Override
	public boolean testConnection(DocumentGateway document) {
		boolean result = false;
		
		try{
			connect(document);
			result = true;
		}catch(ServerException e){
			result = false;
		}
		finally{
			try{
				disconnect();
			}catch(Exception e){
				
			}
		}
		return result;
	}

}
