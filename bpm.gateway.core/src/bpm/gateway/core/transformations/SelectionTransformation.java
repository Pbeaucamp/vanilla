package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunSelection;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * This class allow to select only only few columns from the Inputs
 * @author LCA
 *
 */
public class SelectionTransformation extends AbstractTransformation implements Transformation {

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private List<Integer> buffered = new ArrayList<Integer>();
	private List<String> bufferedNames = new ArrayList<String>();
	
	protected HashMap<Transformation, List<String>> elementsToReturnNames = new HashMap<Transformation, List<String>>();
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Selection Transformations can only have one Input");
		}
		boolean result = super.addInput(stream); 
		
		if (result == false){
			return result;
		}
		
		elementsToReturnNames.put(stream, new ArrayList<String>());
		
		if(stream.getDescriptor(this) != null && stream.getDescriptor(this).getStreamElements() != null){
			for(int i=0; i<stream.getDescriptor(this).getStreamElements().size(); i++){
				activeStreamElement(i);
			}
		}
			
		return result;
	}

	
	public boolean isOutputed(StreamElement st){	
		for(List<String> l : elementsToReturnNames.values()){
			for(String input : l){
				if(input.equals(st.getFullName())){
					return true;
				}
			}
		}
		
		return false;
	}
	
		
	
	/**
	 * 
	 * This method add the given number column from the specified input
	 * to be pushed in the outputs but perform a resaerch on the inputs to
	 * find the right input 
	 * @param input
	 * @param streamElementNumber
	 */
	public void activeStreamElement(int streamElementNumber){
		StreamElement element = null;
		try {
			element = inputs.get(0).getDescriptor(this).getStreamElements().get(streamElementNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String inputName = getTransfoNameForPosition(streamElementNumber);
		
		if(element != null){	
			for(Transformation t : inputs){
//				if (t.getName().equals(inputName)){
					activeStreamElement(t, streamElementNumber, element.getFullName());
//					return;
//				}
			}
		}
	}
	
	
	private final String getTransfoNameForPosition(int streamElementNumber){
		List<StreamElement> l = new ArrayList<StreamElement>();
		
		for(Transformation t : inputs){
			try {
				l.addAll(t.getDescriptor(this).getStreamElements());
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		
		return l.get(streamElementNumber).transfoName;
	}
	
	public void desactiveStreamElement(int streamElementNumber){
		StreamElement element = null;
		try {
			element = inputs.get(0).getDescriptor(this).getStreamElements().get(streamElementNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String inputName = getTransfoNameForPosition(streamElementNumber);
		
		if(element != null){
			for(Transformation t : inputs){
				if (t.getName().equals(inputName)){
					desactiveStreamElement(t, element.getFullName());
					return;
				}
			}
		}
	}
	
	/**
	 * This method add the given number column from the specified input
	 * to be pushed in the outputs 
	 * @param input
	 * @param streamElementNumber
	 */
	public void activeStreamElement(Transformation input, int streamElementNumber, String columnFullName){
		if (!inputs.contains(input)){
			return;
		}
		
		if (elementsToReturnNames.get(input) == null){
			elementsToReturnNames.put(input, new ArrayList<String>());
		}
		
		/*
		 * ensure to not add an already added element
		 */
		boolean alreadyPresent = false;
		for(String col : elementsToReturnNames.get(input)){
			if (col.equals(columnFullName)){
				alreadyPresent = true;
				break;
			}
		}
		
		/*
		 * add the element and update the descriptor
		 */
		if (!alreadyPresent){

			//for loading model from digester
			if (buffered != null && !buffered.isEmpty()){
				for(Integer i : buffered){
					if (i.equals(streamElementNumber)){
						buffered.remove(i);
						elementsToReturnNames.get(input).add(columnFullName);
						break;
					}
				}
				
				if (buffered.isEmpty()){
					buffered = null;
				}
			}
			else if (bufferedNames != null && !bufferedNames.isEmpty()){
				for(String col : bufferedNames){
					if(col.equals(columnFullName)){
						bufferedNames.remove(col);
						elementsToReturnNames.get(input).add(col);
						break;
					}
				}
				
				if(bufferedNames.isEmpty()){
					bufferedNames = null;
				}
			}
			else{
				elementsToReturnNames.get(input).add(columnFullName);
				fireChangedProperty();
			}
			
			refreshDescriptor();
			
		}
		
	}
	
	public boolean columnAreChecked() {
		if(elementsToReturnNames != null){
			for(Transformation transfo : elementsToReturnNames.keySet()){
				return elementsToReturnNames.get(transfo) != null && !elementsToReturnNames.get(transfo).isEmpty();
			}
		}
		
		return false;
	}
	
	/**
	 * This method remove the given number column from the specified input
	 * to be pushed in the outputs 
	 * @param input
	 * @param streamElementNumber
	 */
	public void desactiveStreamElement(Transformation input, String columnFullName){
		if (!isInited()){
			return;
		}
		if (elementsToReturnNames.get(input) == null){
			return;
		}
		
		int index = -1;
		for(int i = 0; i < elementsToReturnNames.get(input).size(); i++){
			if (columnFullName.equals(elementsToReturnNames.get(input).get(i))){
				index = i;
				break;
			}
		}
		
		
		if (index > -1 ){
			elementsToReturnNames.get(input).remove(index);
			
			int counter = -1;
			for(int i = 0 ; i < descriptor.getColumnCount(); i++){
				if (descriptor.getTransformationName(i).equals(input.getName())){
					counter++;
					if (counter == index){
						break;
					}
				}
			}

			refreshDescriptor();
			fireChangedProperty();
		}
	}
	
	/**
	 * Do not use, for digester with old models
	 * @param index
	 */
	public void initOutputed(String index){
		if(buffered == null){
			buffered = new ArrayList<Integer>();
		}
		
		try{
			buffered.add(Integer.parseInt(index));
		}catch(NumberFormatException e){
			
		}
	}
	
	/**
	 * Do not use, for digester only
	 * @param columnName
	 */
	public void initOutputedNames(String columnName){
		if(bufferedNames == null){
			bufferedNames = new ArrayList<String>();
		}
	
		bufferedNames.add(columnName);
	}
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("selectionTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
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

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSelection(this, bufferSize);
	}
	
	@Override
	public void removeInput(Transformation transfo){
		super.removeInput(transfo);
		elementsToReturnNames.remove(transfo);
		
		refreshDescriptor();	
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}
	
	public List<Integer> getOutputedFor(Transformation inputStream){
		List<Integer> indexes = new ArrayList<Integer>();
		for(String columnName : elementsToReturnNames.get(inputStream)){
			try {
				for(int i=0; i<inputs.get(0).getDescriptor(this).getStreamElements().size(); i++){
					if(inputs.get(0).getDescriptor(this).getStreamElements().get(i).getFullName().equals(columnName)){
						indexes.add(i);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return indexes;
	}
	
	@Override
	public void initDescriptor(){
		try {
			if (!getInputs().isEmpty()){
				for(StreamElement element : getInputs().get(0).getDescriptor(this).getStreamElements()){
					
					if ((buffered != null && !buffered.isEmpty()) || (bufferedNames != null && !bufferedNames.isEmpty())){
						activeStreamElement(getInputs().get(0), 
								getInputs().get(0).getDescriptor(this).getStreamElements().indexOf(element), 
								element.getFullName());
					}
				}
				buffered = null;
				bufferedNames = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		setInited();
		refreshDescriptor();
		
	}
	@Override
	public void refreshDescriptor() {
		/*
		 * we rebuild entirely the descriptor
		 */
		if (!isInited()){
			return;
		}
		descriptor = new DefaultStreamDescriptor();
		
		for(Transformation t : inputs){
			for(String columnName : elementsToReturnNames.get(t)){
				
				StreamElement element = null;
				try {
					for(StreamElement el : inputs.get(0).getDescriptor(this).getStreamElements()){
						if(el.getFullName().equals(columnName)){
							element = el;
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(element != null){
					descriptor.addColumn(element.clone(getName(), t.getName()));
				}
			}
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public Transformation copy() {
		SelectionTransformation copy = new SelectionTransformation();
		
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
}
