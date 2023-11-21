package bpm.gateway.core.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.tools.StringParser;


/**
 * This class is an implementation to use fileSystem as a server
 * not really needed but this aloow to dont bypass the model
 * @author LCA
 *
 */
public class FileSystemServer extends AbstractFileServer implements IServerConnection{





	private static FileSystemServer instance;
	
	public static FileSystemServer getInstance(){
		if (instance == null){
			instance = new FileSystemServer();
			instance.setName("File System");
		}
		
		return instance;
	}
	
	protected FileSystemServer(){}
	
	public void addConnection(IServerConnection connection) {
	}

	@Override
	public void connect(DocumentGateway document) throws ServerException {

	}

	public void disconnect(){
	}

	public List<IServerConnection> getConnections() {
		List<IServerConnection> l = new ArrayList<IServerConnection>();
		l.add(this);
		return l;
	}

	public IServerConnection getCurrentConnection(Object adapter) {
		return this;
	}

	public String getDescription() {
		return description;
	}

	public List<DataStream> getDataStream(){
		
		return null;
	}

	

	public String getName() {
		return name;
	}



	public String getType() {
		return Server.FILE_TYPE;
	}

	public void removeConnection(IServerConnection sock) {
		

	}

	public void setCurrentConnection(IServerConnection socket)
			throws ServerException {
	

	}

	@Override
	public boolean testConnection(DocumentGateway document) {
		return true;
	}

	public Element getElement() {
		Element el = DocumentHelper.createElement("fileSystemServer");
		el.addElement("name").setText(getName());
		return el;
	}
	
	public void addDataStream(DataStream stream) { }

	

	public void removeDataStream(DataStream stream) { }

	public Server getServer() {
		return this;
	}

	public boolean isOpened() {
		return true;
	}

	public boolean isSet() {
		return true;
	}

	

	@Override
	public InputStream getInpuStream(DataStream stream) throws Exception{
		String fname = getFileName(stream);
		if (!new File(fname).exists()){
			throw new Exception("The specified file name does not exist.");
		}
		return new FileInputStream(fname);
	}
	
	public String getFileName(DataStream stream) throws Exception{
		String fname = null;
		try{
			if (stream.getDocument() == null){
				StringParser parser = new StringParser(null);
				fname = parser.getValue( stream.getDocument(), stream.getDefinition());
			}
			else{
				fname = stream.getDocument().getStringParser().getValue( stream.getDocument(), stream.getDefinition());
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Wrong file name.");
			
		}
		if (fname == null){
			throw new Exception("No specified file name");
		}
		return fname;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

	
	@Override
	public void addOverridenConnection(Object adapter,
			IServerConnection connection) {
		
		
	}

	@Override
	public void removeOverridenConnection(Object adapter) {
		
		
	}
}
