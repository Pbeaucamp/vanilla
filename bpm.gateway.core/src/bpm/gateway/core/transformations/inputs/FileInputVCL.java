package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileVCLHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunVclInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputVCL extends FileVCL implements Trashable{
	private boolean skipFirstRow = true;
	/**
	 * step used as trash to collect rows that have not been inserted for some reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;
	
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputVCL");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("skipFirstRow").setText(skipFirstRow+"");
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		
		if (getNumberOfRowToSkip() > 0){
			e.addElement("numberOfRowToSkip").setText(getNumberOfRowToSkip() + "");
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}
		return e;

	}



	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunVclInput(this, bufferSize);
	}


	@Override
	public void initDescriptor() {
		try{
			FileVCLHelper.createStreamDescriptor(this, 100);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		super.initDescriptor();
	}

	public Transformation copy() {
		FileInputVCL copy = new FileInputVCL();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setSkipFirstRow(skipFirstRow);
		

		return copy;
	}

	public boolean isSkipFirstRow() {
		return skipFirstRow;
	}

	public void setSkipFirstRow(boolean skipFirstRow) {
		this.skipFirstRow = skipFirstRow;
	}	
	public void setSkipFirstRow(String skipFirstRow) {
		this.skipFirstRow = Boolean.parseBoolean(skipFirstRow);
	}	
	
	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}


	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;
		
	}
	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	
	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		if (outputs.contains(stream)){
			if (trashName != null && trashName.equals(stream.getName())){
				setTrashTransformation(stream);
				trashName = null;
			}
		}
		
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition()+ "\n");
		buf.append("SkipFirstRow : " + isSkipFirstRow()+ "\n");
		buf.append("Encoding: " + getEncoding()+ "\n");
		if (getTrashTransformation() != null){
			buf.append("ErrorHandler: " + getTrashTransformation().getName()+ "\n");
		}
		return buf.toString();
	}
}
