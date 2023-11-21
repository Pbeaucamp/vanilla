package bpm.gateway.core.server.file;

import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * Abtstract Class for XLS Input/output files
 * 
 * @author LCA
 * 
 */
public abstract class FileXLS extends AbstractTransformation implements DataStream {

	private String sheetName = "sheet";
	private Server server = FileSystemServer.getInstance();
	private String filePath;
	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private String encoding = "UTF-8";

	public String getDefinition() {
		return filePath;
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

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public abstract Element getElement();
	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

	protected void setDescriptor(DefaultStreamDescriptor desc) {

		this.descriptor = desc;
	}

	public void setSheetName(String value) {
		this.sheetName = value;

	}

	public String getSheetName() {
		return sheetName;
	}

}
