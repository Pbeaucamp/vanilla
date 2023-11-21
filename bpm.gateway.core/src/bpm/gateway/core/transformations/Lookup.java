package bpm.gateway.core.transformations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunLookup;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Lookup extends SimpleMappingTransformation implements Trashable{

	private boolean removeRowsWithoutLookupMatching = true;
	private boolean trashRowsWithoutLookupMatching = false;
	private Transformation trashTransformation;
	private String trashName;
	
	
	public Lookup(){
		
	}

	public boolean addInput(Transformation stream) throws Exception {
		boolean b = super.addInput(stream);
		
		if (!isMaster(stream)){

			
			DefaultStreamDescriptor thisDesc = (DefaultStreamDescriptor) getDescriptor(this);
			
			
			List<Integer> l = new ArrayList<Integer>();
			
			for(int i = 0; i < thisDesc.getColumnCount(); i++){
				boolean found = false;
				
				
				for(Transformation t : inputs){
					if (t != stream){
						
						for(StreamElement e : t.getDescriptor(this).getStreamElements()){
							if (e.equals(thisDesc.getStreamElements().get(i))){
								found = true;
								break;
							}
						}
						
					}
				}
				
				if (!found){
					l.add(i);
				}
			}
			int count = 0;
			for(Integer i : l){
				thisDesc.removeColumn(i - count++);
			}
			
			
			
		}
		refreshDescriptor();
		return b;
	}
	
	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		
		//TODO : update descriptor
		DefaultStreamDescriptor thisDesc;
		try {
			thisDesc = (DefaultStreamDescriptor) getDescriptor(this);
		} catch (ServerException e1) {
			e1.printStackTrace();
			return;
		}
		DefaultStreamDescriptor transfoDesc;
		try {
			transfoDesc = (DefaultStreamDescriptor) transfo.getDescriptor(this);
		} catch (ServerException e1) {
			e1.printStackTrace();
			return;
		}
		
		List<Integer> toRemove = new ArrayList<Integer>();
		for(StreamElement e : transfoDesc.getStreamElements()){
			int i = 0;
			for(StreamElement _e : thisDesc.getStreamElements()){
				if (_e.equals(e)){
					toRemove.add(i);
				}
				i++;
			}
			
			
		}
		int count = 0;
		for(Integer e : toRemove){
			
			thisDesc.removeColumn(e - count++);
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
	public Element getElement() {
		Element e = super.getElement();
		e.setName("lookup");
		
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(trashTransformation.getName());
		}
		
		e.addElement("removeRowsWithoutMatch").setText(removeRowsWithoutLookupMatching + "");
		e.addElement("trashRowsWithoutMatch").setText(trashRowsWithoutLookupMatching + "");
		
		
		return e;
	}



//	@Override
//	public void setAsMaster(Transformation t) {
//		super.setAsMaster(t);
//		refreshDescriptor();
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunLookup(this, bufferSize);
	}


	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		
		if (!isInited()){
			return;
		}
		
		try{
			Transformation master = null;
			Transformation lookup = null;
			
			for(Transformation t : getInputs()){
				if (isMaster(t)){
					master = t;
					
				}
				else{
					lookup = t;
				}
			}
			
			if (master == null && getInputs().size() == 2){
				if (getInputs().get(0) == lookup){
					master = getInputs().get(1); 
				}
				else{
					master = getInputs().get(0);
				}
			}
			
			
			if (master != null){
				for(StreamElement e : master.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), master.getName()));
				}
			}
			
			if (lookup != null){
				for(StreamElement e : lookup.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), lookup.getName()));
				}
			}
			
			if (bufferMapping != null){
				for(Point p : bufferMapping){
					createMapping(p.x, p.y);
				}
					
				bufferMapping = null;
			}
			else if(bufferMappingName != null){
				for(String input : bufferMappingName.keySet()){
					createMapping(input, bufferMappingName.get(input));
				}
				
				bufferMappingName = null;
			}
			
			for(Transformation  t : getOutputs()){
				t.refreshDescriptor();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Transformation copy() {
		Lookup copy = new Lookup();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			trashTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.trashTransformation = transfo;
		
	}
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}

	/**
	 * @return the removeRowsWithoutLookupMatching
	 */
	public final boolean isRemoveRowsWithoutLookupMatching() {
		return removeRowsWithoutLookupMatching;
	}

	/**
	 * @param removeRowsWithoutLookupMatching the removeRowsWithoutLookupMatching to set
	 */
	public final void setRemoveRowsWithoutLookupMatching(
			boolean removeRowsWithoutLookupMatching) {
		this.removeRowsWithoutLookupMatching = removeRowsWithoutLookupMatching;
	}

	/**
	 * @param removeRowsWithoutLookupMatching the removeRowsWithoutLookupMatching to set
	 */
	public final void setRemoveRowsWithoutLookupMatching(
			String removeRowsWithoutLookupMatching) {
		this.removeRowsWithoutLookupMatching = Boolean.parseBoolean(removeRowsWithoutLookupMatching);
	}


	/**
	 * @return the trashRowsWithoutLookupMatching
	 */
	public final boolean isTrashRowsWithoutLookupMatching() {
		return trashRowsWithoutLookupMatching;
	}




	/**
	 * @param trashRowsWithoutLookupMatching the trashRowsWithoutLookupMatching to set
	 */
	public final void setTrashRowsWithoutLookupMatching(
			boolean trashRowsWithoutLookupMatching) {
		this.trashRowsWithoutLookupMatching = trashRowsWithoutLookupMatching;
	}
	
	/**
	 * @param trashRowsWithoutLookupMatching the trashRowsWithoutLookupMatching to set
	 */
	public final void setTrashRowsWithoutLookupMatching(
			String trashRowsWithoutLookupMatching) {
		this.trashRowsWithoutLookupMatching = Boolean.parseBoolean(trashRowsWithoutLookupMatching);
	}
	@Override
	public String getAutoDocumentationDetails() {
		
		StringBuffer buf = new StringBuffer();
		buf.append(super.getAutoDocumentationDetails());
		if (getTrashTransformation() != null){
			buf.append("Trash Output : " + getTrashTransformation().getName() + "\n"); 
		}
		buf.append("Put in Trash Output rows without matching : " + trashRowsWithoutLookupMatching + "\n");
		buf.append("Remove rows without matching : " + removeRowsWithoutLookupMatching + "\n");
		return buf.toString();
	}
}
