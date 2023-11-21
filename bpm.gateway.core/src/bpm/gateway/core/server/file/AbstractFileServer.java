package bpm.gateway.core.server.file;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;

public abstract class AbstractFileServer extends GatewayObject implements Server {

	public void addConnection(IServerConnection connection) {
		

	}

//	public void addDataStream(DataStream stream) {
//		
//
//	}

	public void connect() throws ServerException {
		

	}

	public void disconnect() throws ServerException {
		

	}

	public List<IServerConnection> getConnections() {
		
		return null;
	}

	public IServerConnection getCurrentConnection() {
		
		return null;
	}

//	public List<DataStream> getDataStream() {
//		
//		return null;
//	}

	public Element getElement() {
		
		return null;
	}

	public String getType() {
		return Server.FILE_TYPE;
	}

	public void removeConnection(IServerConnection sock) {
		

	}

	

	public void setCurrentConnection(IServerConnection socket)
			throws ServerException {
		

	}

	public boolean testConnection() {
		
		return false;
	}

	
	abstract public String getFileName(DataStream stream) throws Exception;	
	
	/**
	 * a Input from the DataStream Definition
	 * @param stream
	 * @return
	 */
	abstract public InputStream getInpuStream(DataStream stream)  throws Exception;

}
