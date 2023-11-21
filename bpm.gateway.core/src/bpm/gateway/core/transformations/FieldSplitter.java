package bpm.gateway.core.transformations;

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
import bpm.gateway.core.transformations.utils.Aggregate;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.spliter.RunFieldSpliter;
import bpm.vanilla.platform.core.IRepositoryContext;


/**
 * This class split a field value 
 * @author LCA
 *
 */
public class FieldSplitter extends AbstractTransformation {

	
	private List<SplitedField> splittedFields = new ArrayList<SplitedField>(); 
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fieldSpliter");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		for(SplitedField sf : splittedFields){
			e.add(sf.getElement());
		}
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new FieldSpliterRuntime(this, runtimeEngine);
//	}
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunFieldSpliter(this, bufferSize);
	}
	
	public List<SplitedField> getSplitedFields(){
		return new ArrayList<SplitedField>(splittedFields);
	}

	/**
	 * @return the StreamElement not provided by a split operation
	 */
	public List<StreamElement> getNonSplitedStreamElements() {
		List<StreamElement> l = new ArrayList<StreamElement>();
		
		for(StreamElement e : descriptor.getStreamElements()){
			boolean splited = false;
			for(SplitedField f : splittedFields){
				if (f.getStreamElements().contains(e)){
					splited = true;
					break;
				}
			}
			
			if (!splited){
				l.add(e);
			}
			
		}
		
		return l;
	}

	public SplitedField getSplitedFieldFor(StreamElement element) {
		for(SplitedField f : splittedFields){
			if (f.getSplited() == (element)){
				return f;
			}
		}
		return null;
	}
	
	public SplitedField getSpliterFieldFor(int indexField) {
		for(SplitedField f : splittedFields){
			if (f.containsIndex(indexField)){
				return f;
			}
		}
		return null;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		boolean b =  super.addInput(stream);
		
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	@Override
	public void refreshDescriptor() {
	
		try{
			descriptor = new DefaultStreamDescriptor();
			
			for(Transformation t : getInputs()){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
			
			
			
			/*
			 * refresh for splitted fields
			 */
			
			for(SplitedField s : splittedFields){
				/*
				 * look for the streamElement splited
				 * if not set, its index must be defined
				 */
				if (s.getSplited() == null ){
					s.setSplited(descriptor.getStreamElements().get(s.getSplitedIndex()));
				}
				else{
					for(StreamElement _e : descriptor.getStreamElements()){
						if (_e.equals(s.getSplited())){
							s.setSplited(_e);
							break;
						}
					}
				}
				
				if (s.getBufferedNames() != null){
					for(String n : s.getBufferedNames()){
						
						StreamElement se = s.getSplited().clone(this.getName(), this.getName());
						se.name = n;
						
						descriptor.addColumn(se);
						s.addField(descriptor.getStreamElements().size() -1);
					}
					s.destroyBuffer();
				}
				
				
				
			}
			
			
			for(Transformation  t : getOutputs()){
				t.refreshDescriptor();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

	public void addSpliter(SplitedField sf){
		splittedFields.add(sf);
		sf.setOwner(this);
	}
	
	
	public void addSpliter(String spliter, StreamElement se) {
		int i = getNonSplitedStreamElements().indexOf(se);
		
		if (i == -1){
			return;
		}
		
		SplitedField sf = new SplitedField();
		sf.setOwner(this);
		sf.setSpliter(spliter);
		sf.setSplited(se);
		splittedFields.add(sf);
	
//		for(SplitedField sf : splittedFields){
//			if (sf.getSplited() == se){
//				sf.ad
//			}
//		}
	}

	public List<SplitedField> getSpliterFor(StreamElement parentElement) {
		
		List<SplitedField> l = new ArrayList<SplitedField>();
		for(SplitedField sf : splittedFields){
			if (sf.getSplited() == parentElement){
				l.add(sf);
			}
		}
		return l;
		

	}

	public void addColumnFor(StreamElement newCol, SplitedField se) {
		descriptor.addColumn(newCol);
		
		se.addField(descriptor.getStreamElements().indexOf(newCol));
		
		
	}
	
	

	public void removeSpliter(SplitedField o) {
		boolean b = splittedFields.remove(o);
		if (b){
			refreshDescriptor();
		}
		
	}

	public void removeNewField(StreamElement o) {
		for(SplitedField f : splittedFields){
			if (f.getStreamElements().contains(o)){
				int i = f.removeColumn(o);
				if (i != -1){
					descriptor.removeColumn(i);
				}
				
				refreshDescriptor();
				break;
			}
		}
		
	}
	
	public boolean isNewField(int indexField){
		for(SplitedField f : splittedFields){
			if (f.containsIndex(indexField)){
				return true;
			}
		}
		return false;
	}
	
	
	public Transformation copy() {
		FieldSplitter copy = new FieldSplitter();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
	
		buf.append("Splits performed :\n");
		for(SplitedField sf : splittedFields){
			try{
				buf.append("\t- Field " + getInputs().get(0).getDescriptor(this).getColumnName(sf.getSplitedIndex()) + " splited on " + sf.getSpliter() + "\n");
			}catch(Exception ex){
				ex.printStackTrace();
				buf.append("\t- Field number " + sf.getSplitedIndex() + " splited on " + sf.getSpliter() + "\n");
			}
		}
		return buf.toString();
	}
}

