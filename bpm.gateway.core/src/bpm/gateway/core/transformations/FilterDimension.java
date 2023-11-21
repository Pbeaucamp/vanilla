package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunFilterDimension;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FilterDimension extends AbstractTransformation implements Trashable{
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private Transformation trashOutput;
	private String trashName;
	
	/*
	 * pos = Input field index
	 * value = Normalize field index
	 */
	private List<Integer> mappingIndex = new ArrayList<Integer>();
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().contains(stream)){
			return false;
		}
		if (getInputs().size() == 0){
			return super.addInput(stream);
		}
		else if (getInputs().size() == 1){
			if (getInputs().get(0) instanceof Normalize){
				if (!(stream instanceof Normalize)){
					boolean b = super.addInput(stream);
					refreshDescriptor();
					return b;
				}
				else{
					throw new Exception("Only one input can be a Normalize step");
				}
			}
			else{
				if (stream instanceof Normalize){
					boolean b =  super.addInput(stream);
					refreshDescriptor();					
					return b;
				}
				else{
					throw new Exception("One of the two inputs must be a Normlize step");
				}
			}
		}
		else{
			throw new Exception("Cannot have more than Two inputs for this Transformation");
		}
		
		
		
	}
	public void setMapping(String inputIndex, String normlizerIndex){
		try{
			setMapping(Integer.parseInt(inputIndex), Integer.parseInt(normlizerIndex));
		}catch(Exception ex){
			
		}
	}
	public void setMapping(int inputIndex, int normlizerIndex){
		while(mappingIndex.size() <= inputIndex){
			mappingIndex.add(null);
		}
		if (normlizerIndex >= 0){
			mappingIndex.set(inputIndex, normlizerIndex);
		}
		else{
			mappingIndex.set(inputIndex, null);
		}
	}
	
	public Integer getMapping(StreamElement inputField){
		int index = descriptor.getStreamElements().indexOf(inputField);
		
		
		if (index < mappingIndex.size()){
			return mappingIndex.get(index);
		}
		else{
			return null;
		}
		
	}
	
	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);	
		if (trashName != null && trashName.equals(stream.getName())){
			setTrashTransformation(stream);
			trashName = null;
		}
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("filterDimension");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		for(int i = 0; i < mappingIndex.size(); i++){
			if (mappingIndex.get(i) != null){
				Element m = e.addElement("mapping");
				m.addElement("pos").setText("" + i);
				m.addElement("inputIndex").setText("" + mappingIndex.get(i));
			}
		}
		
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(trashOutput.getName());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunFilterDimension(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()){
			return;
		}
		for(Transformation t : getInputs()){
			if (!(t instanceof Normalize)){
				try{
					for(StreamElement e : t.getDescriptor(this).getStreamElements()){
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			trashOutput = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashOutput;
	}

	public void setTrashTransformation(Transformation transfo) {
		trashOutput = transfo;
		
	}
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}

	public Transformation copy() {
		FilterDimension copy = new FilterDimension();
		copy.setName("Copy of " + getName());
		return copy;
	}
	
	public Normalize getDimensionValidatorInput(){
		for(Transformation t : getInputs()){
			if (t instanceof Normalize){
				return (Normalize)t;
			}
		}
		
		return null;
	}

	public List<Integer> getMappings() {
		return new ArrayList<Integer>(mappingIndex);
	}
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		if (getTrashTransformation() != null){
			buf.append("Trash Output : " + getTrashTransformation().getName() + "\n");
		}
		Transformation t = null;
		if (getInputs().get(0) == getDimensionValidatorInput()){
			t = getInputs().get(1); 
		}
		else{
			t = getInputs().get(0);
		}
		for(int i = 0; i < mappingIndex.size(); i++){
			if (mappingIndex.get(i) == null || mappingIndex.get(i) < 0){
				continue;
			}
			try{
				buf.append ("\t- " + t.getDescriptor(this).getColumnName(i) + " mapped on " + getDimensionValidatorInput().getDescriptor(this).getColumnName(mappingIndex.get(i)));	
			}catch(Exception ex){
				buf.append ("\t- " + i + " mapped on index " + mappingIndex.get(i));
			}
			
		}
		return buf.toString();
	}
}
