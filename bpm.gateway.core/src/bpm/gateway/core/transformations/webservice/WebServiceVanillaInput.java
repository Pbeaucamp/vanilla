package bpm.gateway.core.transformations.webservice;

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
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunWebServiceVanillaInput;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.service.ServiceConstants;

public class WebServiceVanillaInput extends AbstractTransformation implements DataStream {

	private Server server = FileSystemServer.getInstance();
	
	private String filePath;
	private int webServiceDefinitionId;
	
	private DefaultStreamDescriptor descriptor;

	public WebServiceVanillaInput() {
		addPropertyChangeListener(this);
	}

	@Override
	public void initDescriptor() {
		try {
			descriptor = (DefaultStreamDescriptor) getDocument().getWebServiceVanillaHelper().getDescriptor(this);
			setDefinition("{$P_" + ServiceConstants.PARAMETER_FILE + "}");
			if(getDocument().getParameter(ServiceConstants.PARAMETER_FILE) == null){
				Parameter param = new Parameter();
				param.setName(ServiceConstants.PARAMETER_FILE);
				param.setType(0);
				getDocument().addParameter(param);
			}
			setInited();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("webServiceVanillaInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("webServiceDefinitionId").setText(String.valueOf(webServiceDefinitionId));
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if(filePath != null){
			e.addElement("definition").setText(filePath);
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunWebServiceVanillaInput(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

	}

	public Transformation copy() {
		WebServiceVanillaInput copy = new WebServiceVanillaInput();
		copy.setName("copy of " + name);
		copy.setDescription(description);
		copy.setDefinition(filePath);
		copy.setWebServiceDefinitionId(webServiceDefinitionId);
		copy.setServer(server);

		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Name : " + name + "\n");
		buf.append("File Path : " + filePath + "\n");
		buf.append("Web Service Definition ID : " + webServiceDefinitionId + "\n");

		return buf.toString();
	}

	public void setWebServiceDefinitionId(int webServiceDefinitionId) {
		this.webServiceDefinitionId = webServiceDefinitionId;
		try {
			descriptor = (DefaultStreamDescriptor) getDocument().getWebServiceVanillaHelper().getDescriptor(this);
			fireProperty(PROPERTY_INPUT_CHANGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setWebServiceDefinitionId(String webServiceDefinitionId) {
		this.webServiceDefinitionId = Integer.parseInt(webServiceDefinitionId);
	}

	public int getWebServiceDefinitionId() {
		return webServiceDefinitionId;
	}

	public Server getServer() {
		if (server == null){
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setServer(Server s) {
		this.server = s;
	}

	public void setServer(String serverName) {
		this.server = ResourceManager.getInstance().getServer(serverName);
	}

	@Override
	public String getDefinition() {
		return filePath;
	}

	@Override
	public void setDefinition(String definition) {
		this.filePath = definition;
	}
}
