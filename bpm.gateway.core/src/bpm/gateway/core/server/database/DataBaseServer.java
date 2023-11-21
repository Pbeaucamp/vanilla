package bpm.gateway.core.server.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

/**
 * This class is an implementation to use Relation DataBase as Server
 * @author LCA
 *
 */
public class DataBaseServer extends GatewayObject implements Server {

	/*
	 * Physical fields
	 */
	private DataBaseConnection connection;
	private List<DataBaseConnection> alternateConnections = new ArrayList<DataBaseConnection>();
	
	private List<DataStream> dataStreams = new ArrayList<DataStream>();

	
	private HashMap<Object, DataBaseConnection> overridennConnections = new HashMap<Object, DataBaseConnection>();
	
	public void connect(DocumentGateway document) throws ServerException {
		if (connection == null || !connection.isSet()){
			throw new ServerException("The connection is not fully defined. Verify your connection informations.", this);
		}

		connection.connect(document);
	}

	public void disconnect() throws ServerException {
		if (connection == null || !connection.isOpened()){
			return;
		}

		try {
			connection.disconnect();
		} catch (JdbcException e) {
			throw new ServerException(e.getMessage() + ". Error while disconnect", e, this);
		}
	}

	public String getDescription() {
		return description;
	}

	public List<DataStream> getDataStream(){
		return dataStreams;
	}

	

	public String getName() {
		return name;
	}

	
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

	
	public void addConnection(IServerConnection connection){
		if (connection != null && connection instanceof DataBaseConnection && !alternateConnections.contains(connection)){
			alternateConnections.add((DataBaseConnection)connection);
			((DataBaseConnection)connection).setServer(this);
		}
	}
	
	public void removeConnection(IServerConnection connection){
		if (alternateConnections.remove(connection)){
			((DataBaseConnection)connection).setServer(null);
		}
		
		if (this.connection == connection){
			this.connection = null;
		}
		
	}
	
	public void setCurrentConnection(IServerConnection connection) throws ServerException{
		
		if (!(connection instanceof DataBaseConnection)){
			throw new ServerException("Wrong class for setting DataBase connection " + connection.getClass().getName(), this);
		}
		
		if (connection == null || !alternateConnections.contains(connection)){
			throw new ServerException("This connection is not a part of the Server", this);
		}
		
		if (this.connection != null && this.connection.isOpened()){
			this.connection.disconnect();
		}
		
		this.connection = (DataBaseConnection)connection;
		
	}
	/**
	 * 
	 * @param adapter object used to override connection in some case, if null, the default connection is 
	 * returned without any changes
	 * @return
	 */
	public IServerConnection getCurrentConnection(Object adapter){
		if (adapter == null){
			return connection;
		}
		IServerConnection con = overridennConnections.get(adapter);
		if (con == null){
			return connection;
		}
		return con;
	}
	

	

	public String getType() {
		return Server.DATABASE_TYPE;
	}

	public List<IServerConnection> getConnections() {
		return (List<IServerConnection>)((List)alternateConnections);
	}

	public Element getElement() {
		Element el = DocumentHelper.createElement("dataBaseServer");
		for(IServerConnection c : alternateConnections){
			el.add(c.getElement());
		}
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		return el;
	}

	public void addOverridenConnection(Object adapter, IServerConnection connection){
		overridennConnections.put(adapter, (DataBaseConnection)connection);
	}
	
	public void removeOverridenConnection(Object adapter){
		overridennConnections.remove(adapter);
	}
}
