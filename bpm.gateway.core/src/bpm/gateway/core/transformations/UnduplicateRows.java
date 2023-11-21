package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.unduplication.RunUnduplicate;
import bpm.vanilla.platform.core.IRepositoryContext;

public class UnduplicateRows extends SelectDistinct implements Trashable{

	private Transformation trashTransformation;
	private String trashName;
	
	private List<String> fieldsToTestNames = new ArrayList<String>();
	
	/*
	 * Only used by digester for old model
	 */
	private List<Integer> bufferFieldsToTest;
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	
	public void setTrashTransformation(Transformation t){
		if (t == null){
			trashTransformation = null;
		}
		if (getOutputs().contains(t)){
			trashTransformation = t;
		}
	}
	
	public Transformation getTrashTransformation(){
		if (trashName != null && getDocument() != null){
			trashTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashTransformation;
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public boolean isChecked(StreamElement e){
		
		for(String field : fieldsToTestNames){
			if(e.getFullName().equals(field)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Only used by digester for old model
	 * 
	 * @param index
	 */
	public void addFieldBuffer(String index){
		if(bufferFieldsToTest == null){
			bufferFieldsToTest = new ArrayList<Integer>();
		}
		
		try{
			bufferFieldsToTest.add(Integer.parseInt(index));
		}catch(NumberFormatException e){ }
	}
	
	public void addFieldName(String fieldName){
		fieldsToTestNames.add(fieldName);
	}
	
	public void addField(StreamElement e){
		for (String field : fieldsToTestNames){
			if (field.equals(e.getFullName())){
				return;
			}
		}
		
		fieldsToTestNames.add(e.getFullName());
	}
	
	
	
	public void removeField(StreamElement e){
		for (String field : fieldsToTestNames){
			if (field.equals(e.getFullName())){
				fieldsToTestNames.remove(field);
				return;
			}
		}
	}
	
	
	@Override
	public void refreshDescriptor() {
		super.refreshDescriptor();
		if(!isInited()){
			return;
		}
		else {
			if(bufferFieldsToTest != null){
				for(Integer i : bufferFieldsToTest){
					StreamElement element = null;
					try {
						element = descriptor.getStreamElements().get(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(element != null){
						fieldsToTestNames.add(element.getFullName());
					}
				}
				bufferFieldsToTest = null;
			}
			
			List<String> toRemove = new ArrayList<String>();
			for(String field : fieldsToTestNames){
				boolean found = false;
				for(StreamElement el : descriptor.getStreamElements()){
					if(el.getFullName().equals(field)){
						found = true;
						break;
					}
				}
				
				if (!found){
					toRemove.add(field);
				}
			}
			
			fieldsToTestNames.removeAll(toRemove);
		}
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("unduplicateRows");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (trashTransformation != null){
			e.addElement("trashRef").setText(trashTransformation.getName());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		for (String field : fieldsToTestNames){
			e.addElement("testedFieldName").setText(field);
		}
		
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunUnduplicate(this, bufferSize);
	}


	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		
		if (trashName != null && trashName.equals(stream.getName())){
			setTrashTransformation(stream);
			trashName = null;
		}
	}

	public Transformation copy() {
		UnduplicateRows copy = new UnduplicateRows();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	public List<Integer> getFields() {
		List<Integer> positionFields = new ArrayList<Integer>();
		for(String field : fieldsToTestNames){
			for(int i=0; i<descriptor.getStreamElements().size(); i++){
				if(descriptor.getStreamElements().get(i).getFullName().equals(field)){
					positionFields.add(i);
					break;
				}
			}
		}
		return positionFields;
	}
	
	@Override
	public String getAutoDocumentationDetails() {
		
		StringBuffer buf = new StringBuffer();
		buf.append(super.getAutoDocumentationDetails());
		
		buf.append("Fields Tested:");
		for(String field : fieldsToTestNames){
			buf.append("\t- " + field +  "\n");
		}
		return buf.toString();
	}
}
