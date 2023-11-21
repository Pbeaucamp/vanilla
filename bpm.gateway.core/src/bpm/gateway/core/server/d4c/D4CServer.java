package bpm.gateway.core.server.d4c;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.utils.D4CHelper;

public class D4CServer extends FileSystemServer {

	private D4CConnection connection;

	@Override
	public void addConnection(IServerConnection connection) {
		this.connection = (D4CConnection) connection;
		this.connection.setServer(this);
	}

	@Override
	public void addOverridenConnection(Object adapter, IServerConnection connection) {
	}

	@Override
	public void connect(DocumentGateway document) throws ServerException {
		connection.connect(document);
	}

	@Override
	public void disconnect() {
		try {
			connection.disconnect();
		} catch (JdbcException e) {
			e.printStackTrace();
		}
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
		Element el = DocumentHelper.createElement("d4cServer");
		el.add(connection.getElement());
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());

		return el;
	}

	@Override
	public String getType() {
		return Server.D4C_TYPE;
	}

	@Override
	public void removeConnection(IServerConnection sock) {
		this.connection = null;
	}

	@Override
	public void removeOverridenConnection(Object adapter) {
	}

	@Override
	public void setCurrentConnection(IServerConnection socket) throws ServerException {
		this.connection = (D4CConnection) socket;
		this.connection.setServer(this);
	}

	@Override
	public boolean testConnection(DocumentGateway document) {
		boolean result = false;

		try {
			connect(document);
			result = true;
		} catch (ServerException e) {
			result = false;
		} finally {
			try {
				disconnect();
			} catch (Exception e) {

			}
		}
		return result;
	}

	@Override
	public String getFileName(DataStream stream) throws Exception {
		if (stream instanceof D4CInput) {
			String packageName = ((D4CInput) stream).getPackageName();
			String resourceId = ((D4CInput) stream).getResourceId();
			
			try {
				CkanResource resource = getResource(packageName, resourceId);
				return resource != null ? resource.getName() : "";
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public D4CHelper getD4CHelper() {
		D4CConnection connection = (D4CConnection) getCurrentConnection(null);
		return new D4CHelper(connection.getUrl(), connection.getOrg(), connection.getLogin(), connection.getPassword());
	}
	
	public String getOrg() {
		D4CConnection connection = (D4CConnection) getCurrentConnection(null);
		return connection.getOrg();
	}

	@Override
	public InputStream getInpuStream(DataStream stream) throws Exception {
		if (stream instanceof D4CInput) {
			CkanResource selectedResource = ((D4CInput) stream).getSelectedResource();
			
			try {
				D4CHelper d4cHelper = getD4CHelper();
				
				if (selectedResource == null) {
					String packageName = ((D4CInput) stream).getPackageName();
					String resourceId = ((D4CInput) stream).getResourceId();
					selectedResource = getResource(packageName, resourceId);
				}
				
				return d4cHelper.getResourceFile(selectedResource);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	private CkanResource getResource(String packageName, String resourceId) throws Exception {
		D4CHelper d4cHelper = getD4CHelper();
		CkanPackage pack = d4cHelper.findCkanPackage(packageName);
		
		if (pack.getResources() != null) {
			for (CkanResource resource : pack.getResources()) {
				if (resource.getId().equals(resourceId)) {
					return resource;
				}
			}
		}
		
		return null;
	}

}
