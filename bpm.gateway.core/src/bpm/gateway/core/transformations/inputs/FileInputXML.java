package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunXmlInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputXML extends FileXML {
	
	private String privateKeyPath;
	private String password;
	
	private String defaultAttributeName;

	@Override
	public void initDescriptor() {
		try {
			FileXMLHelper.createStreamDescriptor(this, 100);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.initDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputXML");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		e.addElement("fromUrl").setText(String.valueOf(fromUrl));

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}

		e.addElement("rootTag").setText(getRootTag());
		e.addElement("rowTag").setText(getRowTag());
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());
		e.addElement("encrypting").setText(isEncrypting() + "");
		e.addElement("privateKeyPath").setText(getPrivateKeyPath() != null ? getPrivateKeyPath() : "");
		e.addElement("password").setText(getPassword() != null ? getPassword() : "");

		e.addElement("fromXSD").setText(isFromXSD() + "");
		e.addElement("xsdFilePath").setText(getXsdFilePath());
		e.addElement("rootElement").setText(getRootElementXML());
		e.addElement("defaultAttributeName").setText(getDefaultAttributeName());
		
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
		return new RunXmlInput(this, bufferSize);
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

	public Transformation copy() {
		FileInputXML copy = new FileInputXML();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setRootTag(getRootTag());
		copy.setRowTag(getRowTag());
		copy.setEncrypting(isEncrypting());
		copy.setPrivateKeyPath(getPrivateKeyPath());
		copy.setPassword(getPassword());
		copy.setFromUrl(isFromUrl());
		copy.setEncoding(getEncoding());
		copy.setEncrypting(isEncrypting());
		copy.setFromXSD(isFromXSD());
		copy.setXsdFilePath(getXsdFilePath());
		copy.setDefaultAttributeName(getDefaultAttributeName());
		
		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("RootTag : " + getRootTag() + "\n");
		buf.append("RowTag : " + getRowTag() + "\n");
		buf.append("Encoding: " + getEncoding() + "\n");
		buf.append("Encrypting: " + isEncrypting() + "\n");
		buf.append("Private Key Path: " + getPrivateKeyPath() != null ? getPrivateKeyPath() : "" + "\n");
		buf.append("From Url: " + fromUrl + "\n");
		buf.append("Default Attribute Name: " + getDefaultAttributeName() + "\n");

		return buf.toString();
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setFromUrl(boolean fromUrl) {
		this.fromUrl = fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = Boolean.parseBoolean(fromUrl);
	}

	public boolean isFromUrl() {
		return fromUrl;
	}
	
	public String getDefaultAttributeName() {
		return defaultAttributeName;
	}
	
	public void setDefaultAttributeName(String defaultAttributeName) {
		this.defaultAttributeName = defaultAttributeName;
	}
}
