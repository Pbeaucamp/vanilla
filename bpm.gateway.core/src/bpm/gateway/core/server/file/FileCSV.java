package bpm.gateway.core.server.file;

import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * Abstract File for CSV Files
 * 
 * @author LCA
 * 
 */
public abstract class FileCSV extends AbstractTransformation implements DataStream {

	private char separator = ';';
	private Server server = FileSystemServer.getInstance();

	private String filePath;
	private String fileUrl;

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private String encoding = "UTF-8";

	protected boolean fromUrl = false;
	
	protected boolean isJson = false;
	protected String jsonRootItem = "";
	protected int jsonDepth = 1;

	public String getDefinition() {
		if (fromUrl) {
			return fileUrl;
		}
		else {
			return filePath;
		}
	}

	public Server getServer() {
		if (server == null) {
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setDefinition(String definition) {
		if (fromUrl) {
			this.fileUrl = definition;
		}
		else {
			this.filePath = definition;
		}
	}

	public void setServer(Server s) {
		this.server = s;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;

	}

	public void setServer(String serverName) {
		for (Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)) {
			if (s.getName().equals(serverName)) {
				setServer(s);
				return;
			}
		}

	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char c) {
		separator = c;
	}

	public void setSeparator(String s) {
		try {
			separator = s.charAt(0);
		} catch (IndexOutOfBoundsException e) { }
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public abstract Element getElement();

	// public abstract TransformationRuntime getExecutioner(RuntimeEngine
	// runtimeEngine);
	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

	protected void setDescriptor(DefaultStreamDescriptor desc) {
		this.descriptor = desc;
		
		//Deactivate this for now as it breaks the use of metadata from architect. To monitor if there is a drawback
//		refreshDescriptor();
//		this.fireChangedProperty();
	}
}
