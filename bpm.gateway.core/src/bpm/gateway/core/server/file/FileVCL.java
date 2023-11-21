package bpm.gateway.core.server.file;

import java.util.ArrayList;
import java.util.List;

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
 * base class for Files with variable column length
 * 
 * @author ludo
 * 
 */
public abstract class FileVCL extends AbstractTransformation implements DataStream {
	private Server server = FileSystemServer.getInstance();
	private String filePath;
	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	protected List<Integer> columnSize = new ArrayList<Integer>();

	private int numberOfRowToSkip = 0;
	private String encoding = "UTF-8";

	public String getDefinition() {
		return filePath;
	}

	public List<Integer> getColumnSizes() {
		return new ArrayList<Integer>(columnSize);
	}

	public Server getServer() {
		if (server == null) {
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setDefinition(String definition) {
		this.filePath = definition;

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

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public abstract Element getElement();
	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

	protected void setDescriptor(DefaultStreamDescriptor desc) {

		this.descriptor = desc;
		refreshDescriptor();
		this.fireChangedProperty();
	}

	@Override
	public void refreshDescriptor() {
		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}

	}

	protected void setColumnSizes(List<Integer> sizes) {
		columnSize = sizes;
	}

	/**
	 * @return the numberOfRowToSkip
	 */
	public int getNumberOfRowToSkip() {
		return numberOfRowToSkip;
	}

	/**
	 * @param numberOfRowToSkip
	 *            the numberOfRowToSkip to set
	 */
	public void setNumberOfRowToSkip(int numberOfRowToSkip) {
		this.numberOfRowToSkip = numberOfRowToSkip;
	}

	/**
	 * @param numberOfRowToSkip
	 *            the numberOfRowToSkip to set
	 */
	public void setNumberOfRowToSkip(String numberOfRowToSkip) {
		try {
			this.numberOfRowToSkip = Integer.parseInt(numberOfRowToSkip);
		} catch (Exception ex) {

		}
	}

}
