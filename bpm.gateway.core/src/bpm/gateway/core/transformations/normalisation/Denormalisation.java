package bpm.gateway.core.transformations.normalisation;

import java.util.ArrayList;
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
import bpm.gateway.runtime2.transformation.normalisation.RunDenormalize;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Denormalisation extends AbstractTransformation {

	
	private Integer inputKeyFieldIndex;
	private List<NormaliserField> fields = new ArrayList<NormaliserField>();
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private List<Integer> groupFieldIndex = new ArrayList<Integer>(); 
	

	public List<Integer> getGroupFieldIndex(){
		return new ArrayList<Integer>(groupFieldIndex);
	}
	
	public void addGroupFieldIndex(String s){
		try{
			groupFieldIndex.add(Integer.parseInt(s));
		}catch(NumberFormatException e){
			
		}
	}
	
	public void addGroupFieldindex(Integer i){
		if (i != null){
			for(Integer x : groupFieldIndex){
				if (x.intValue() == i.intValue()){
					return;
				}
			}
			groupFieldIndex.add(i);
			refreshDescriptor();
		}
	}
	
	public void removeGroupFieldindex(Integer i ){
		for(Integer x : groupFieldIndex){
			if (x.intValue() == i.intValue()){
				groupFieldIndex.remove(x);
				
				break;
			}
		}
		refreshDescriptor();
	}
	
	public List<NormaliserField> getFields(){
		return new ArrayList<NormaliserField>(fields);
	}
	
	public Integer getInputKeyField(){
		return inputKeyFieldIndex;
	}
	
	public void setInputKeyField(String s){
		try{
			inputKeyFieldIndex = Integer.parseInt(s);
		}catch(Exception e){
			
		}
		
	}
	
	public boolean setInputKeyField(StreamElement e){
		try{
			int i = getInputs().get(0).getDescriptor(this).getStreamElements().indexOf(e);
			
			if (i >= 0){
				inputKeyFieldIndex = i;
				return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return false;
		
	}
	
	public void addField(String fieldName, String inputFieldValueIndex, String value){
		NormaliserField field = new NormaliserField();
		field.setFieldName(fieldName);
		field.setInputFieldValueIndex(inputFieldValueIndex);
		field.setValue(value);
		fields.add(field);
		field.setOwner(this);
		
	}
	public void addField(NormaliserField field){
		fields.add(field);
		field.setOwner(this);
		refreshDescriptor();
	}
	
	public void removeField(NormaliserField field){
		fields.remove(field);
		refreshDescriptor();
	}
	
	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#addInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (!getInputs().contains(stream) && getInputs().size() > 0){
			throw new Exception ("support only one input field");
		}
		
		boolean b = super.addInput(stream);
		
		if (b){
			if (isInited()){
				int inputColumnCount = stream.getDescriptor(this).getColumnCount();
				if (inputKeyFieldIndex != null && inputKeyFieldIndex.intValue() >= inputColumnCount){
					inputKeyFieldIndex = null;
				}
				
				for(NormaliserField f : fields){
					if (f.getInputFieldValueIndex() != null && f.getInputFieldValueIndex().intValue() >= inputColumnCount){
						f.setInputFieldValueIndex(null);
					}
				}
			}
			
			refreshDescriptor();
		}
		
		return b;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("denormalize");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (inputKeyFieldIndex != null){
			e.addElement("inputKeyFieldIndex").setText(inputKeyFieldIndex + "");
		}
		
		for(Integer i : groupFieldIndex){
			e.addElement("pivotFieldFieldIndex").setText(i + "");
		}
		
		for(NormaliserField f : getFields()){
			e.add(f.getElement());
		}
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new DenormalizerRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDenormalize(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		descriptor = new DefaultStreamDescriptor();
		
		for(NormaliserField f : getFields()){
			
			try{
				if (!getInputs().isEmpty() && f.getInputFieldValueIndex() > getInputs().get(0).getDescriptor(this).getColumnCount()){
					f.setInputFieldValueIndex(null);
				}
			}catch(Exception e){
				
			}
			
			
			StreamElement e = new StreamElement();
			e.name = f.getFieldName();
			e.originTransfo = getName();
			try{
				StreamElement col = getInputs().get(0).getDescriptor(this).getStreamElements().get(f.getInputFieldValueIndex());
				
				e.typeName =  new String(col.typeName);
				e.className = new String(col.className);
			}catch(Exception ex){
				
			}
			
			
			descriptor.addColumn(e);
			
		}
		
		List<Integer> pivotToRemove = new ArrayList<Integer>();
		for(Integer i : groupFieldIndex){
			try{
				StreamElement e = getInputs().get(0).getDescriptor(this).getStreamElements().get(i);
				
				descriptor.addColumn(e.clone(getName(), getInputs().get(0).getName()));
				
			}catch(Exception ex){
				pivotToRemove.add(i);
			}
		}
		
		groupFieldIndex.removeAll(pivotToRemove);
		
		
		 try{
			 getInputs().get(0).getDescriptor(this).getStreamElements().get(inputKeyFieldIndex);
		 }catch(Exception e){
			 inputKeyFieldIndex = null;
		 }
		
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

	}

	public Transformation copy() {
		return null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}
