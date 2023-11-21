package bpm.gateway.core.transformations.outputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.ITransformationHolder;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.MultiSheetXLSOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MultiFolderXLS extends FileXLS implements ITransformationHolder{

	private boolean includeHeader = true;
	private boolean delete = true;
	private boolean append = false;
	
	
	private HashMap<String , Object> incomingMapping = new LinkedHashMap<String , Object>();
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	

	/**
	 * @return the includeHeader
	 */
	public boolean isIncludeHeader() {
		return includeHeader;
	}

	/**
	 * @param includeHeader the includeHeader to set
	 */
	public void setIncludeHeader(boolean includeHeader) {
		this.includeHeader = includeHeader;
	}
	public void setIncludeHeader(String includeHeader) {
		try{
			this.includeHeader = Boolean.parseBoolean(includeHeader);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public void setDelete(String delete) {
		try{
			this.delete = Boolean.parseBoolean(delete);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	

	
	
	
	@Override
	public void addOutput(Transformation stream) {
		
	}

	public void adaptInput(String inputName, Object adapter) throws Exception {
		if (adapter == null){
			incomingMapping.remove(inputName);
			
		}
		
		if (!(adapter instanceof String)){
			throw new Exception("Can only be adapted on String representing an XLS SheetName");
		}
		
		for(Transformation t : getInputs()){
			if (t.getName().equals(inputName)){
				incomingMapping.put((String)adapter, t);
				return;
			}
		}
		incomingMapping.put((String)adapter, inputName);
	}

	public boolean addInput(Transformation input){
		if (input != null && !getInputs().contains(input)){
			inputs.add(input);
			return true;
		}
		return false;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("multiOutputXLS");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("delete").setText("" + delete);
		e.addElement("sheetName").setText("" + getSheetName());
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		e.addElement("encoding").setText(getEncoding());
		e.addElement("includeHeader").setText(includeHeader + "");
		e.addElement("append").setText(append + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		for(String k : incomingMapping.keySet()){
			if (incomingMapping.get(k) != null){
				Element el = e.addElement("inputMapping");
				el.addElement("sheetName").setText(k);
				el.addElement("inputName").setText(getTransformation(k).getName());
			}
			
		}
		
		return e;
	}

	public List<Transformation> getInputs() {
		return new ArrayList<Transformation>(inputs);
	}

	public Transformation getTransformation(Object adapter) {
		if (incomingMapping.get(adapter) instanceof Transformation){
			return (Transformation)incomingMapping.get(adapter);
		}
		else{
			for(Transformation t : getInputs()){
				if (t.getName().equals(incomingMapping.get(adapter))){
					incomingMapping.put((String)adapter, t);
					return t;
				}
			}
		}
		return null;
	}

	public void removeInput(Transformation input) {
		inputs.remove(input);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return new DefaultStreamDescriptor();
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new MultiSheetXLSOutput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
	}

	public Transformation copy() {
		
		return null;
	}

	public String getAutoDocumentationDetails() {
		
		return null;
	}

	public String getSheetName(Transformation element) {
		
		for(String s : incomingMapping.keySet()){
			if (incomingMapping.get(s) instanceof Transformation && incomingMapping.get(s) == element){
				return s;
			}
			else if (incomingMapping.get(s) instanceof String){
				if (element.getName().equals(incomingMapping.get(s))){
					incomingMapping.put(s, element);
					return s;
				}
			}
		}
		return null;
	}

	public List<String> getSheetNames() {
		return new ArrayList<String>(incomingMapping.keySet());
	}

	public boolean isAppend() {
		return append;
	}
	
	public void setAppend(boolean value) {
		append = value;
	}
	
	public void setAppend(String value) {
		try{
			append = Boolean.parseBoolean(value);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
