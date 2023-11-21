package bpm.gateway.core.server.database.nosql.cassandra;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

public class CassandraConnection extends GatewayObject implements IServerConnection  {
	
	private String host;
	private String port;
	private String username;
	private String password;
	private String keyspace;
	
	private CassandraServer server;
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getKeyspace() {
		return keyspace;
	}
	
	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
	
	public Element getElement() {
		Element el = DocumentHelper.createElement("cassandraConnection");
		el.addElement("name").setText(getName());
		el.addElement("host").setText(getHost());
		el.addElement("port").setText(getPort());
		el.addElement("keyspace").setText(getKeyspace());	
		el.addElement("login").setText(getUsername());
		el.addElement("password").setText(getPassword());
		return el;
	}

	public void setServer(CassandraServer cassandraServer) {
		server = cassandraServer;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void connect(DocumentGateway document) throws ServerException { }

	@Override
	public void disconnect() throws JdbcException { }

	@Override
	public String getAutoDocumentationDetails() {
		return null;
	}

	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public boolean isSet() {
		return false;
	}
}
