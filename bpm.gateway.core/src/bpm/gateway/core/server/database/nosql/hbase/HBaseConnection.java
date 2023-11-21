package bpm.gateway.core.server.database.nosql.hbase;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

public class HBaseConnection extends GatewayObject implements IServerConnection {
	
	private String configurationFileUrl;
//	private String login;
//	private String password;
	
	private HBaseServer server;

	@Override
	public Server getServer() {
		return server;
	}

	public void setConfigurationFileUrl(String configFile) {
		this.configurationFileUrl = configFile;
	}

//	public void setLogin(String text) {
//		this.login = text;
//	}
//
//	public void setPassword(String text) {
//		this.password = text;
//	}

	public String getConfigurationFileUrl() {
		return configurationFileUrl;
	}

//	public String getLogin() {
//		return login;
//	}
//
//	public String getPassword() {
//		return password;
//	}

	public void setServer(HBaseServer hBaseServer) {
		server = hBaseServer;
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
		Element el = DocumentHelper.createElement("hbaseConnection");
		el.addElement("name").setText(getName());
		el.addElement("configurationFile").setText(getConfigurationFileUrl());	
//		el.addElement("login").setText(getLogin());
//		el.addElement("password").setText(getPassword());
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
