package bpm.gateway.core.transformations;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunEncrypt;
import bpm.vanilla.platform.core.IRepositoryContext;

public class EncryptTransformation extends SelectionTransformation {

	private String publicKey;
	private boolean encrypt = true;

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPublicKey() {
		return publicKey;
	}
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("anonymeTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		e.addElement("publicKey").setText(publicKey);
		e.addElement("encrypt").setText(String.valueOf(encrypt));
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		for(List<String> columns : elementsToReturnNames.values()){
			for(String input : columns){
				e.addElement("selectedNames").setText(input);
			}
		}
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	public Transformation copy() {
		EncryptTransformation copy = new EncryptTransformation();
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		
		return copy;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Fields Outputed :\n");
		for(List<String> columns : elementsToReturnNames.values()){
			
			for(String input : columns){
				buf.append("\t- " + input + "\n");
			}
			
		}
		
		return buf.toString();
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunEncrypt(this, bufferSize);
	}
	
	public void setEncrypt(String encrypt) {
		if(encrypt != null && !encrypt.isEmpty()){
			this.encrypt = Boolean.parseBoolean(encrypt);
		}
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		descriptor = new DefaultStreamDescriptor();
		
		try {
			for(Transformation t : inputs){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}
}
