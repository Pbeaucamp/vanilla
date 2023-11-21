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
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunFilter;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Filter extends AbstractTransformation implements Trashable{


	private Transformation defaultTransformation;
	private String defaultName;
	
	private List<Condition> conditions = new ArrayList<Condition>();
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private boolean exclusive = false;
	
	

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
	
	public void setExclusive(String exclusive){
		this.exclusive = Boolean.parseBoolean(exclusive);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("filter");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("exclusive").setText("" + exclusive);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		for(Condition c : conditions){
			e.add(c.getElement());
		}
		
		if (defaultTransformation != null){
			e.addElement("trashRef").setText(defaultTransformation.getName());
		}
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunFilter(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
	
		try{
			descriptor = new DefaultStreamDescriptor();
			if (!isInited()){
				return;
			}
			for(Transformation t : getInputs()){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
			
			for(Condition c : conditions){
				if(c.getStreamElementNum() != null){
					StreamElement el = null;
					try {
						el = descriptor.getStreamElements().get(c.getStreamElementNum());
						c.setStreamElementNumberToNull();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(el != null){
						c.setStreamElementName(el.getFullName());
					}
				}
			}
			
			List<Condition> toRemove = new ArrayList<Condition>();
			
			for(Condition c : conditions){
				boolean found = false;
				for(StreamElement el : descriptor.getStreamElements()){
					if(el.getFullName().equals(c.getStreamElementName())){
						found = true;
						break;
					}
				}
				if (!found){
					toRemove.add(c);
				}
			}
			for(Condition c : toRemove){
				removeCondition(c);
			}

			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

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
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		conditions = new ArrayList<Condition>();
		refreshDescriptor();
	}

	@Override
	public void removeOutput(Transformation transfo) {
		for(Condition c : conditions){
			if (c.getOutput() == transfo){
				removeCondition(c);
				break;
			}
		}
		
		if (transfo == defaultTransformation){
			defaultTransformation = null;
		}
		super.removeOutput(transfo);
	}
	
	
	
	public void removeCondition(Condition c){
		conditions.remove(c);
		
		int numberForSameOutput = 0;  
		boolean getNonLogical = false;
		
		/*
		 * update the conditions
		 * if there is a condition on the same output and no NONE logical operand
		 * we update the first to remove its logical operand
		 * otherwise, there will be problems at runtime
		 */
		for(Condition cond : conditions){
			if (cond.getOutput() == c.getOutput()){
				numberForSameOutput++;
				if (cond.getLogical() == Condition.NONE){
					getNonLogical = true;
				}
			}
		}
		
		if (!getNonLogical && numberForSameOutput > 0){
			for(Condition cond : conditions){
				if (cond.getOutput() == c.getOutput()){
					cond.setLogical(Condition.NONE);
					break;
				}
			}
		}
	}
	
	
	public void addCondition(Condition c){

		conditions.add(c);
	}

	public List<Condition> getConditions() {
		return new ArrayList<Condition>(conditions);
	}

	
	
	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		
		
		/*
		 * set the conditions that arent set but defined from xml
		 */
		for(Condition c : conditions){
			if (c.getOutput() == null && c.getOutputRef() != null){
				if (c.getOutputRef().equals(stream.getName())){
					c.setOutput(stream);
				}
			}
		}
		
		
		if (defaultName != null && defaultName.equals(stream.getName())){
			setTrashTransformation(stream);
			defaultName = null;
		}
	}

	public Transformation getTrashTransformation() {
		if (defaultName != null && getDocument() != null){
			defaultTransformation = getDocument().getTransformation(defaultName);
			defaultName = null;
		}
		return defaultTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		if (transfo == null){
			defaultTransformation = null;
		}
		if (getOutputs().contains(transfo)){
			defaultTransformation = transfo;
		}
		
	}
	public void setTrashTransformation(String name){
		this.defaultName = name;
	}
	
	public Transformation copy() {
		Filter copy = new Filter();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	public void swapOutputs(Transformation t1, Transformation t2) {
		int index = outputs.indexOf(t2);
		outputs.set(outputs.indexOf(t1), t2);
		outputs.set(index, t1);
		
		
		
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append(" Filter exclusives : " + exclusive + "\n");
		if (getTrashTransformation() != null){
			buf.append(" Disbanded Row  : " + getTrashTransformation().getName() + "\n");
		}
		/*
		 * extract outputs
		 */
		List<Transformation> outputs = new ArrayList<Transformation>();
		
		for(Condition c : conditions){
			if (!outputs.contains(c.getOutput())){
				outputs.add(c.getOutput());
			}
		}
		
		/*
		 * dump outputs details
		 */
		
		for(Transformation t : outputs){
			buf.append("Output " + t.getName() + ":\n");
			
			for(Condition c : conditions){
				if (c.getOutput() == t){
					buf.append("\t- " + (c.getLogical() == Condition.NONE ? "" : Condition.LOGICALS[c.getLogical()]) + " " + c.getStreamElementName() + c.getOperator() + c.getValue() != null ? c.getValue() : "" + "\n");
				}
			}
			
			
		}
		
		return buf.toString();
	}

	public int getColumnNumberFromName(String streamElementName) {
		for(int i=0; i<descriptor.getStreamElements().size(); i++){
			if(descriptor.getStreamElements().get(i).getFullName().equals(streamElementName)){
				return i;
			}
		}
		return -1;
	}
	
}
