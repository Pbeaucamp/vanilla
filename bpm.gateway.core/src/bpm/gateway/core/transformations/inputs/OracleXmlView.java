package bpm.gateway.core.transformations.inputs;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.OracleXmlViewHelper;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunOracleXmlViewInput;
import bpm.vanilla.platform.core.IRepositoryContext;

import com.thoughtworks.xstream.XStream;

public class OracleXmlView extends AbstractTransformation implements DataStream {

	protected HashMap<Transformation, DefaultStreamDescriptor> descriptors = new LinkedHashMap<Transformation, DefaultStreamDescriptor>();

	private DataBaseServer server;
	private DefinitionXSD rootElement;
	
	private String definition;

	@Override
	public void initDescriptor() {
		try {
			OracleXmlViewHelper.createStreamDescriptor(this, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.initDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("oracleXmlView");
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

		e.addElement("rootElement").setText(getRootElementXML());
		
		try {
			if (getDescriptor(null) != null) {
				e.add(getDescriptor(null).getElement());
			}
		} catch (ServerException e1) {
			e1.printStackTrace();
		}
		return e;
	}

	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunOracleXmlViewInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
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
			for (StreamElement st : desc.getStreamElements()) {
				descriptor.addColumn(st);
			}
		}
		return descriptor;
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
		this.definition = definition;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDescriptor(HashMap<Transformation, DefaultStreamDescriptor> descriptors) {
		this.descriptors = descriptors;
		fireChangedProperty();
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


	public Transformation copy() {
		FileInputXML copy = new FileInputXML();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		
		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Table Name : " + getDefinition() + "\n");

		return buf.toString();
	}

}
