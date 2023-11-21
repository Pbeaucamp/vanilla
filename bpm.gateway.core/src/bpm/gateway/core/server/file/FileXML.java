package bpm.gateway.core.server.file;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;

import com.thoughtworks.xstream.XStream;

public abstract class FileXML extends AbstractTransformation implements DataStream {

	// protected DefaultStreamDescriptor descriptor = new
	// DefaultStreamDescriptor();

	protected HashMap<Transformation, DefaultStreamDescriptor> descriptors = new LinkedHashMap<Transformation, DefaultStreamDescriptor>();

	private Server server = FileSystemServer.getInstance();
	private String filePath;
	private String fileUrl;
	private String workSheetName;
	private String encoding = "UTF-8";

	private String rootTag = "Root";
	private String rowTag = "Row";

	private boolean encrypting;

	private boolean fromXSD;
	private String xsdFilePath;
	private DefinitionXSD rootElement;

	protected boolean fromUrl = false;

	public String getRootTag() {
		return rootTag;
	}

	public void setRootTag(String rootTag) {
		this.rootTag = rootTag;
	}

	public String getRowTag() {
		return rowTag;
	}

	public void setRowTag(String rowTag) {
		this.rowTag = rowTag;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if (transfo == null || transfo == this) {
			if (descriptors.get(this) != null) {
				return descriptors.get(this);
			}
			else {
				return getAllDescriptors();
			}
		}
		else {
			return descriptors.get(transfo) != null ? descriptors.get(transfo) : getAllDescriptors();
		}
	}
	
	private StreamDescriptor getAllDescriptors() {
		DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
		for (StreamDescriptor desc : descriptors.values()) {
			for(StreamElement st : desc.getStreamElements()) {
				descriptor.addColumn(st);
			}
		}
		return descriptor;
	}

	public String getDefinition() {
		if(fromUrl) {
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
		if(fromUrl) {
			this.fileUrl = definition;
		}
		else {
			this.filePath = definition;
		}
	}

	public void setServer(Server s) {
		this.server = s;
	}

	protected void setDescriptor(HashMap<Transformation, DefaultStreamDescriptor> descriptors) {
		this.descriptors = descriptors;
		fireChangedProperty();
	}

	public String getWorkSheetName() {
		return workSheetName;
	}

	public void setWorkSheetName(String workSheetName) {
		this.workSheetName = workSheetName;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setEncrypting(boolean encrypting) {
		this.encrypting = encrypting;
	}

	public void setEncrypting(String encrypting) {
		this.encrypting = Boolean.parseBoolean(encrypting);
	}

	public boolean isEncrypting() {
		return encrypting;
	}

	public void setFromXSD(boolean fromXSD) {
		this.fromXSD = fromXSD;
	}

	public void setFromXSD(String fromXSD) {
		this.fromXSD = Boolean.parseBoolean(fromXSD);
	}

	public boolean isFromXSD() {
		return fromXSD;
	}

	public void setXsdFilePath(String xsdFilePath) {
		this.xsdFilePath = xsdFilePath;
	}

	public String getXsdFilePath() {
		return xsdFilePath != null ? xsdFilePath : "";
	}

	public DefinitionXSD getRootElement() {
		return rootElement;
	}

	public void setRootElement(DefinitionXSD rootElement) {
		this.rootElement = rootElement;
	}

	public void setRootElementFromDigester(String rootElement) {
		try {
			rootElement = rootElement.substring("<![CDATA[".length(), rootElement.length() - ("]]>".length()));
			XStream xstream = new XStream();
			this.rootElement = (DefinitionXSD) xstream.fromXML(rootElement);
		} catch (Exception e) {
		}
	}

	public String getRootElementXML() {
		if (rootElement == null) {
			return "";
		}
		XStream xstream = new XStream();
		return "<![CDATA[" + xstream.toXML(rootElement) + "]]>";
	}

	@Override
	public abstract void refreshDescriptor();

	public abstract Element getElement();

	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

}
