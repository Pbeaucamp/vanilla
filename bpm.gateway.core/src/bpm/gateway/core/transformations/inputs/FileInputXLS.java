package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Server;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunXlsInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputXLS extends FileXLS{

	
	private boolean skipFirstRow = false;
	private int skipLines = 0;
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputXLS");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("sheetName").setText(getSheetName());
		e.addElement("skipFirstRow").setText(skipFirstRow + "");
		e.addElement("skipLines").setText(skipLines + "");
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		e.addElement("encoding").setText(getEncoding());
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new FileInputXLSRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunXlsInput(null, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public boolean getSkipFirstRow() {
		
		return skipFirstRow;
	}
	
	
	public void setSkipFirstRow(boolean skipFirstRow) {
		
		this.skipFirstRow = skipFirstRow;
	}
	
	
	public void setSkipFirstRow(String skipFirstRow) {
		
		this.skipFirstRow = Boolean.parseBoolean(skipFirstRow);
	}

	public void setServer(String serverName){
		for(Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)){
			if (s.getName().equals(serverName)){
				setServer(s);
				return;
			}
		}
		
	}
	
	@Override
	public void initDescriptor() {
		try{
			FileXLSHelper.createStreamDescriptor(this);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		super.initDescriptor();
	}
	
	public Transformation copy() {
		FileInputXLS copy = new FileInputXLS();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setSkipFirstRow(skipFirstRow);

		return copy;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition()+ "\n");
		buf.append("SheetName : " + getSheetName()+ "\n");
		buf.append("SkipFirstRow : " + getSkipFirstRow()+ "\n");
		buf.append("Encoding: " + getEncoding()+ "\n");
		
		return buf.toString();
	}

	public int getSkipLines() {
		return skipLines;
	}

	public void setSkipLines(int skipLines) {
		this.skipLines = skipLines;
	}
	
	public void setSkipLines(String skipLines) {
		this.skipLines = Integer.parseInt(skipLines);
	}
}

