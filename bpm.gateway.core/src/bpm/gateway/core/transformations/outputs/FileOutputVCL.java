package bpm.gateway.core.transformations.outputs;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunVCLOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileOutputVCL extends FileVCL implements Trashable{

	
	private boolean append = false;
	private boolean containHeaders = true; 
	private boolean delete = false;
	private boolean truncateField = false;
	/**
	 * step used as trash to collect rows that have not been inserted for some reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;
	
	public boolean addInput(Transformation stream) throws Exception {
				
		boolean result = super.addInput(stream); 
		if (result == false){
			return result;
		}
	
		
		
		StreamDescriptor desc = stream.getDescriptor(this);
		int pos = descriptor.getColumnCount();
		for(StreamElement e : desc.getStreamElements()){
			descriptor.addColumn(e.clone(getName(), stream.getName()));
			
			if (columnSize.size() <= descriptor.getColumnCount()){
				columnSize.add(30);
			}
			
		}
			
		return result;
		
		
	}

//	public void addOutput(Transformation stream) {
//	}

	
	public void setColumnSize(int columnIndex, int columnSize){
		this.columnSize.set(columnIndex, columnSize);
	}
	
	public void setColumnSize(String columnIndex, String columnSize){
		try{
			int pos = Integer.parseInt(columnIndex);
			int sz = Integer.parseInt(columnSize);
			
			while (this.columnSize.size() <= pos){
				this.columnSize.add(30);
			}
			setColumnSize(pos, sz);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("fileOutputVCL");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("delete").setText("" + delete);
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		e.addElement("encoding").setText(getEncoding());
		e.addElement("append").setText(append + "");
		e.addElement("truncateField").setText(truncateField + "");
		e.addElement("containsHeaders").setText(containHeaders + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}
		for(int i = 0; i < columnSize.size(); i++){
			Element cs = e.addElement("columnLength");
			cs.addElement("columnIndex").setText(i + "");
			cs.addElement("length").setText(columnSize.get(i) + "");
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new FileOutputCSVRuntime(this, runtimeEngine);
//	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunVCLOutput(this, bufferSize);
	}

	public void removeInput(Transformation transfo) {
		
		if (!inputs.contains(transfo)){
			return;
		}
		
	//we need to update the descriptor too
		
		int index = inputs.indexOf(transfo);
				
		int start = 0;
		
		for(int i = 0; i < index; i++){
			try {
				start += (inputs.get(i)).getDescriptor(this).getColumnCount();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		List<Integer> toDelete = new ArrayList<Integer>();
		
		try{
			for(int i = start; i < start + (transfo).getDescriptor(this).getColumnCount(); i++){
				toDelete.add(i);
				
			}
				
				
			int count = 0;
			for (int i = 0; i< toDelete.size(); i++){
				descriptor.removeColumn(i - count);
				try{
					columnSize.remove(i - count);
				}catch(Exception ex){
					
				}
				count ++;
			}
		}catch (ServerException e){
			e.printStackTrace();
		}
		
		super.removeInput(transfo);

	}

	
	

		
	

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		descriptor = new DefaultStreamDescriptor();
		
		for(Transformation t : inputs){
			try {
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
						descriptor.addColumn(getName(), e.tableName, e.name, e.type, e.className, t.getName(), 
								e.isNullable, e.typeName, e.defaultValue, e.isPrimaryKey);
				}
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			} catch (ServerException e) {
				
				e.printStackTrace();
			}
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public boolean isAppend() {
		return append;
	}

	public boolean isContainHeader() {
		return containHeaders;
	}

	
	public void setContainHeaders(boolean value){
		this.containHeaders = value;
	}
	
	public void setContainHeaders(String value){
		this.containHeaders = Boolean.parseBoolean(value);
	}
	
	public void setAppend(Boolean value) {
		append = value;
		
	}
	public void setAppend(String value) {
		append = Boolean.parseBoolean(value);
		
	}

	public void setServer(String serverName){
		for(Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)){
			if (s.getName().equals(serverName)){
				setServer(s);
				return;
			}
		}
		
	}
	
	public Transformation copy() {
		FileOutputVCL copy = new FileOutputVCL();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());

		return copy;
	}

	public boolean getDelete() {
		return delete;
	}	
	
	public void setDelete(boolean delete) {
		this.delete = delete;
	}	
	public void setDelete(String delete){
		this.delete = Boolean.parseBoolean(delete);
	}
	
	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;
		
	}
	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
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

	public boolean getTruncateField() {
		return truncateField;
	}
	
	public void setTruncateField(boolean truncate) {
		this.truncateField = truncate;
	}
	public void setTruncateField(String truncate) {
		try{
			this.truncateField = Boolean.parseBoolean(truncate);
		}catch(Exception ex){
			
		}
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition()+ "\n");
		buf.append("Write header : " + isContainHeader()+ "\n");
		buf.append("Encoding : " + getEncoding()+ "\n");
		buf.append("Append : " + isAppend() + "\n");
		buf.append("Delete First : " + getDelete() + "\n");
		buf.append("Truncate Field : " + getTruncateField() + "\n");
		
		
		return buf.toString();
	}
}
