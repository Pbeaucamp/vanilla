package bpm.gateway.core.transformations.outputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunSubTransformationFinalStep;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SubTransformationFinalStep extends AbstractTransformation{
	private DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return desc;
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Selection Transformations can only have one Input");
		}
		boolean result = super.addInput(stream); 
		
		if (result == false){
			return result;
		}
		else{
			refreshDescriptor();
		}
		return result;
	}
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("subTransformationFinalStep");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		return e;

	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {

		return new RunSubTransformationFinalStep(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		desc = new DefaultStreamDescriptor();
		
		if (!getInputs().isEmpty()){
			
			try{
				for(StreamElement e : getInputs().get(0).getDescriptor(this).getStreamElements()){
					StreamElement n = e.clone(getName(), getInputs().get(0).getName());
					desc.addColumn(n);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		for(Transformation tr : getOutputs()){
			tr.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		SubTransformationFinalStep step = new SubTransformationFinalStep();
		step.setName("Copy of " + getName());
		return step;
	}
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		
		
		return buf.toString();
	}
}
