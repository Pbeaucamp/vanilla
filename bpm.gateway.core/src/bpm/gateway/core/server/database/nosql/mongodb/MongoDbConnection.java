package bpm.gateway.core.server.database.nosql.mongodb;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

public class MongoDbConnection extends GatewayObject implements IServerConnection{
	
	private MongoDbServer server;
	
	private String keyspace, host, port, login, password, yourDb;
	private boolean pass;
	
	
	public String getYourDb() {
		return yourDb;
	}

	public void setYourDb(String yourDb) {
		this.yourDb = yourDb;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPass() {
		return pass;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public String getKeyspace() {
		return keyspace;
	}
	
	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
	
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
	
	@Override
	public void connect(DocumentGateway document) throws ServerException {		
	}

	@Override
	public void disconnect() throws JdbcException {

	}

	@Override
	public String getAutoDocumentationDetails() {
		return null;
	}

	@Override
	public Element getElement() {
		Element el = DocumentHelper.createElement("mongoDbConnection");
		el.addElement("name").setText(getName());
		el.addElement("host").setText(getHost());
		el.addElement("port").setText(getPort());
		el.addElement("login").setText(getLogin());
		el.addElement("password").setText(getPassword());
		return el;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public boolean isSet() {
		return false;
	}

	public void setServer(MongoDbServer mongoDbServer) {
		server = mongoDbServer;
	}


}
