package bpm.gateway.core.transformations;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.subtrans.RunSubTrans;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SubTransformation extends AbstractTransformation {
	
	
	private String definition; 
	private String finalStep;
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	/*
	 * keys are the model Parameter
	 * values are the StreamElements position in the InputStream
	 * only one InputStream allowed
	 *  
	 */
	private HashMap<String, Integer> mapping = new HashMap<String, Integer>();
	
	

	
	
	/**
	 * @return the finalStep
	 */
	public String getFinalStep() {
		return finalStep;
	}

	/**
	 * @param finalStep the finalStep to set
	 */
	public void setFinalStep(String finalStep) {
		this.finalStep = finalStep;
		refreshDescriptor();
	}
	
	public void setFinalStep(Transformation finalStep) {
		this.finalStep = finalStep.getName();
		descriptor = new DefaultStreamDescriptor();
		
		refreshDescriptor();

	}

	public String getDefinition(){
		return definition;
	}
	
	public void setDefinition(String definition){
		
		mapping.clear();
		this.definition = definition;
		super.listeners.firePropertyChange(new PropertyChangeEvent(this, "definition", null, this.definition));
		
//		refreshDescriptor();
	}
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("subTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("definition").setText(definition);
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (getFinalStep() != null){
			e.addElement("finalStep").setText(getFinalStep());
		}
		
		for(String s : mapping.keySet()){
			Element m = e.addElement("mapping");
			m.addElement("parmeterName").setText(s);
			m.addElement("fieldNumber").setText(mapping.get(s) + "");
		}
		
		return e;
	}

	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSubTrans(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {

		try {
			descriptor = getDocument().getSubTransformationHelper().getDescriptor(this);
			if (!isInited()){
				return;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		
		return null;
	}

	
	public void map(String parameterName, String streamElementPos){
		mapping.put(parameterName, Integer.parseInt(streamElementPos));
	}
	
	public void map(String parameterName, int streamElementPos){
		for(String s : mapping.keySet()){
			if (s.equals(parameterName)){
				mapping.put(s, streamElementPos);
				return;
			}
		}
		
		mapping.put(parameterName, streamElementPos);
	}
	
	
	public void unmap(String parameterName){
		for(String s : mapping.keySet()){
			if (s.equals(parameterName)){
				mapping.remove(s);
				return;
			}
		}
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The SubTransformations can only have one Input");
		}
		
		boolean b = super.addInput(stream);
		
		if(b){
			refreshDescriptor();
		}
		
		return b;
	}

	public Integer getMappingFor(String s) {
		for(String key : mapping.keySet()){
			if (key.equals(s)){
				return mapping.get(key);
			}
		}
		return null;
	}

	public String getMappingFor(Integer i) {
		for(String key : mapping.keySet()){
			if (i != null && i.equals(mapping.get(key))) {
				return key;
			}
		}
		return null;
	}

	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		mapping = new HashMap<String, Integer>();
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("Subtransformation directory Item id : " + getDefinition() + "\n");
		
		if (!mapping.isEmpty()){
			buf.append("Parameters mapping:\n");
			
			for(String s : mapping.keySet()){
				try{
					buf.append("\t- " + s + " provided by " + getInputs().get(0).getDescriptor(this).getColumnName(mapping.get(s)) + "\n");
				}catch(Exception ex){
					buf.append("\t- " + s + " provided by field number " + mapping.get(s) + "\n");
				}
			}
		}
		return buf.toString();
	}
}
