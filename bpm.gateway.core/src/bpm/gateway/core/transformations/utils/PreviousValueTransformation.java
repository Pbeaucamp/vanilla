package bpm.gateway.core.transformations.utils;

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
import bpm.gateway.runtime2.transformation.selection.RunPreviousValue;
import bpm.vanilla.platform.core.IRepositoryContext;

public class PreviousValueTransformation extends AbstractTransformation {

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private List<String> keyElements = new ArrayList<String>();
	private List<String> previousElements = new ArrayList<String>();
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		return super.addInput(stream);
	}
	
	@Override
	public Transformation copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if(descriptor == null || descriptor.getStreamElements().size() == 0) {
			refreshDescriptor();
		}
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("previousValueTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		for(String columns : keyElements){
			e.addElement("key").setText(columns);
		}
		for(String columns : previousElements){
			e.addElement("previous").setText(columns);
		}
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunPreviousValue(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		try {
			descriptor = new DefaultStreamDescriptor();
			
			for(Transformation t : getInputs()) {
				for(StreamElement elem : t.getDescriptor(this).getStreamElements()) {
					descriptor.addColumn(elem.clone(getName(), t.getName()));
				}
			}
			for(String s : previousElements) {
				StreamElement elem = new StreamElement();
				for(StreamElement e : descriptor.getStreamElements()) {
					if(e.name.equals(s)) {
						elem.name = s + "_previousvalue";
						elem.className = e.className;
						elem.originTransfo = this.name;
						elem.transfoName = this.name;
						elem.type = e.type;
						elem.typeName = e.typeName;
						break;
					}
				}
				descriptor.addColumn(elem);
				
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	public void addKey(String key) {
		for(String s : keyElements) {
			if(s.equals(key)) {
				return;
			}
		}
		keyElements.add(key);
		refreshDescriptor();
	}

	public void removeKey(String key) {
		keyElements.remove(key);
		refreshDescriptor();
	}
	
	public void addPrevious(String previous) {
		for(String s : previousElements) {
			if(s.equals(previous)) {
				return;
			}
		}
		previousElements.add(previous);
		refreshDescriptor();
	}
	
	public void removePrevious(String previous) {
		previousElements.remove(previous);
		refreshDescriptor();
	}
	
	public void addKeyDigester(String key) {
		keyElements.add(key);
	}
	
	public void addPreviousDigester(String previous) {
		previousElements.add(previous);
	}
	
	public List<StreamElement> getKeyElements(List<StreamElement> input) {
		List<StreamElement> keys = new ArrayList<StreamElement>();
		for(String s : keyElements) {
			for(StreamElement elem : input) {
				if(s.equals(elem.name)) {
					keys.add(elem);
					break;
				}
			}
		}
		
		return keys;
	}
	
	public List<StreamElement> getPreviousElements(List<StreamElement> input) {
		List<StreamElement> previous = new ArrayList<StreamElement>();
		for(String s : previousElements) {
			for(StreamElement elem : input) {
				if(s.equals(elem.name)) {
					previous.add(elem);
					break;
				}
			}
		}
		
		return previous;
	}
	
	public StreamDescriptor getOriginalDescriptor() throws ServerException {
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		for(Transformation t : getInputs()) {
			for(StreamElement elem : t.getDescriptor(this).getStreamElements()) {
				desc.addColumn(elem.clone(getName(), t.getName()));
			}
		}
		return desc;
	}

	public List<Integer> getKeyIndexes() {
		List<Integer> indexes = new ArrayList<Integer>();
		for(String s : keyElements) {
			for(int i = 0 ; i < descriptor.getColumnCount() ; i++) {
				if(descriptor.getStreamElements().get(i).name.equals(s)) {
					indexes.add(i);
					break;
				}
			}
		}
		return indexes;
	}

	public List<Integer> getPreviousIndexes() {
		List<Integer> indexes = new ArrayList<Integer>();
		for(String s : previousElements) {
			for(int i = 0 ; i < descriptor.getColumnCount() ; i++) {
				if(descriptor.getStreamElements().get(i).name.equals(s)) {
					indexes.add(i);
					break;
				}
			}
		}
		return indexes;
	}
}
