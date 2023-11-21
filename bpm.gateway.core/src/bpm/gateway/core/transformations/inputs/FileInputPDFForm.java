package bpm.gateway.core.transformations.inputs;

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
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputPDFForm extends AbstractTransformation implements DataStream {

	private StreamDescriptor descriptor = new DefaultStreamDescriptor();

	private Server server;
	private String definition = "";
	private String propertiesFilePath;

	public FileInputPDFForm() {
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputPDFForm");
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

		if (getPropertiesFilePath() != null) {
			e.addElement("propertiesFilePath").setText(getPropertiesFilePath());
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		// return new RunKmlInput(this, bufferSize);
		return null;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		if (definition != null && !definition.isEmpty()) {
			try {
				// String file =
				// ((AbstractFileServer)this.getServer()).getFileName(this);
				// descriptor = KMLHelper.getKmlDescriptor(getName(), file);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Transformation t : outputs) {
				t.refreshDescriptor();
			}
		}

	}

	public Transformation copy() {
		return null;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getPropertiesFilePath() {
		return propertiesFilePath;
	}
	
	public void setPropertiesFilePath(String propertiesFilePath) {
		this.propertiesFilePath = propertiesFilePath;
	}
	
	public Server getServer() {
		if (server == null) {
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setServer(Server s) {
		if (s instanceof FileSystemServer) {
			this.server = s;
		}
	}

	public void setServer(String serverName) {
		this.server = ResourceManager.getInstance().getServer(serverName);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");

		return buf.toString();
	}
}
