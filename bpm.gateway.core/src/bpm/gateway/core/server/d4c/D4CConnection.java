package bpm.gateway.core.server.d4c;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

public class D4CConnection extends GatewayObject implements IServerConnection {
	
	private String url;
	private String org;
	
	private String login;
	private String password;
	
//	private String apiKey;
	
	private D4CServer server;

	@Override
	public Server getServer() {
		return server;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getOrg() {
		return org;
	}
	
	public void setOrg(String org) {
		this.org = org;
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

	public void setServer(D4CServer server) {
		this.server = server;
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
	public Element getElement() {
		Element el = DocumentHelper.createElement("d4cConnection");
		el.addElement("name").setText(getName());
		el.addElement("url").setText(getUrl());
		el.addElement("org").setText(getOrg());
//		el.addElement("apiKey").setText(getApiKey());
		el.addElement("login").setText(getLogin());
		el.addElement("password").setText(getPassword());
		return el;
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
