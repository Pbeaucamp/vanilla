package bpm.gateway.core.server.vanilla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;

public class VanillaServer extends GatewayObject implements Server, IServerConnection{

	private String url;
	private String login;
	private String password;

	
	private HashMap<Object, DataBaseConnection> overridennConnections = new HashMap<Object, DataBaseConnection>();
	public VanillaServer(){
		
	}
	
	public VanillaServer(String name, String description, String url, String login, String password){
		setName(name);
		setDescription(description);
		setUrl(url);
		setLogin(login);
		setPassword(password);
		
		
	}
	public void addOverridenConnection(Object adapter, IServerConnection connection){
		overridennConnections.put(adapter, (DataBaseConnection)connection);
	}
	
	public void removeOverridenConnection(Object adapter){
		overridennConnections.remove(adapter);
	}
	
	/**
	 * do nothing
	 */
	public void addConnection(IServerConnection connection) {}

	public void connect() throws ServerException {
		
		
	}

	public void disconnect() {
		
		
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

	public Element getElement() {
		Element el = DocumentHelper.createElement("vanillaServer");
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		
		Element con = el.addElement("vanillaConnection");
		con.addElement("url").setText(getUrl());
		con.addElement("login").setText(getLogin());
		con.addElement("password").setText(getPassword());
		
		
		
		return el;
	}

	public String getType() {
		return Server.VANILLA_SECURITY_SERVER;
	}

	/**
	 * do nothing
	 */
	public void removeConnection(IServerConnection sock) {}

	/**
	 * do nothing
	 */
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
		return true;
	}

	public boolean isSet() {
		return url != null && !"".equals(url.trim())  && login!=null && !("".equals(login));
	}

	/**
	 * @return the host
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param host the host to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
	
		buf.append("Url : " + getUrl() + "\n");
		
		
		buf.append("Login : " + getLogin() + "\n");
		buf.append("Password : " + getPassword().replaceAll(".", "*") + "\n");
		return buf.toString();
	}

}
