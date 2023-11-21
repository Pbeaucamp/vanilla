package bpm.gateway.core.server.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;

public class LdapServer extends GatewayObject implements Server {

	private List<LdapConnection> alternateConnections = new ArrayList<LdapConnection>();
	private LdapConnection connection = null;
	private HashMap<Object, DataBaseConnection> overridennConnections = new HashMap<Object, DataBaseConnection>();
	
	public void addConnection(IServerConnection connection) {
		if (connection instanceof LdapConnection){
			alternateConnections.add((LdapConnection)connection);
			((LdapConnection)connection).setServer(this);
		}
		
		
	}
	public void addOverridenConnection(Object adapter, IServerConnection connection){
		overridennConnections.put(adapter, (DataBaseConnection)connection);
	}
	
	public void removeOverridenConnection(Object adapter){
		overridennConnections.remove(adapter);
	}

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

	public List<IServerConnection> getConnections() {
		return (List<IServerConnection>)((List)alternateConnections);

	}

	public IServerConnection getCurrentConnection(Object adapter) {
		if (overridennConnections.get(adapter) == null){
			return connection;
		}
		return overridennConnections.get(adapter);
	}

	public Element getElement() {
		Element el = DocumentHelper.createElement("ldapServer");
		for(IServerConnection c : alternateConnections){
			el.add(c.getElement());
		}
		
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		return el;
	}

	public String getType() {
		return Server.LDAP_TYPE;
	}

	public String getName() {
		return name;
	}
	
	public void removeConnection(IServerConnection sock) {
		if (alternateConnections.remove(connection)){
			((LdapConnection)connection).setServer(null);
		}
		
		if (this.connection == connection){
			this.connection = null;
		}
		
	}

	public void setCurrentConnection(IServerConnection connection)
			throws ServerException {
		if (!(connection instanceof LdapConnection)){
			throw new ServerException("Wrong class for setting DataBase connection " + connection.getClass().getName(), this);
		}
		
		if (connection == null || !alternateConnections.contains(connection)){
			throw new ServerException("This connection is not a part of the Server", this);
		}
		
		if (this.connection != null && this.connection.isOpened()){
//			throw new ConnectionException("You cannot change of connection while the current one is not closed.", this);
			
			this.connection.disconnect();
		}
		
		this.connection = (LdapConnection)connection;

		
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
