package bpm.gateway.core.tsbn;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.tsbn.RunConnectorReferentiel;
import bpm.vanilla.platform.core.IRepositoryContext;

public class ConnectorReferentielXML extends AbstractTransformation implements DataStream {

	private DataBaseServer server;
	private Server fileServer = FileSystemServer.getInstance();
	
	private String filePath;
	private String encoding = "UTF-8";

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return new DefaultStreamDescriptor();
	}

	public String getDefinition() {
		return filePath;
	}

	public void setServer(Server server) {
		this.server = (DataBaseServer) server;
	}

	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}

	public Server getServer() {
		return server;
	}

	public void setDefinition(String definition) {
		this.filePath = definition;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public void initDescriptor() {
		super.initDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("connectorReferentielXML");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());
		
		return e;
	}

	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunConnectorReferentiel(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}
	}

	public Transformation copy() {
		ConnectorReferentielXML copy = new ConnectorReferentielXML();
		copy.setServer(server);
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setTemporaryFilename(getTemporaryFilename());
		copy.setTemporarySpliterChar(getTemporarySpliterChar());
		
		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("Encoding: " + getEncoding() + "\n");
		buf.append("SqlServer : \n" + server.getName() + "\n");

		return buf.toString();
	}

	public Server getFileServer() {
		return fileServer;
	}

}
