package bpm.gateway.core.server.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;

public class GatewayServer implements Server, IServerConnection{
	
	
	
	private String url;
	private String login;
	private String password;
//	private GatewayServerConnection connection; 
	
	private String description;
	private String name;
	
	private HashMap<Object, DataBaseConnection> overridennConnections = new HashMap<Object, DataBaseConnection>();
	
	public void addOverridenConnection(Object adapter, IServerConnection connection){
		overridennConnections.put(adapter, (DataBaseConnection)connection);
	}
	
	public void removeOverridenConnection(Object adapter){
		overridennConnections.remove(adapter);
	}
	
	public void addConnection(IServerConnection connection) {
	}
	
	public GatewayServer(){}
	
	public GatewayServer(String name, String description, String url, String login, String password){
		setName(name);
		setDescription(description);
		setUrl(url);
		setLogin(login);
		setPassword(password);

	}
	
	public void connect() throws ServerException {
		//TODO
		
		
		
	}
	
	public void disconnect() {
		//TODO
		
	}
	
	public List<IServerConnection> getConnections() {
		List<IServerConnection> l = new ArrayList<IServerConnection>();
		l.add(this);
		return l;
	}
	
	public IServerConnection getCurrentConnection(Object adapter) {
		if (overridennConnections.get(adapter) == null){
			return this;
		}
		return overridennConnections.get(adapter);
	}
	
	public String getDescription() {
		return description;
	}
	
	public Element getElement() {
		Element el = DocumentHelper.createElement("gatewayServer");
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		
		Element con = el.addElement("gatewayConnection");
		con.addElement("url").setText(getUrl());
		con.addElement("login").setText(getLogin());
		con.addElement("password").setText(getPassword());
		
		
		
		return el;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return Server.GATEWAY_TYPE;
	}
	
	public void removeConnection(IServerConnection sock) {
		
		
	}
	
	public void setCurrentConnection(IServerConnection socket)
			throws ServerException {
		
		
	}
	
	public boolean testConnection() {
		return false;
	}
	
	public Server getServer() {
		return this;
	}
	
	public boolean isOpened() {
		
		return false;
	}
	
	public boolean isSet() {
		
		return true;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final String getLogin() {
		return login;
	}

	public final void setLogin(String login) {
		this.login = login;
	}

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setName(String name) {
		this.name = name;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
	
		buf.append("Url : " + getUrl() + "\n");
		
		
		buf.append("Login : " + getLogin() + "\n");
		buf.append("Password : " + getPassword().replaceAll(".", "*") + "\n");
		return buf.toString();
	}

	
}
