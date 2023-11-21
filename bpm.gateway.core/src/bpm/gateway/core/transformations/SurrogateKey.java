package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.Collections;
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
import bpm.gateway.runtime2.transformation.sequence.RunSurrogateKey;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SurrogateKey extends AbstractTransformation {
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private String fieldName = "id";

	private List<String> fieldKeyNames = new ArrayList<String>();

	/*
	 * Only used by the digester
	 */
	private List<Integer> bufferFieldKeyIndex;
	
	public SurrogateKey() {
		super();
		setFieldName("surrogateKey_" + new Object().hashCode());
	}
	
	public String getFieldName() {
		return fieldName;
	}

	
	 @Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean b =  super.addInput(stream);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		refreshDescriptor();
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#removeInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		fieldKeyNames.clear();
	}
	
	
	public void addFieldKeyIndex(StreamElement field){	
		for(String key : fieldKeyNames){
			if (key.equals(field.getFullName())){
				return ;
			}
		}
		fieldKeyNames.add(field.getFullName());
		
		Collections.sort(fieldKeyNames);
	}
	
	/**
	 * Only used by digester for old model
	 * @param fieldNumber
	 */
	public void addFieldKeyIndex(String fieldNumber){
		if(bufferFieldKeyIndex == null){
			bufferFieldKeyIndex = new ArrayList<Integer>();
		}
		
		try{
			Integer i = Integer.parseInt(fieldNumber);
			for(Integer k : bufferFieldKeyIndex){
				if (k.equals(i)){
					return ;
				}
			}
			bufferFieldKeyIndex.add(i);
			Collections.sort(bufferFieldKeyIndex);
		} catch(Exception ex){ }
	}
	
	/**
	 * Only used by digester
	 * @param keyElement
	 */
	public void addFieldKeyName(String keyElement){
		fieldKeyNames.add(keyElement);
	}
	
	public void removeFieldKeyIndex(StreamElement field){
		try{
			for(String key : fieldKeyNames){
				if(field.getFullName().equals(key)){
					fieldKeyNames.remove(key);
					break;
				}
			}
			
			Collections.sort(fieldKeyNames);
		}catch(Exception ex){
			
		}
	}
	

	public List<Integer> getFieldKeys(){
		List<Integer> keyPositions = new ArrayList<Integer>();
		
		for(String key : fieldKeyNames){
			try {
				List<StreamElement> elements = inputs.get(0).getDescriptor(this).getStreamElements();
				for(int i=0; i<elements.size(); i++){
					if(elements.get(i).getFullName().equals(key)){
						keyPositions.add(i);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return keyPositions;
	}
	
	public List<StreamElement> getFields(){
		List<StreamElement> l = new ArrayList<StreamElement>();
		
		try{
			List<StreamElement> f = getInputs().get(0).getDescriptor(this).getStreamElements();
			for(String key : fieldKeyNames){
				for(StreamElement el : f){
					if(el.getFullName().equals(key)){
						l.add(el);
						break;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return l;
	}

	
	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.Sequence#getElement()
	 */
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("surrogateKey");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		

		e.addElement("fieldName").setText(fieldName);
		
		
		for(String key : fieldKeyNames){
			e.addElement("keyName").setText(key);
		}
		return e;
	}

	public void removeFieldKeyIndexAll() {
		fieldKeyNames.clear();
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.Sequence#getExecutioner(int)
	 */
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSurrogateKey(this, bufferSize);
	}

//	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Fields Used to generate surrogate key:");
		for(String key : fieldKeyNames){
			buf.append("\t- " + key +  "\n");
		}
		return buf.toString();
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public void refreshDescriptor() {
		try{
			descriptor = new DefaultStreamDescriptor();
			if (!isInited()){
				return;
			}
			StreamElement col = new StreamElement();
			col.name = fieldName;
			col.className = "java.lang.String";
			col.originTransfo = this.getName();
			col.transfoName = this.getName();
			col.typeName = "STRING";
			
			
			
			for(Transformation t : getInputs()){
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
			
			descriptor.addColumn(col);
			
			if(bufferFieldKeyIndex != null){
				for(Integer i : bufferFieldKeyIndex){
					StreamElement element = null;
					try {
						element = inputs.get(0).getDescriptor(this).getStreamElements().get(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(element != null){
						fieldKeyNames.add(element.getFullName());
					}
				}
				
				bufferFieldKeyIndex = null;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		SurrogateKey seq = new SurrogateKey();
		seq.setFieldName(getFieldName());
		seq.setName("copy of " + getName());
		return seq;
	}
}
