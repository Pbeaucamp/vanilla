package bpm.gateway.core.transformations.outputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunXMLOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileOutputXML extends FileXML {

	private boolean delete = false;
	private boolean includeType = false;
	
	private String publicKeyPath;

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileOutputXML");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("encoding").setText(getEncoding());
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("delete").setText("" + delete);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}

		e.addElement("rootTag").setText(getRootTag());
		e.addElement("rowTag").setText(getRowTag());
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encrypting").setText(isEncrypting() + "");
		e.addElement("publicKeyPath").setText(getPublicKeyPath() != null ? getPublicKeyPath() : "");

		e.addElement("fromXSD").setText(isFromXSD() + "");
		e.addElement("xsdFilePath").setText(getXsdFilePath());
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

	public boolean addInput(Transformation stream) throws Exception {
		boolean result = super.addInput(stream);
		if (result == false) {
			return result;
		}

		refreshDescriptor();

		return result;

	}

	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		refreshDescriptor();
	}

	public void removeOutput(Transformation transfo) {

	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

		for (Transformation t : inputs) {
			try {
				for (StreamElement e : t.getDescriptor(this).getStreamElements()) {
					descriptor.addColumn(getName(), e.tableName, e.name.replace(" ", "_"), e.type, e.className, t.getName(), e.isNullable, e.typeName, e.defaultValue, e.isPrimaryKey);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		
		descriptors.put(this, descriptor);

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}

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
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunXMLOutput(this, bufferSize);
	}

	public Transformation copy() {
		FileOutputXML copy = new FileOutputXML();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setRootTag(getRootTag());
		copy.setRootTag(getRowTag());
		copy.setEncrypting(isEncrypting());
		copy.setPublicKeyPath(getPublicKeyPath());
		return copy;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean selection) {
		this.delete = selection;
	}

	public void setDelete(String selection) {
		this.delete = Boolean.parseBoolean(selection);
	}

	public boolean getIncludeType() {
		return includeType;
	}

	public void setIncludeType(boolean selection) {
		this.includeType = selection;
	}

	public void setIncludeType(String selection) {
		this.includeType = Boolean.parseBoolean(selection);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");

		buf.append("Encoding : " + getEncoding() + "\n");
		buf.append("Delete First : " + getDelete() + "\n");
		buf.append("Root Tag : " + getRootTag() + "\n");
		buf.append("Row Tag : " + getRowTag() + "\n");
		buf.append("Encrypting: " + isEncrypting() + "\n");
		buf.append("Public Key Path: " + getPublicKeyPath() != null ? getPublicKeyPath() : "" + "\n");

		return buf.toString();
	}

	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}

	public String getPublicKeyPath() {
		return publicKeyPath;
	}
}
