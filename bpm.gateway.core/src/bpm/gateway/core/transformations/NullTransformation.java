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
import bpm.gateway.core.transformations.utils.ConditionNull;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunNullTransformation;
import bpm.vanilla.platform.core.IRepositoryContext;

public class NullTransformation extends AbstractTransformation{

	private List<ConditionNull> conditions = new ArrayList<ConditionNull>();
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean b = super.addInput(stream);
		if(b){
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
		Element e = DocumentHelper.createElement("nullTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
			for(ConditionNull c : conditions){
				e.add(c.getElement());
			}
		
			if (descriptor != null){
				e.add(descriptor.getElement());
			}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx,	int bufferSize) {
		return new RunNullTransformation(this,  bufferSize);
	}

	public void addCondition(ConditionNull c){
		conditions.add(c);
		
//		refreshDescriptor();
	}
	
	public List<ConditionNull> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionNull> conditions) {
		this.conditions = conditions;
	}

	public void removeCondition(ConditionNull c){
		boolean b = conditions.remove(c);
		
		int numberForSameOutput = 0;  
		boolean getNonLogical = false;
		
		/*
		 * update the conditions
		 * if there is a condition on the same output and no NONE logical operand
		 * we update the first to remove its logical operand
		 * otherwise, there will be problems at runtime
		 */
		for(ConditionNull cond : conditions){
			if (cond.getOutput() == c.getOutput()){
				numberForSameOutput++;
			}
		}
		
		if (!getNonLogical && numberForSameOutput > 0){
			for(ConditionNull cond : conditions){
				if (cond.getOutput() == c.getOutput()){
//					cond.setLogical(Condition.NONE);
					break;
				}
			}
		}
		
//		if (b){
//			refreshDescriptor();
//		}
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
			
			for(ConditionNull c : conditions){
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
			
//			List<ConditionNull> toRemove = new ArrayList<ConditionNull>();
//			
//			for(ConditionNull c : conditions){
//				boolean found = false;
//				for(StreamElement el : descriptor.getStreamElements()){
//					if(el.getFullName().endsWith(c.getStreamElementName())){
//						found = true;
//						break;
//					}
//				}
//				if (!found){
//					toRemove.add(c);
//				}
//			}
//			for(ConditionNull c : toRemove){
//				removeCondition(c);
//			}

			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}		
	}
	
	@Override
	public Transformation copy() {
		NullTransformation nullTransfo = new NullTransformation();
		nullTransfo.setDescription(description);
		nullTransfo.setName("copy of " + getName());
		return nullTransfo;
	}

	@Override
	public String getAutoDocumentationDetails() {
		
		return null;
	}

}
